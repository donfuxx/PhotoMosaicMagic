package com.appham.mosaicmagic.async;

import android.support.annotation.NonNull;

import com.appham.mosaicmagic.BitmapUtils;
import com.appham.mosaicmagic.model.SlicedBitmap;
import com.appham.mosaicmagic.model.Tile;
import com.appham.mosaicmagic.presenter.MosaicGenerator;

import java.util.Locale;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public class CalcColorTask extends PooledTask<SlicedBitmap, Void, SlicedBitmap> {

    private final MosaicGenerator mosaicGenerator;

    public CalcColorTask(@NonNull MosaicGenerator mosaicGenerator) {
        this.mosaicGenerator = mosaicGenerator;
    }

    @Override
    protected SlicedBitmap doInBackground(SlicedBitmap... slicedBitmaps) {

        SlicedBitmap slicedBitmap = slicedBitmaps[0];

        slicedBitmap.resetLoadedDotsRowCount();

        for (Tile tile : slicedBitmap.getLastRow()) {

            int color = BitmapUtils.calcAverageColor(tile.getBitmap());

            String colorCode = String.format(Locale.ROOT, "%06X",
                    (0xFFFFFF & color));

            tile.setAvgColorInt(color);

            tile.setAvgColor(colorCode);

            BitmapUtils.addTileDotBitmap(tile);

            slicedBitmap.incrLoadedDotsRowCount();

        }
        return slicedBitmap;
    }

    @Override
    protected void onPostExecute(@NonNull SlicedBitmap slicedBitmap) {

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
