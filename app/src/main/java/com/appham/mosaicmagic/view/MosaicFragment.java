package com.appham.mosaicmagic.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.appham.mosaicmagic.R;
import com.appham.mosaicmagic.presenter.MosaicPresenter;

/**
 * The main fragment with the mosaic image view
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class MosaicFragment extends BaseFragment implements MosaicView {

    public static final String TAG = "MosaicFragment";
    private ImageView imgMosaic;
    private Button btnLoadImg;
    private int displayW;
    private int displayH;
    private Bitmap mosaicBitmap;
    private MosaicPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mosaic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.setRetainInstance(true);

        // get screen dimensions in pixels
        displayW = getResources().getDisplayMetrics().widthPixels;
        displayH = getResources().getDisplayMetrics().heightPixels;

        // init the main mosaic image view
        imgMosaic = (ImageView) view.findViewById(R.id.imgMosaic);
        mosaicBitmap = Bitmap.createBitmap(displayW, displayH,
                Bitmap.Config.ARGB_8888);
        imgMosaic.setImageBitmap(mosaicBitmap);
        imgMosaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLoadImgButtonVisibility();
            }
        });

        // init image loading button
        btnLoadImg = (Button) view.findViewById(R.id.btnLoadImg);
        btnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        btnLoadImg.setVisibility(View.GONE);
    }

    @Override
    public void showLoadImgButton() {
        btnLoadImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void toggleLoadImgButtonVisibility() {
        if (btnLoadImg.getVisibility() == View.VISIBLE) {
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
