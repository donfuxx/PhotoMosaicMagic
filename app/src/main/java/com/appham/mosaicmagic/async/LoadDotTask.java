package com.appham.mosaicmagic.async;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.appham.mosaicmagic.HttpUtils;
import com.appham.mosaicmagic.R;
import com.appham.mosaicmagic.model.SlicedBitmap;
import com.appham.mosaicmagic.model.Tile;
import com.appham.mosaicmagic.presenter.MosaicGenerator;

import java.net.HttpURLConnection;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public class LoadDotTask extends PooledTask<SlicedBitmap, Void, SlicedBitmap> {

    private static final String TAG = "LoadDotTask";
    private final MosaicGenerator mosaicGenerator;
    private final Tile tile;

    public LoadDotTask(@NonNull MosaicGenerator mosaicGenerator, @NonNull Tile tile) {
        this.mosaicGenerator = mosaicGenerator;
        this.tile = tile;
    }

    @NonNull
    @Override
    protected SlicedBitmap doInBackground(SlicedBitmap... slicedBitmaps) {

        SlicedBitmap slicedBitmap = slicedBitmaps[0];

        // try to get the dot image from the server
        HttpURLConnection connection = null;
        try {

            connection = HttpUtils.getHttpConnection(
                    HttpUtils.getServerUrl(mosaicGenerator.getBaseActivity().getString(R.string.server) + "/color/",
                            slicedBitmap.getTileWidth(), slicedBitmap.getTileHeight(),
                            tile.getAvgColor()));

            tile.setDot(BitmapFactory.decodeStream(connection.getInputStream()));

        } catch (Exception e) {
            String cancelReason = e + ": " + e.getMessage();
            Log.e(TAG, cancelReason);
            slicedBitmap.setCancelReason(cancelReason);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return slicedBitmap;
    }

    @Override
    protected void onPostExecute(@NonNull SlicedBitmap slicedBitmap) {

        // show message to user in case loading dot didn't succeed
        if (slicedBitmap.isCancelled()) {

            if (!slicedBitmap.isCancelReasonWasShown()) {
                Toast.makeText(mosaicGenerator.getBaseActivity(), slicedBitmap.getCancelReason(),
                        Toast.LENGTH_LONG).show();
                slicedBitmap.setCancelReasonWasShown(true);
            }
            return;
        }

        slicedBitmap.incrLoadedDotsRowCount();

        // update image once all tiles in the current row have a dot loaded
        if (slicedBitmap.getLoadedDotsRowCount() == slicedBitmap.getCols()) {
            mosaicGenerator.updateMosaic(slicedBitmap);

            // calculate colors for the next row
            if (slicedBitmap.getDrawnRowsCount() < slicedBitmap.getRows()) {
                mosaicGenerator.calcAverageColors(slicedBitmap);
            } else { // if no more rows recycle the bitmap
                slicedBitmap.getBitmap().recycle();
            }
        }

    }
}
