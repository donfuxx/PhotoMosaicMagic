package com.appham.mosaicmagic.view;

import android.graphics.Bitmap;

import com.appham.mosaicmagic.BaseActivity;

/**
 * @author thomas
 */

public interface MosaicView {

    void hideLoadImgButton();

    void showLoadImgButton();

    void toggleLoadImgButtonVisibility();

    void setMosaicBitmap(Bitmap bitmap);

    int getDisplayW();

    int getDisplayH();

    BaseActivity getBaseActivity();
}
