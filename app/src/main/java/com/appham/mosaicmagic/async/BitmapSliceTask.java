package com.appham.mosaicmagic.async;

import android.support.annotation.NonNull;

import com.appham.mosaicmagic.model.SlicedBitmap;
import com.appham.mosaicmagic.presenter.MosaicGenerator;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class BitmapSliceTask extends PooledTask<SlicedBitmap, Void, SlicedBitmap> {

    private final MosaicGenerator mosaicGenerator;

    public BitmapSliceTask(@NonNull MosaicGenerator mosaicGenerator) {
        this.mosaicGenerator = mosaicGenerator;
    }

    @NonNull
    @Override
    protected SlicedBitmap doInBackground(SlicedBitmap... slicedBitmaps) {

        SlicedBitmap slicedBitmap = slicedBitmaps[0];

        slicedBitmap.sliceNextRow();

        return slicedBitmap;
    }

    @Override
    protected void onPostExecute(@NonNull SlicedBitmap slicedBitmap) {

        mosaicGenerator.calcAverageColors(slicedBitmap);

    }
}
