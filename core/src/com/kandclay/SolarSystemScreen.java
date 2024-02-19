package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.kandclay.Constants;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game; // Store reference to the main game class
    private Stage worldStage;
    private Stage uiStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private final int virtualHeight = 720;
    private final int virtualWidth = 1280;
    private FitViewport uiViewport;
    private Group sun;
    private Group earth;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
    }

    /**
     * This method is called when this screen becomes the current screen for a Game.
     * It initializes the viewport and stage, sets the input processor to the stage,
     * and calls the methods to create the solar system and UI controls.
     */
    @Override
    public void show() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(virtualWidth, virtualHeight, worldCamera); // Extend in one direction
        worldViewport.setScaling(Scaling.contain);

        uiViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        worldStage = new Stage(worldViewport);
        uiStage = new Stage(uiViewport);

        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, worldStage)); // Handle input for both stages

        createSolarSystem();
        createUIControls();
    }


    private void createSun() {
        sun = createCelestialBodyGroup("sprites/sun.png", 150, 150);
        //relative to the screen
        sun.setPosition(Gdx.graphics.getWidth() / 2f - sun.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - sun.getHeight() / 2f);
        // sun.setPosition(0,0);
        //point from where it rotates
        sun.setOrigin(sun.getWidth() / 2f, sun.getHeight() / 2f);
    }

    /**
     * This method is responsible for creating the solar system in the game.
     * It creates a sun and an earth and adds them to the stage.
     * More celestial bodies can be added as needed.
     */
    private void createSolarSystem() {

        createSun();
        worldStage.addActor(sun);

        // Create an Actor for the earth using the createCelestialBody method
        earth = createCelestialBodyGroup("sprites/earth.png", 80, 80);

        // Set the position of the earth relative to the sun
        earth.setPosition(700, 0);
        sun.addActor(earth);

        Actor moon = createCelestialBody("sprites/Ice.png", 20, 20);

        moon.setPosition(250, 0);
        earth.addActor(moon);

        // Additional celestial bodies can be added to the sunGroup as needed
    }

    /**
     * This method creates a group representing a celestial body in the game.
     * It creates a texture from the provided file path, creates an image from the texture,
     * sets the size and origin of the image, adds the image to a new group, and returns the group.
     *
     * @param texturePath The path to the texture file for the celestial body.
     * @return A group containing an image of the celestial body.
     */
    private Group createCelestialBodyGroup(String texturePath, int width, int height) {
        Image image = new Image(new Texture(Gdx.files.internal(texturePath)));
        Group group = new Group();
        image.setSize(width, height);
        group.setSize(width, height);
        group.addActor(image);
        return group;
    }

    /**
     * This method creates an Actor representing a celestial body in the game.
     * It creates a texture from the provided file path, creates an image from the texture,
     * sets the size and origin of the image, and returns the image as an Actor.
     *
     * @param texturePath The path to the texture file for the celestial body.
     * @param width       The width of the celestial body.
     * @param height      The height of the celestial body.
     * @return An Actor representing the celestial body.
     */
    private Actor createCelestialBody(String texturePath, float width, float height) {

        Texture texture = new Texture(Gdx.files.internal(texturePath));

        Image celestialBody = new Image(texture);

        celestialBody.setSize(width, height);

        return celestialBody;
    }

    private void manualRotate() {
        sun.rotateBy(10);
    }

    private void rotate(float delta) {
        float rotationSpeed = 10;
        // every child in the sun group rotates
        sun.rotateBy(rotationSpeed * delta);
    }

    /**
     * This method creates the UI controls for the game.
     * It creates a skin from a file, creates a rotate button with the skin,
     * sets the position of the button, adds a click listener to the button that rotates all actors on the stage,
     * and adds the button to the stage.
     */
    private void createUIControls() {
        // Create a new Skin from the file "uiskin.json"
        Skin skin = new Skin(Gdx.files.internal("lgdxs/skin/lgdxs-ui.json"));

        // Create a new TextButton with the text "Rotate" and the skin
        TextButton rotateButton = new TextButton("Rotate", skin);

        // Set the position of the button to the top left of the screen
        rotateButton.setPosition(0, uiViewport.getWorldHeight() - rotateButton.getHeight());

        // Add a ClickListener to the button
        rotateButton.addListener(new ClickListener() {
            // Override the clicked method to rotate all actors on the stage by 10 degrees when the button is clicked
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manualRotate();
            }
        });

        // Add the button to the stage
        uiStage.addActor(rotateButton);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rotate(delta);

        worldCamera.update();
        worldStage.act(delta);
        worldStage.draw();

        uiStage.act(delta);
        uiStage.draw();
    }


    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true); // True to center the camera
        uiViewport.update(width, height);
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
        worldStage.dispose();
        uiStage.dispose();
    }
}

