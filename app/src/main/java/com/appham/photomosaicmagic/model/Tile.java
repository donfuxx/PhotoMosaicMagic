package com.appham.photomosaicmagic.model;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * A Tile of a SlicedBitmap
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 * @see SlicedBitmap
 */
public class Tile {

    private final Bitmap bitmap;
    private Bitmap dot;
    private String avgColor;
    private int avgColorInt;
    private int padding;
    private int bgColor = Color.WHITE;
    private TileType tileType = TileType.CIRCLE;

    public Tile(Bitmap bitmap, int padding, TileType tileType) {
        this.bitmap = bitmap;
        this.padding = padding;
        this.tileType = tileType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap getDot() {
        return dot;
    }

    public void setDot(Bitmap dot) {
        this.dot = dot;
    }

    public String getAvgColor() {
        return avgColor;
    }

    public void setAvgColor(String avgColor) {
        this.avgColor = avgColor;
    }

    public void recycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (dot != null && !dot.isRecycled()) {
            dot.recycle();
        }
    }

    public int getAvgColorInt() {
        return avgColorInt;
    }

    public void setAvgColorInt(int avgColorInt) {
        this.avgColorInt = avgColorInt;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }
}
