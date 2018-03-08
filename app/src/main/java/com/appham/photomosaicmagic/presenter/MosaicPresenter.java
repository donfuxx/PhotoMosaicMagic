package com.appham.photomosaicmagic.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.appham.photomosaicmagic.BaseActivity;
import com.appham.photomosaicmagic.BitmapUtils;
import com.appham.photomosaicmagic.async.BitmapSliceTask;
import com.appham.photomosaicmagic.async.CalcColorTask;
import com.appham.photomosaicmagic.async.LoadImgTask;
import com.appham.photomosaicmagic.model.SlicedBitmap;
import com.appham.photomosaicmagic.view.MosaicView;

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
        new LoadImgTask(this).executePool(data);
    }

    /**
     * 2. Step: slice the image bitmap into tiles
     */
    @Override
    public void sliceImage(@NonNull Bitmap bitmap, int tileWidth, int tileHeight) {
        new BitmapSliceTask(this).executePool(new SlicedBitmap(bitmap, tileWidth, tileHeight,
                getBaseActivity().getPrefs().getTileType()));

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
