package com.appham.mosaicmagic.async;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.appham.mosaicmagic.BitmapUtils;
import com.appham.mosaicmagic.R;
import com.appham.mosaicmagic.presenter.MosaicGenerator;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class LoadImgTask extends PooledTask<Uri, Void, Bitmap> {

    private static final String TAG = "LoadImgTask";
    private final MosaicGenerator mosaicGenerator;
    private final int tileWidth;
    private final int tileHeight;

    public LoadImgTask(@NonNull MosaicGenerator mosaicGenerator) {
        this.mosaicGenerator = mosaicGenerator;
        this.tileWidth = mosaicGenerator.getBaseActivity().getPrefs().getTileWidth();
        this.tileHeight = mosaicGenerator.getBaseActivity().getPrefs().getTileHeight();
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mosaicGenerator.getBaseActivity(), R.string.loading_image,
                Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    protected Bitmap doInBackground(Uri... uris) {
        try {

            // get sampled bitmap
            Bitmap bitmap = BitmapUtils.decodeSampledStream(uris[0],
                    mosaicGenerator.getBaseActivity().getContentResolver(),
                    mosaicGenerator.getDisplayW(), mosaicGenerator.getDisplayH());

            // scale to fit tile sizes if needed
            if (bitmap != null) {
                return BitmapUtils.scaleForTileSize(bitmap, tileWidth, tileHeight);
            }

        } catch (Exception e) {
            Log.e(TAG, "Decoding Bitmap Error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(@Nullable Bitmap bitmap) {

        if (bitmap == null) {
            Toast.makeText(mosaicGenerator.getBaseActivity(), R.string.image_not_loaded,
                    Toast.LENGTH_LONG).show();
            return;
        }

        mosaicGenerator.sliceImage(bitmap, tileWidth, tileHeight);


    }
}
