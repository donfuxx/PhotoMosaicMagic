package com.appham.photomosaicmagic.model;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A model of a bitmap sliced into tiles for mosaic drawing
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class SlicedBitmap {

    private final Bitmap bitmap;
    private final int tileWidth;
    private final int tileHeight;
    private final int rows;
    private final int cols;
    private final int sliceCount;
    private final TileType tileType;
    private String cancelReason;
    private boolean cancelReasonWasShown;

    /**
     * a list of all the rows of tiles of this bitmap
     */
    private final List<List<Tile>> tiles;

    /**
     * Indicates how many dots are already loaded in the current row
     */
    private int loadedDotsRowCount;

    /**
     * Indicates how many rows are already drawn on the canvas
     */
    private int drawnRowsCount;

    public SlicedBitmap(Bitmap bitmap, int tileWidth, int tileHeight, TileType tileType) {
        this.bitmap = bitmap;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileType = tileType;
        this.rows = bitmap.getHeight() / tileHeight;
        this.cols = bitmap.getWidth() / tileWidth;
        this.sliceCount = rows * cols;
        this.tiles = new ArrayList<>(sliceCount);
    }

    /**
     * @return number of rows of tiles
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return number of columns of tiles
     */
    public int getCols() {
        return cols;
    }

    /**
     * Slices up the next row of tiles
     *
     * @return true if there was a next row to slice, false otherwise
     */
    public boolean sliceNextRow() {

        if (tiles.size() < rows) {
            tiles.add(sliceRow(Math.max(tiles.size() - 1, 0)));
            return true;
        }

        return false;
    }

    /**
     * @param index the index of the row (0 is the top row)
     * @return a list of bitmap tiles from left to right of the row or null if invalid index
     */
    @Nullable
    private List<Tile> sliceRow(int index) {
        if (index >= 0 && index < rows) {

            List<Tile> tiles = new ArrayList<>(cols);

            for (int i = 0; i < cols; i++) {
                tiles.add(new Tile(
                        Bitmap.createBitmap(bitmap, i * tileWidth, index * tileHeight, tileWidth, tileHeight),
                        tileType));
            }

            return tiles;

        }
        return null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getSliceCount() {
        return sliceCount;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }

    public List<Tile> getLastRow() {
        if (!tiles.isEmpty()) {
            return tiles.get(tiles.size() - 1);
        }
        return null;
    }

    public int getLoadedDotsRowCount() {
        return loadedDotsRowCount;
    }

    public void resetLoadedDotsRowCount() {
        this.loadedDotsRowCount = 0;
    }

    public void incrLoadedDotsRowCount() {
        this.loadedDotsRowCount++;
    }

    public int getDrawnRowsCount() {
        return drawnRowsCount;
    }

    public void incrDrawnRowsCount() {
        this.drawnRowsCount++;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public boolean isCancelled() {
        return cancelReason != null;
    }

    public boolean isCancelReasonWasShown() {
        return cancelReasonWasShown;
    }

    public void setCancelReasonWasShown(boolean cancelReasonWasShown) {
        this.cancelReasonWasShown = cancelReasonWasShown;
    }

    public TileType getTileType() {
        return tileType;
    }
}
