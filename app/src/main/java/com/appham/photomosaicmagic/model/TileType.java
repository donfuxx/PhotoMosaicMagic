package com.appham.photomosaicmagic.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.appham.photomosaicmagic.presenter.MosaicContract;

/**
 * @author thomas
 */

public enum TileType implements MosaicContract.Model {

    CIRCLE(Canvas::drawOval),
    SQUARE(Canvas::drawRect);

    private final MosaicContract.Model model;

    TileType(MosaicContract.Model model) {
        this.model = model;
    }

    @Override
    public void draw(Canvas canvas, RectF rectF, Paint paint) {
        this.model.draw(canvas, rectF, paint);
    }
}
