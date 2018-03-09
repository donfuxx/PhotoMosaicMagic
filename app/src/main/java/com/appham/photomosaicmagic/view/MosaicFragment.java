package com.appham.photomosaicmagic.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.appham.photomosaicmagic.R;
import com.appham.photomosaicmagic.presenter.MosaicContract;
import com.appham.photomosaicmagic.presenter.MosaicPresenter;

/**
 * The main fragment with the mosaic image view
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class MosaicFragment extends BaseFragment implements MosaicContract.View {

    public static final String TAG = "MosaicFragment";
    private ImageView imgMosaic;
    private Button btnLoadImg;
    private int displayW;
    private int displayH;
    private MosaicPresenter presenter;

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mosaic, container, false);
    }

    @Override
    public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {

        this.setRetainInstance(true);

        // get screen dimensions in pixels
        displayW = getResources().getDisplayMetrics().widthPixels;
        displayH = getResources().getDisplayMetrics().heightPixels;

        // init the main mosaic image view
        imgMosaic = view.findViewById(R.id.imgMosaic);
        Bitmap mosaicBitmap = Bitmap.createBitmap(displayW, displayH,
                Bitmap.Config.ARGB_8888);
        imgMosaic.setImageBitmap(mosaicBitmap);
        imgMosaic.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                toggleLoadImgButtonVisibility();
            }
        });

        // init image loading button
        btnLoadImg = view.findViewById(R.id.btnLoadImg);
        btnLoadImg.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                getBaseActivity().selectImg();
                hideLoadImgButton();
            }
        });

        // init mosaic presenter
        this.presenter = new MosaicPresenter(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void hideLoadImgButton() {
        btnLoadImg.setVisibility(android.view.View.GONE);
    }

    @Override
    public void showLoadImgButton() {
        btnLoadImg.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void toggleLoadImgButtonVisibility() {
        if (btnLoadImg.getVisibility() == android.view.View.VISIBLE) {
            hideLoadImgButton();
        } else {
            showLoadImgButton();
        }
    }

    @Override
    public void setMosaicBitmap(Bitmap bitmap) {
        imgMosaic.setImageBitmap(bitmap);
    }

    @Override
    public int getDisplayW() {
        return displayW;
    }

    @Override
    public int getDisplayH() {
        return displayH;
    }

    public MosaicPresenter getPresenter() {
        return presenter;
    }
}
