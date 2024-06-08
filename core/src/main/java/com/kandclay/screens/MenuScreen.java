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
import com.kandclay.managers.MyAssetManager;

public class MenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private MyAssetManager assetManager;

    public MenuScreen(final Main game) {
        this.assetManager = game.assetManager;
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/default/skin/uiskin.json"));

        // Create buttons
        TextButton playButton = new TextButton("Play", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SolarSystemScreen(game));
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new OptionsScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Set button sizes
        playButton.setSize(200,100);
        optionsButton.setSize(200,100);
        exitButton.setSize(200,100);

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).size(playButton.getWidth(), playButton.getHeight()).uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(optionsButton).size(optionsButton.getWidth(), optionsButton.getHeight()).uniformX();
        table.row();
        table.add(exitButton).size(exitButton.getWidth(), exitButton.getHeight()).uniformX();

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
