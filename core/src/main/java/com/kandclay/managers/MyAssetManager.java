// MyAssetManager.java
package com.kandclay.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyAssetManager {
    private AssetManager assetManager;

    public MyAssetManager() {
        assetManager = new AssetManager();
    }

    public void loadAssets() {
        String[] textures = {
            "sprites/static/backgroundSimple.png",
            "sprites/anim/earth.png",
            "sprites/anim/saturn.png",
            "sprites/anim/moon.png",
            "sprites/anim/sun.png",
            "sprites/anim/uranus.png",
            "sprites/anim/venus.png",
            "sprites/anim/jupiter.png",
            "sprites/anim/mars.png",
            "sprites/anim/neptune.png",
            "sprites/anim/mercury.png",
            "sprites/static/ice.png"
        };
        for (String texture : textures) {
            assetManager.load(texture, Texture.class);
        }
        assetManager.load("skin/default/skin/uiskin.json", Skin.class);
    }

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public <T> T get(String assetPath, Class<T> type) {
        return assetManager.get(assetPath, type);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
