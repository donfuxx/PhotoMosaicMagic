package com.appham.photomosaicmagic.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author thomas
 */
public interface CanvasDrawing {

    void draw(Canvas canvas, RectF rectF, Paint paint);
}
