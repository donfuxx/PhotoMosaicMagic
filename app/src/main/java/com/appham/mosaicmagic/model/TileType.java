package com.appham.mosaicmagic.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author thomas
 */

public enum TileType implements CanvasDrawing {

    CIRCLE(Canvas::drawOval),
    SQUARE(Canvas::drawRect);

    private final CanvasDrawing canvasDrawing;

    TileType(CanvasDrawing canvasDrawing) {
        this.canvasDrawing = canvasDrawing;
    }

    @Override
    public void draw(Canvas canvas, RectF rectF, Paint paint) {
        this.canvasDrawing.draw(canvas, rectF, paint);
    }
}
