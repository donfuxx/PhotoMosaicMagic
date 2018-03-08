package com.appham.photomosaicmagic;

import android.content.SharedPreferences;

import com.appham.photomosaicmagic.model.TileType;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */
public class Prefs {
    private SharedPreferences sharedPref;
    private int tileWidth, tileHeight;
    private TileType tileType;

    public Prefs(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        updatePrefs();
    }

    public void updatePrefs() {
        this.tileWidth = Integer.parseInt(sharedPref.getString(PrefKeys.TILE_WIDTH.name(), "32"));
        this.tileHeight = Integer.parseInt(sharedPref.getString(PrefKeys.TILE_HEIGHT.name(), "32"));
        this.tileType = TileType.valueOf(sharedPref.getString(PrefKeys.TILE_TYPE.name(), TileType.CIRCLE.name()));
    }

    public void save() {
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(PrefKeys.TILE_WIDTH.name(), String.valueOf(tileWidth));
        edit.putString(PrefKeys.TILE_HEIGHT.name(), String.valueOf(tileHeight));
        edit.putString(PrefKeys.TILE_TYPE.name(), tileType.name());
        edit.apply();
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public TileType getTileType() {
        return tileType;
    }

    private enum PrefKeys {
        TILE_WIDTH, TILE_HEIGHT, TILE_TYPE
    }

}
