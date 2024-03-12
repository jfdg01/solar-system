package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private final AssetManager assetManager;
    private CameraController cameraController;
    private World world;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    private float stateTime = 0;
    private CelestialBody sun, earth, saturn, moon;

    String backgroundPath;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();

        loadAssets();
    }

    private void loadAssets() {
        backgroundPath = "sprites/static/backgroundSimple.png";

        assetManager.load(backgroundPath, Texture.class);
        assetManager.load("sprites/anim/earth.png", Texture.class);
        assetManager.load("sprites/anim/saturn.png", Texture.class);
        assetManager.load("sprites/anim/moon.png", Texture.class);
        assetManager.load("sprites/anim/sun.png", Texture.class);
        assetManager.finishLoading();
    }

    @Override
    public void show() {
        initializeWorld();
        initializeCameraAndViewport();
        initializeStage();
        setupInputProcessors();
        createSolarSystem();
    }

    private void initializeWorld() {
        world = new World(new Vector2(0, 0), true);
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
        cameraController = new CameraController(worldCamera, worldViewport, CAMERA_MOVE_SPEED, ZOOM_IN_FACTOR, ZOOM_OUT_FACTOR, this);
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        // Add other input processors as needed
        Gdx.input.setInputProcessor(multiplexer);
    }


    private void createSolarSystem() {
        Vector2 sunPosition = new Vector2(Gdx.graphics.getWidth() / 2f / PIXELS_TO_METERS, Gdx.graphics.getHeight() / 2f / PIXELS_TO_METERS);
        float sunRadius = SUN_RADIUS_PIXELS / PIXELS_TO_METERS;
        sun = createCelestialBody(BodyDef.BodyType.StaticBody, sunRadius, 0, sunPosition, "sun");

        Vector2 earthPosition = new Vector2(sunPosition.x + EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, sunPosition.y);
        float earthRadius = EARTH_RADIUS_PIXELS / PIXELS_TO_METERS;
        earth = createCelestialBody(BodyDef.BodyType.DynamicBody, earthRadius, EARTH_ORBIT_SPEED, earthPosition, "earth");

        Vector2 saturnPosition = new Vector2(sunPosition.x + SATURN_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, sunPosition.y);
        float saturnRadius = SATURN_RADIUS_PIXELS / PIXELS_TO_METERS;
        saturn = createCelestialBody(BodyDef.BodyType.DynamicBody, saturnRadius, SATURN_ORBIT_SPEED, saturnPosition, "saturn");

        Vector2 moonPosition = new Vector2(earthPosition.x + MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS, earthPosition.y);
        float moonRadius = MOON_RADIUS_PIXELS / PIXELS_TO_METERS;
        moon = createCelestialBody(BodyDef.BodyType.DynamicBody, moonRadius, MOON_ORBIT_SPEED, moonPosition, "moon");
    }

    private Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
        Texture texture = assetManager.get(String.format("sprites/anim/%s.png", regionName), Texture.class);

        // Assuming each frame is of equal size, calculate the width and height of each frame
        int frameWidth = texture.getWidth() / ANIMATION_NUM_COLS;
        int frameHeight = texture.getHeight() / ANIMATION_NUM_ROWS;

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[ANIMATION_NUM_ROWS * ANIMATION_NUM_COLS];

        int index = 0;
        for (int i = 0; i < ANIMATION_NUM_ROWS; i++) {
            for (int j = 0; j < ANIMATION_NUM_COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<TextureRegion>(FRAME_DURATION, animationFrames);
    }

    private CelestialBody createCelestialBody(BodyDef.BodyType bodyType, float radius, float orbitSpeed,
                                              Vector2 position, String regionName) {

        Animation<TextureRegion> animation = createAnimationFromAssetManager(regionName);
        return new CelestialBody(world, bodyType, radius, orbitSpeed, position, spriteBatch, animation);
    }

    public void manageOrbits() {
        adjustOrbitVelocity(moon, earth, MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(earth, sun, EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(saturn, sun, SATURN_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
    }

    public void adjustOrbitVelocity(CelestialBody orbitingBody, CelestialBody centralBody, float maxOrbitDistance) {
        Vector2 centerToBody = orbitingBody.body.getPosition().sub(centralBody.body.getPosition());
        float currentDistance = centerToBody.len();

        // Calculate the tangent velocity for the orbiting body
        Vector2 tangentVelocity = new Vector2(-centerToBody.y, centerToBody.x).nor().scl(orbitingBody.getOrbitSpeed());

        // If the current distance exceeds the maximum allowed distance, adjust the velocity to bring the orbiting body closer
        if (currentDistance > maxOrbitDistance) {
            // Calculate a correction vector that points towards the central body
            Vector2 correctionVector = centerToBody.scl(-1).nor().scl(orbitingBody.getOrbitSpeed() * 0.5f); // Apply a gentle pull towards the central body
            tangentVelocity.add(correctionVector);
        }

        // Adjust the orbiting body's velocity, considering the central body's movement as well
        Vector2 adjustedVelocity = tangentVelocity.add(centralBody.body.getLinearVelocity());
        orbitingBody.body.setLinearVelocity(adjustedVelocity.x, adjustedVelocity.y);
    }

    private void drawSolarSystem() {
        drawSun();
        drawEarth();
        drawSaturn();
        drawMoon();
    }

    private void drawBackground() {
        Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);
        float backgroundWidth = backgroundTexture.getWidth();
        float backgroundHeight = backgroundTexture.getHeight();

        // Calculate visible area considering zoom
        float visibleWidth = worldViewport.getWorldWidth() * worldCamera.zoom;
        float visibleHeight = worldViewport.getWorldHeight() * worldCamera.zoom;

        // Calculate the offset to align the background center with the camera center
        float startX = worldCamera.position.x - (backgroundWidth / 2);
        float startY = worldCamera.position.y - (backgroundHeight / 2);

        // Calculate the required number of tiles to cover the visible area, including extra for zooming out
        int tilesX = (int) Math.ceil(visibleWidth / backgroundWidth) + 2; // Adding 2 for padding
        int tilesY = (int) Math.ceil(visibleHeight / backgroundHeight) + 2; // Adding 2 for padding

        // Adjust for initial layer to be centered
        startX -= (tilesX / 2f) * backgroundWidth;
        startY -= (tilesY / 2f) * backgroundHeight;

        // Draw the background tiles
        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                spriteBatch.draw(backgroundTexture, startX + (x * backgroundWidth), startY + (y * backgroundHeight));
            }
        }
    }


    @Override
    public void render(float delta) {
        stateTime += delta;
        manageOrbits();
        clearScreen();
        stepWorld();

        spriteBatch.begin();
        drawBackground();
        drawSolarSystem();
        spriteBatch.end();

        updateWorldStage(delta);
        update(delta);
    }

    public void update(float deltaTime) {
        cameraController.update(deltaTime);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void stepWorld() {
        world.step(1f / PHYSICS_TIME_STEP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
    }

    private void drawSun() {
        sun.draw(stateTime);
    }

    private void drawEarth() {
        earth.draw(stateTime);
    }

    private void drawSaturn() {
        saturn.draw(stateTime);
    }

    private void drawMoon() {
        moon.draw(stateTime);
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
        assetManager.dispose();
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

    public CelestialBody getSun() {
        return sun;
    }

    public CelestialBody getEarth() {
        return earth;
    }

    public CelestialBody getSaturn() {
        return saturn;
    }

    public CelestialBody getMoon() {
        return moon;
    }
}