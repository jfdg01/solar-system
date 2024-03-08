package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import com.kandclay.Constants;

import static com.kandclay.Constants.SPEED_FACTOR;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game; // Store reference to the main game class
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;

    private Actor sun;
    private Actor earth;
    private Actor mercury;
    private Actor moon;

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
        worldViewport = new ExtendViewport(400, 300, worldCamera); // Extend in one direction
        worldViewport.setScaling(Scaling.contain);

        worldStage = new Stage(worldViewport);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.H) {
                    zoomIn();
                    return true;
                } else if (keycode == Keys.J) {
                    zoomOut();
                    return true;
                }
                return false;
            }
        });
        multiplexer.addProcessor(worldStage);
        Gdx.input.setInputProcessor(multiplexer);

        createSolarSystem();
    }

    /**
     * This method is responsible for creating the solar system in the game.
     * It creates a sun and an earth and adds them to the stage.
     * More celestial bodies can be added as needed.
     */
    private void createSolarSystem() {

        sun = createCelestialBody("sprites/sun.png", 100, 100);
        worldStage.addActor(sun);
        sun.setPosition(Gdx.graphics.getWidth() / 2f - sun.getWidth() / 2, Gdx.graphics.getHeight() / 2f - sun.getHeight() / 2);
        sun.setOrigin(sun.getWidth() / 2, sun.getHeight() / 2);

        earth = createCelestialBody("sprites/earth.png", 50, 50);
        earth.setPosition(sun.getX() + sun.getWidth(), sun.getY()); // Set initial position of earth
        worldStage.addActor(earth);

        /*mercury = createCelestialBody("sprites/mercury.png", 25, 25);
        worldStage.addActor(mercury);
        mercury.setPosition(50, 0);
        orbitCelestialBody(mercury, sun, Constants.MERCURY_ROTATION_SPEED, 1);

        moon = createCelestialBody("sprites/moon.png", 10, 10);
        worldStage.addActor(moon);
        moon.setPosition(25, 0);
        orbitCelestialBody(moon, earth, Constants.MOON_ROTATION_SPEED, 1);*/
    }

    private void orbitCelestialBody(Actor orbitingBody, Actor orbitedBody, float orbitSpeed, float delta) {
        float centerX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
        float centerY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

        float angle = (float) (Math.atan2(orbitingBody.getY() - centerY, orbitingBody.getX() - centerX) + orbitSpeed * delta * SPEED_FACTOR);
        float distance = (float) Math.hypot(orbitingBody.getX() - centerX, orbitingBody.getY() - centerY);

        orbitingBody.setPosition(centerX + distance * (float) Math.cos(angle), centerY + distance * (float) Math.sin(angle));
    }

    private Actor createCelestialBody(String texturePath, float width, float height) {

        Texture texture = new Texture(Gdx.files.internal(texturePath));

        Image celestialBody = new Image(texture);

        celestialBody.setSize(width, height);

        return celestialBody;
    }

    private void manualRotate() {
        sun.rotateBy(10);
    }

    private void rotateCelestialBody(Actor celestialBody, float rotationSpeed, float delta) {
        celestialBody.rotateBy(rotationSpeed * delta * SPEED_FACTOR);
    }

    /*private void rotate(float delta) {
        sun.rotateBy(Constants.SUN_ROTATION_SPEED * delta);
    }*/

    /**
     * This method creates the UI controls for the game.
     * It creates a skin from a file, creates a rotate button with the skin,
     * sets the position of the button, adds a click listener to the button that rotates all actors on the stage,
     * and adds the button to the stage.
     */
    /*private void createUIControls() {
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
    }*/
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // rotate(delta);
        rotateCelestialBody(sun, Constants.SUN_ROTATION_SPEED, delta);
        // rotateCelestialBody(earth, Constants.EARTH_ROTATION_SPEED, delta);
        /*rotateCelestialBody(mercury, Constants.MERCURY_ROTATION_SPEED, delta);*/

        orbitCelestialBody(earth, sun, Constants.EARTH_ROTATION_SPEED, 1);


        worldCamera.update();
        worldStage.act(delta);
        worldStage.draw();
    }

    public void zoomIn() {
        worldCamera.zoom *= Constants.ZOOM_IN_FACTOR;
    }

    public void zoomOut() {
        worldCamera.zoom *= Constants.ZOOM_OUT_FACTOR;
    }


    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
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
    }
}

