package com.appham.photomosaicmagic.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.appham.photomosaicmagic.BaseActivity;
import com.appham.photomosaicmagic.BitmapUtils;
import com.appham.photomosaicmagic.R;
import com.appham.photomosaicmagic.async.CalcColorTask;
import com.appham.photomosaicmagic.model.SlicedBitmap;
import com.appham.photomosaicmagic.view.MosaicView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * The "middle man" between the mosaic view and the mosaic models.
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public class MosaicPresenter implements MosaicGenerator {

    private MosaicView mosaicView;
    private final BaseActivity baseActivity;
    private Bitmap mosaicBitmap;

    public MosaicPresenter(@NonNull MosaicView mosaicView) {
        this.mosaicView = mosaicView;
        this.baseActivity = mosaicView.getBaseActivity();
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
        mosaicView.setMosaicBitmap(mosaicBitmap);
    }

    /**
     * 3. Step: for each tile of the last row calculate the average color
     */
    @Override
    public void calcAverageColors(@NonNull SlicedBitmap slicedBitmap) {
        new CalcColorTask(this).executePool(slicedBitmap);
    }

    /**
     * 4. Step: Update the mosaic image with the latest row of dots
     */
    @Override
    public void updateMosaic(@NonNull SlicedBitmap slicedBitmap) {

        BitmapUtils.drawMosaicOnBitmap(mosaicBitmap, slicedBitmap);

        if (mosaicView != null) {
            mosaicView.setMosaicBitmap(mosaicBitmap);
        }
    }

    @Override
    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    @Override
    public int getDisplayW() {
        return mosaicView.getDisplayW();
    }

    @Override
    public int getDisplayH() {
        return mosaicView.getDisplayH();
    }

    @Override
    public void onDestroy() {
        if (mosaicBitmap != null && !mosaicBitmap.isRecycled()) {
            mosaicBitmap.recycle();
        }
        mosaicView = null;
    }
}
