package com.appham.photomosaicmagic.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private int displayW, displayH;
    private MosaicPresenter presenter;
    private LinearLayout layButtons;

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

        // init mosaic presenter
        this.presenter = new MosaicPresenter(this);

        // init the main mosaic image view
        imgMosaic = view.findViewById(R.id.imgMosaic);
        Bitmap mosaicBitmap = Bitmap.createBitmap(displayW, displayH,
                Bitmap.Config.ARGB_8888);
        imgMosaic.setImageBitmap(mosaicBitmap);
        imgMosaic.setOnClickListener(view13 -> toggleButtonVisibility());

        // init buttons
        Button btnLoadImg = view.findViewById(R.id.btnLoadImg);
        btnLoadImg.setOnClickListener(view1 -> {
            getBaseActivity().selectImg();
            hideButtons();
        });
        Button btnDownload = view.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(view2 -> {
            if (presenter.getMosaicBitmap() == null) {
                Toast.makeText(getBaseActivity(), R.string.load_image_first, Toast.LENGTH_LONG).show();
                return;
            }
            getBaseActivity().downloadImg(presenter.getMosaicBitmap());
            hideButtons();
        });
        layButtons = view.findViewById(R.id.layButtons);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void hideButtons() {
        layButtons.setVisibility(android.view.View.GONE);
    }

    @Override
    public void showButtons() {
        layButtons.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void toggleButtonVisibility() {
        if (layButtons.getVisibility() == android.view.View.VISIBLE) {
            hideButtons();
        } else {
            showButtons();
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
