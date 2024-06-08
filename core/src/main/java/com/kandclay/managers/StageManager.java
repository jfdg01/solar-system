package com.kandclay.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.kandclay.actors.BackgroundActor;

public class StageManager {
    private final Stage worldStage;

    public StageManager(CameraManager cameraManager) {
        cameraManager.getViewport().setScaling(Scaling.contain);
        this.worldStage = new Stage(cameraManager.getViewport());
    }

    public Stage getWorldStage() {
        return worldStage;
    }

    public void initializeBackground(Texture backgroundTexture, float width, float height) {
        BackgroundActor backgroundActor = new BackgroundActor(backgroundTexture);
        backgroundActor.setSize(width, height);
        backgroundActor.setPosition(0, 0);
        backgroundActor.toBack();
        worldStage.addActor(backgroundActor);
    }

    public void update(float delta) {
        worldStage.act(delta);
    }
}
