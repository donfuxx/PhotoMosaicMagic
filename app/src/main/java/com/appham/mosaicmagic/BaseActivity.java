package com.appham.mosaicmagic;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.appham.mosaicmagic.view.MosaicFragment;
import com.appham.mosaicmagic.view.SettingsFragment;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static final int IMG_REQUEST_CODE = 1;
    private Prefs prefs;
    private MosaicFragment mosaicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // init shared preferences
        prefs = new Prefs(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

        // add Mosaic UI as a fragment
        this.mosaicFragment = new MosaicFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.frameMosaic, mosaicFragment, MosaicFragment.TAG)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // User chose the settings, show the app preferences screen
            case R.id.action_settings:

                showSettingsFragment();
                return true;

            // User clicked the load image icon
            case R.id.action_load_image:

                showMosaicFragment();

                // show select image chooser
                mosaicFragment.hideLoadImgButton();
                selectImg();
                return true;

            // user selected nothing
            default:

                return super.onOptionsItemSelected(item);

        }
    }

    private void showSettingsFragment() {
        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager()
                .findFragmentByTag(SettingsFragment.TAG);

        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }

        // show settings if not currently shown
        if (!settingsFragment.isAdded()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frameMosaic, settingsFragment, SettingsFragment.TAG)
                    .commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    private void showMosaicFragment() {
        mosaicFragment = (MosaicFragment) getFragmentManager()
                .findFragmentByTag(MosaicFragment.TAG);

        if (mosaicFragment == null) {
            mosaicFragment = new MosaicFragment();
        }

        // show mosaic fragment if not currently shown
        if (!mosaicFragment.isAdded()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frameMosaic, mosaicFragment, MosaicFragment.TAG)
                    .commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMG_REQUEST_CODE) {

            mosaicFragment.getPresenter().loadImage(data.getData());

        } else {
            Log.i(TAG, getString(R.string.no_image_chosen));
            Toast.makeText(getApplicationContext(), R.string.no_image_chosen,
                    Toast.LENGTH_LONG).show();

            mosaicFragment.showLoadImgButton();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        prefs.save();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mosaicFragment == null || !mosaicFragment.isAdded()) {
            showMosaicFragment();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Opens a chooser to let user select the image from his device
     */
    public void selectImg() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_image)), IMG_REQUEST_CODE);

        Toast.makeText(getApplicationContext(), getString(R.string.select_image),
                Toast.LENGTH_SHORT).show();

    }

    public Prefs getPrefs() {
        return prefs;
    }
}
