package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private World worldPhysics;
    private float physics_accumulator = 0f;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    //Sun
    private Body sunBody;
    private TextureRegion sunRegion;
    private Texture sunTexture;

    // Earth
    private Body earthBody;
    private Texture earthTexture;
    private TextureRegion earthRegion;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        initializeWorld();
        initializeCameraAndViewport();
        initializeStage();
        setupInputProcessors();
        loadResources();
        createSolarSystem();
    }

    private void initializeWorld() {
        worldPhysics = new World(new Vector2(0, 0), true);
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void setupInputProcessors() {
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
    }

    private void loadResources() {
        sunTexture = new Texture(Gdx.files.internal("sprites/Lava.png"));
        sunRegion = new TextureRegion(sunTexture);
        earthTexture = new Texture(Gdx.files.internal("sprites/Terran.png"));
        earthRegion = new TextureRegion(earthTexture);
    }

    private void createSun() {
        // Code to create a static body for the sun
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Gdx.graphics.getWidth() / 2f / PIXELS_TO_METERS, Gdx.graphics.getHeight() / 2f / PIXELS_TO_METERS);

        sunBody = worldPhysics.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(SUN_RADIUS_PIXELS / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        sunBody.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createEarth() {
        // Adjust these values as needed
        Vector2 initialPosition = new Vector2(sunBody.getPosition().x + (float) EARTH_DISTANCE_TO_SUN_PIXELS / Constants.PIXELS_TO_METERS, sunBody.getPosition().y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);

        earthBody = worldPhysics.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((float) EARTH_RADIUS_PIXELS / Constants.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        earthBody.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createSolarSystem() {
        createSun();
        createEarth();
    }

    private void handleEarthRotation() {
        Vector2 centerToEarth = earthBody.getPosition().sub(sunBody.getPosition());

        Vector2 tangentVelocity = new Vector2(-centerToEarth.y, centerToEarth.x);
        tangentVelocity.nor(); // Normalize to get direction
        tangentVelocity.scl(EARTH_ORBIT_SPEED); // Scale by the desired speed
        earthBody.setLinearVelocity(tangentVelocity.x, tangentVelocity.y);
    }


    @Override
    public void render(float delta) {
        handleEarthRotation();
        clearScreen();
        stepWorld(delta);

        spriteBatch.begin();
        drawSun();
        drawEarth();
        spriteBatch.end();

        updateWorldStage(delta);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void stepWorld(float delta) {
        // Add the frame's delta time to the accumulator.
        physics_accumulator += delta;

        // Run the simulation for as many fixed steps as the accumulator can cover.
        while (physics_accumulator >= PHYSICS_TIME_STEP) {
            worldPhysics.step(PHYSICS_TIME_STEP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
            physics_accumulator -= PHYSICS_TIME_STEP;
        }
    }

    private void drawSun() {
        Vector2 bodyPos = sunBody.getPosition();
        float rotation = MathUtils.radiansToDegrees * sunBody.getAngle();
        drawCelestialBody(sunRegion, bodyPos, rotation, SUN_RADIUS_PIXELS);
    }

    private void drawEarth() {
        Vector2 bodyPos = earthBody.getPosition();
        drawCelestialBody(earthRegion, bodyPos, 0, NO_SCALING); // Earth does not scale like the sun in this example, hence -1 for scale factor
    }

    private void drawCelestialBody(TextureRegion region, Vector2 position, float rotation, float scalePixels) {
        float scale = scalePixels > 0 ? (scalePixels * 2) / region.getRegionWidth() : 1; // Only scale if scalePixels is positive
        float screenX = position.x * Constants.PIXELS_TO_METERS - (float) region.getRegionWidth() / 2;
        float screenY = position.y * Constants.PIXELS_TO_METERS - (float) region.getRegionHeight() / 2;

        spriteBatch.draw(region, screenX, screenY, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), scale, scale, rotation);
    }

    private void updateWorldStage(float delta) {
        worldCamera.update();
        worldStage.act(delta);
        worldStage.draw();
    }

    public void zoomIn() {
        worldCamera.zoom *= ZOOM_IN_FACTOR;
    }

    public void zoomOut() {
        worldCamera.zoom *= ZOOM_OUT_FACTOR;
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
    }

    @Override
    public void dispose() {
        worldStage.dispose();
        sunTexture.dispose();
        earthTexture.dispose();
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
}