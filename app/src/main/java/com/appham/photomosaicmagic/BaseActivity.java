package com.appham.photomosaicmagic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.appham.photomosaicmagic.view.MosaicFragment;
import com.appham.photomosaicmagic.view.SettingsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static final int IMG_REQUEST_CODE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
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
                mosaicFragment.hideButtons();
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

            mosaicFragment.showButtons();
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

    public void downloadImg(@NonNull Bitmap bitmap) {

        // check for write storage permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            return;
        }

        Observable.fromCallable(() -> {
            String folderName = getString(R.string.app_name).replaceAll("\\s+", "_");
            File path = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
            path.mkdirs();

            String filename = folderName + "_" + bitmap.getByteCount() + ".jpg";
            File file = new File(path, filename);
            FileOutputStream stream;

            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] bytes = byteBuffer.array();

            stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();

            return folderName;

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((folderName) -> {
                            Toast.makeText(this,
                                    getString(R.string.image_downloaded) + " " + folderName,
                                    Toast.LENGTH_LONG).show();
                        },
                        Throwable::printStackTrace);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, download img!
                    Bitmap bitmap = mosaicFragment.getPresenter().getMosaicBitmap();
                    if (bitmap != null) {
                        downloadImg(bitmap);
                    }

                } else {
                    Toast.makeText(this, R.string.grant_write_permission_first,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public Prefs getPrefs() {
        return prefs;
    }
}
