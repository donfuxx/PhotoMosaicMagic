package com.appham.mosaicmagic.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.appham.mosaicmagic.BaseActivity;
import com.appham.mosaicmagic.model.SlicedBitmap;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public interface MosaicGenerator {

    void loadImage(@NonNull Uri data);

    void sliceImage(@NonNull Bitmap bitmap, int tileWidth, int tileHeight);

    void calcAverageColors(@NonNull SlicedBitmap slicedBitmap);

    void updateMosaic(@NonNull SlicedBitmap slicedBitmap);

    BaseActivity getBaseActivity();

    int getDisplayW();

    int getDisplayH();

    void onDestroy();
}
