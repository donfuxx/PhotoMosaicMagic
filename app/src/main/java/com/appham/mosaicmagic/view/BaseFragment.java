package com.appham.mosaicmagic.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.appham.mosaicmagic.BaseActivity;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class BaseFragment extends Fragment {

    private BaseActivity baseActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initBaseActivity();

        super.onViewCreated(view, savedInstanceState);
    }

    public void initBaseActivity() {
        baseActivity = (BaseActivity) getActivity();
    }

    public BaseActivity getBaseActivity() {
        if (baseActivity == null) {
            initBaseActivity();
        }
        return baseActivity;
    }

}
