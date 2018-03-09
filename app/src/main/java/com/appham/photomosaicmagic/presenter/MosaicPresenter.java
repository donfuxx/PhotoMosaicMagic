package com.appham.photomosaicmagic.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.appham.photomosaicmagic.BaseActivity;
import com.appham.photomosaicmagic.BitmapUtils;
import com.appham.photomosaicmagic.R;
import com.appham.photomosaicmagic.model.SlicedBitmap;
import com.appham.photomosaicmagic.model.Tile;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * The "middle man" between the mosaic view and the mosaic models.
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public class MosaicPresenter implements MosaicContract.Presenter {

    private MosaicContract.View view;
    private final BaseActivity baseActivity;
    private Bitmap mosaicBitmap;

    public MosaicPresenter(@NonNull MosaicContract.View view) {
        this.view = view;
        this.baseActivity = view.getBaseActivity();
    }

    /**
     * 1. Step: load the image from user device asynchronously
     */
    @Override
    public void loadImage(@NonNull Uri data) {
        int tileWidth = baseActivity.getPrefs().getTileWidth();
        int tileHeight = baseActivity.getPrefs().getTileHeight();

        Observable.fromCallable(() -> {

            try {

                // get sampled bitmap
                Bitmap bitmap = BitmapUtils.decodeSampledStream(data,
                        baseActivity.getContentResolver(), getDisplayW(), getDisplayH());

                // scale to fit tile sizes if needed
                if (bitmap != null) {
                    return BitmapUtils.scaleForTileSize(bitmap, tileWidth, tileHeight);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((bitmap) -> {
                    if (bitmap == null) {
                        Toast.makeText(baseActivity, R.string.image_not_loaded,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    sliceImage(bitmap, tileWidth, tileHeight);
                });
    }

    /**
     * 2. Step: slice the image bitmap into tiles
     */
    @Override
    public void sliceImage(@NonNull Bitmap bitmap, int tileWidth, int tileHeight) {

        Observable.fromCallable(() -> {
            SlicedBitmap slicedBitmap = new SlicedBitmap(bitmap, tileWidth, tileHeight,
                    baseActivity.getPrefs().getTileType());

            slicedBitmap.sliceNextRow();

            return slicedBitmap;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::calcAverageColors);

        // set original image as the mosaic image
        mosaicBitmap = bitmap.copy(bitmap.getConfig(), true);
        view.setMosaicBitmap(mosaicBitmap);
    }

    /**
     * 3. Step: for each tile of the last row calculate the average color
     */
    @Override
    public void calcAverageColors(@NonNull SlicedBitmap slicedBitmap) {
        Observable.fromCallable(() -> {
            slicedBitmap.resetLoadedDotsRowCount();

            for (Tile tile : slicedBitmap.getLastRow()) {

                int color = BitmapUtils.calcAverageColor(tile.getBitmap());

                String colorCode = String.format(Locale.ROOT, "%06X",
                        (0xFFFFFF & color));

                tile.setAvgColorInt(color);

                tile.setAvgColor(colorCode);

                BitmapUtils.addTileDotBitmap(tile);

                slicedBitmap.incrLoadedDotsRowCount();

            }
            return slicedBitmap;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((resultSlicedBitmap) -> {
                    // update image once all tiles in the current row have a dot loaded
                    if (resultSlicedBitmap.getLoadedDotsRowCount() == resultSlicedBitmap.getCols()) {
                        updateMosaic(resultSlicedBitmap);

                        // calculate colors for the next row
                        if (resultSlicedBitmap.getDrawnRowsCount() < resultSlicedBitmap.getRows()) {
                            calcAverageColors(resultSlicedBitmap);
                        } else { // if no more rows recycle the bitmap
                            resultSlicedBitmap.getBitmap().recycle();
                        }
                    }
                });
    }

    /**
     * 4. Step: Update the mosaic image with the latest row of dots
     */
    @Override
    public void updateMosaic(@NonNull SlicedBitmap slicedBitmap) {

        BitmapUtils.drawMosaicOnBitmap(mosaicBitmap, slicedBitmap);

        if (view != null) {
            view.setMosaicBitmap(mosaicBitmap);
        }
    }

    @Override
    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    @Override
    public int getDisplayW() {
        return view.getDisplayW();
    }

    @Override
    public int getDisplayH() {
        return view.getDisplayH();
    }

    @Override
    public void onDestroy() {
        if (mosaicBitmap != null && !mosaicBitmap.isRecycled()) {
            mosaicBitmap.recycle();
        }
        view = null;
    }
}
