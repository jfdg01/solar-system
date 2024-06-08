package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kandclay.Main;
import com.kandclay.screens.MenuScreen;

public class OptionsScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    public OptionsScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/default/skin/uiskin.json"));

        // Create button to toggle orbit direction
        TextButton toggleOrbitButton = new TextButton("Toggle Orbit Direction", skin);

        // Add listener to toggle orbit button
        toggleOrbitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("OptionsScreen", "Toggle Orbit Direction button clicked");
                game.clockwiseDirection = !game.clockwiseDirection;
            }
        });

        // Create back button
        TextButton backButton = new TextButton("Back", skin);

        // Add listener to back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(toggleOrbitButton).size(200, 100).uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(backButton).size(200, 100).uniformX();

        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
