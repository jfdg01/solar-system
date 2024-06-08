package com.kandclay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kandclay.managers.MyAssetManager;
import com.kandclay.screens.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    SpriteBatch batch;
    public boolean clockwiseDirection = true;
    public MyAssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new MyAssetManager();
        assetManager.loadAssets();
        assetManager.finishLoading();
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
}
