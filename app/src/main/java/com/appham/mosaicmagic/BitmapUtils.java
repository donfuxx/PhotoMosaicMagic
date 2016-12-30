package com.appham.mosaicmagic;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.appham.mosaicmagic.model.SlicedBitmap;
import com.appham.mosaicmagic.model.Tile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public abstract class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    /**
     * Scales the bitmap to perfectly fit the tiles without overlap when necessary
     */
    @WorkerThread
    @NonNull
    public static Bitmap scaleForTileSize(@NonNull Bitmap bitmap, int tileWidth, int tileHeight) {

        int overlapWidth = bitmap.getWidth() % tileWidth;
        int overlapHeight = bitmap.getHeight() % tileHeight;

        //if there are overlaps
        if (overlapWidth > 0 || overlapHeight > 0 ||
                bitmap.getWidth() < tileWidth || bitmap.getHeight() < tileHeight) {

            Bitmap scaledBitmap = null;

            //if the bitmap is even smaller than the tile size then scale up
            if (bitmap.getWidth() < tileWidth || bitmap.getHeight() < tileHeight) {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        tileWidth * 10, tileHeight * 10, false);

            } else { // else scale down to bitmap without overlap
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() - overlapWidth, bitmap.getHeight() - overlapHeight, false);
            }

            bitmap.recycle();
            return scaledBitmap;
        } else {
            return bitmap;
        }
    }

    /**
     * @return The decoded bitmap from the stream with optimal sample rate
     * @throws IOException
     */
    @WorkerThread
    @Nullable
    public static Bitmap decodeSampledStream(@NonNull Uri uri, @NonNull ContentResolver contentResolver,
                                             int displayWidth, int displayHeight) throws IOException {

        Bitmap bitmap = null;
        try {
            InputStream stream = contentResolver.openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(stream, null, options);
            stream.close();

            // if it is a very large image then increase the sample
            int sample = 1;
            while (options.outWidth > displayWidth * sample ||
                    options.outHeight > displayHeight * sample) {
                sample = sample * 2;
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = sample;

            stream = contentResolver.openInputStream(uri);

            bitmap = BitmapFactory.decodeStream(stream, null, options);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * calculates the avg. color based on a bitmap tile's red, green, blue values
     */
    @WorkerThread
    public static int calcAverageColor(@NonNull Bitmap tile) {
        int reds = 0;
        int greens = 0;
        int blues = 0;
        int pixCount = tile.getHeight() * tile.getWidth();

        for (int y = 0; y < tile.getHeight(); y++) {
            for (int x = 0; x < tile.getWidth(); x++) {
                int color = tile.getPixel(x, y);
                reds += Color.red(color);
                greens += Color.green(color);
                blues += Color.blue(color);
            }
        }

        // Average RGB color values
        int red = reds / pixCount;
        int green = greens / pixCount;
        int blue = blues / pixCount;

        return Color.argb(255, red, green, blue);
    }

    /**
     * Generates a dot bitmap and adds it to the tile
     *
     * @param tile
     */
    @WorkerThread
    public static void addTileDotBitmap(Tile tile) {
        final Bitmap dot = Bitmap.createBitmap(tile.getBitmap().getWidth(),
                tile.getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(dot);

        final Paint paint = new Paint();
        final Rect rect = new Rect(tile.getPadding(), tile.getPadding(),
                tile.getBitmap().getWidth() - tile.getPadding(),
                tile.getBitmap().getHeight() - tile.getPadding());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawColor(tile.getBgColor());
        paint.setColor(tile.getAvgColorInt());

        tile.getTileType().draw(canvas, rectF, paint);

//        canvas.drawOval(rectF, paint);
//        canvas.drawRect(rect, paint);

//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(tile.getBitmap(), null, rect, paint);

        tile.getBitmap().recycle();

        tile.setDot(dot);
    }

    /**
     * Performs the mosaic drawing
     *
     * @param mosaicBitmap the bitmap where the mosaic gets drawn
     * @param slicedBitmap the model of the sliced mosaic tiles
     */
    public static void drawMosaicOnBitmap(@NonNull Bitmap mosaicBitmap, @NonNull SlicedBitmap slicedBitmap) {

        // don't draw on already recycled bitmaps
        if (mosaicBitmap.isRecycled()) {
            return;
        }

        Canvas canvas = new Canvas(mosaicBitmap);

        List<Tile> tilesRow = slicedBitmap.getLastRow();

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
//        whitePaint.setAlpha(128);

        // First draw a white background for the row
//        canvas.drawRect(0,
//                slicedBitmap.getTileHeight() * (slicedBitmap.getDrawnRowsCount()),
//                slicedBitmap.getBitmap().getWidth(),
//                slicedBitmap.getTileHeight() * (slicedBitmap.getDrawnRowsCount() + 1),
//                whitePaint);

        // Draw mosaic dots row
        for (int i = 0; i < tilesRow.size(); i++) {

            Tile tile = tilesRow.get(i);
            canvas.drawBitmap(tile.getDot(),
                    i * slicedBitmap.getTileWidth(),
                    slicedBitmap.getTileHeight() * (slicedBitmap.getDrawnRowsCount()), null);

            // recycle no longer used tiles
            tile.recycle();

        }

        // after row is drawn process the next row
        slicedBitmap.sliceNextRow();
        slicedBitmap.incrDrawnRowsCount();
    }
}
