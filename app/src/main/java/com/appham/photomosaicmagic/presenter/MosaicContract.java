package com.appham.photomosaicmagic.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.appham.photomosaicmagic.BaseActivity;
import com.appham.photomosaicmagic.model.SlicedBitmap;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public interface MosaicContract {

    interface Model {

        void draw(Canvas canvas, RectF rectF, Paint paint);
    }

    interface View {

        void hideButtons();

        void showButtons();

        void toggleButtonVisibility();

        void setMosaicBitmap(Bitmap bitmap);

        int getDisplayW();

        int getDisplayH();

        BaseActivity getBaseActivity();

    }

    interface Presenter {

        void loadImage(@NonNull Uri data);

        void sliceImage(@NonNull Bitmap bitmap, int tileWidth, int tileHeight, int tilePadding);

        void calcAverageColors(@NonNull SlicedBitmap slicedBitmap);

        void updateMosaic(@NonNull SlicedBitmap slicedBitmap);

        BaseActivity getBaseActivity();

        int getDisplayW();

        int getDisplayH();

        Bitmap getMosaicBitmap();

        void onDestroy();
    }

}
