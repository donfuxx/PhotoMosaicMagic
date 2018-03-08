package com.appham.photomosaicmagic;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.appham.photomosaicmagic.model.SlicedBitmap;
import com.appham.photomosaicmagic.model.Tile;
import com.appham.photomosaicmagic.model.TileType;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Some simple unit tests just to demonstrate that the app can be tested automatically
 *
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bitmap.class)
public class SlicedBitmapUnitTest extends TestCase {

    private SlicedBitmap slicedBitmap;

    /**
     * Instantiates a slicedBitmap with 32x32 tiles
     */
    @Before
    public void mockBitmap() {

        // create a sliced bitmap with 32x32 tile size
        slicedBitmap = new SlicedBitmap(getMockedBitmap(1000, 500), 32, 32, TileType.CIRCLE);
    }

    /**
     * Mocks a bitmap with specific width and height
     */
    @NonNull
    private Bitmap getMockedBitmap(int width, int height) {
        Bitmap bitmap = mock(Bitmap.class);
        when(bitmap.getWidth()).thenReturn(width);
        when(bitmap.getHeight()).thenReturn(height);
        return bitmap;
    }

    /**
     * Test that slice count is correctly calculated
     */
    @Test
    public void testSlicedCountCorrect() throws Exception {

        assertEquals("Wrong amount of slices", 465, slicedBitmap.getSliceCount());
    }

    /**
     * Test that the the last row is null before slicing bitmap
     */
    @Test
    public void testLastRowBefore() throws Exception {

        assertNull("There should NOT be a last row yet", slicedBitmap.getLastRow());
    }

    /**
     * Test that the the last row has correct amount of valid tiles after slicing
     */
    @Test
    public void testLastRowAfter() throws Exception {

        PowerMockito.mockStatic(Bitmap.class);

        slicedBitmap.sliceNextRow();

        assertNotNull("There should be a last row after slicing", slicedBitmap.getLastRow());
        assertEquals("Wrong amount of row tiles", 31, slicedBitmap.getLastRow().size());

        for (Tile tile : slicedBitmap.getLastRow()) {
            assertNotNull("Row tile should not be null", tile);
        }
    }

}