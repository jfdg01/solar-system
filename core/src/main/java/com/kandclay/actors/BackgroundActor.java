package com.kandclay.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BackgroundActor extends Actor {
    private final Texture backgroundTexture;

    public BackgroundActor(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        this.backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Ensure the stage is of type Stage and its camera is an OrthographicCamera
        Stage stage = getStage();
        if (stage != null && stage.getCamera() instanceof OrthographicCamera) {
            OrthographicCamera camera = (OrthographicCamera) stage.getCamera();

            // Viewport dimensions adjusted for zoom
            float viewportWidth = camera.viewportWidth * camera.zoom;
            float viewportHeight = camera.viewportHeight * camera.zoom;

            // Center the background around the camera's position
            float backgroundCenterX = camera.position.x;
            float backgroundCenterY = camera.position.y;

            // Calculate texture coordinates offset by the camera position to make the zoom effect centered on the camera
            float u = (backgroundCenterX - viewportWidth / 2) / backgroundTexture.getWidth();
            float v = (backgroundCenterY - viewportHeight / 2) / backgroundTexture.getHeight();
            float u2 = u + viewportWidth / backgroundTexture.getWidth();
            float v2 = v + viewportHeight / backgroundTexture.getHeight();

            // Ensure the drawing is centered on the screen
            float drawX = backgroundCenterX - viewportWidth / 2;
            float drawY = backgroundCenterY - viewportHeight / 2;

            // Draw the texture with calculated coordinates for wrapping
            batch.draw(backgroundTexture, drawX, drawY, viewportWidth, viewportHeight, u, v, u2, v2);
        }
    }
}
