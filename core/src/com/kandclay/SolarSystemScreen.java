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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;

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
    private Array<CelestialBody> celestialBodies;
    String backgroundPath;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        celestialBodies = new Array<>();

        loadAssets();
    }

    private void loadAssets() {
        backgroundPath = "sprites/static/backgroundSimple.png";

        assetManager.load(backgroundPath, Texture.class);
        assetManager.load("sprites/anim/earth.png", Texture.class);
        assetManager.load("sprites/anim/saturn.png", Texture.class);
        assetManager.load("sprites/anim/moon.png", Texture.class);
        assetManager.load("sprites/anim/sun.png", Texture.class);
        assetManager.load("sprites/anim/uranus.png", Texture.class);
        //
        /*assetManager.load("sprites/anim/venus.png", Texture.class);
        assetManager.load("sprites/anim/jupiter.png", Texture.class);
        assetManager.load("sprites/anim/mars.png", Texture.class);
        assetManager.load("sprites/anim/neptune.png", Texture.class);*/

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
        cameraController = new CameraController(worldCamera, worldViewport, CAMERA_MOVE_SPEED, ZOOM_IN_FACTOR, ZOOM_OUT_FACTOR, this.getCelestialBodies());
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
        CelestialBody sun = createCelestialBody(BodyDef.BodyType.StaticBody, sunRadius, 0, sunPosition, "sun", null, 0);
        celestialBodies.add(sun);
        SUN = celestialBodies.indexOf(sun, true);

        float mercuryDistanceToSun = MERCURY_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 mercuryPosition = new Vector2(sunPosition.x + mercuryDistanceToSun + sunRadius, sunPosition.y);
        float mercuryRadius = MERCURY_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody mercury = createCelestialBody(BodyDef.BodyType.DynamicBody, mercuryRadius, MERCURY_ORBIT_SPEED, mercuryPosition, "moon", sun, mercuryDistanceToSun);
        celestialBodies.add(mercury);
        MERCURY = celestialBodies.indexOf(mercury, true);

        float venusDistanceToSun = VENUS_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 venusPosition = new Vector2(sunPosition.x + venusDistanceToSun + sunRadius, sunPosition.y);
        float venusRadius = VENUS_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody venus = createCelestialBody(BodyDef.BodyType.DynamicBody, venusRadius, VENUS_ORBIT_SPEED, venusPosition, "moon", sun, venusDistanceToSun);
        celestialBodies.add(venus);
        VENUS = celestialBodies.indexOf(venus, true);

        float earthDistanceToSun = EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 earthPosition = new Vector2(sunPosition.x + earthDistanceToSun + sunRadius, sunPosition.y);
        float earthRadius = EARTH_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody earth = createCelestialBody(BodyDef.BodyType.DynamicBody, earthRadius, EARTH_ORBIT_SPEED, earthPosition, "earth", sun, earthDistanceToSun);
        celestialBodies.add(earth);
        EARTH = celestialBodies.indexOf(earth, true);

        float moonDistanceToEarth = MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS;
        Vector2 moonPosition = new Vector2(earthPosition.x + moonDistanceToEarth + sunRadius, earthPosition.y);
        float moonRadius = MOON_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody moon = createCelestialBody(BodyDef.BodyType.DynamicBody, moonRadius, MOON_ORBIT_SPEED, moonPosition, "moon", earth, moonDistanceToEarth);
        celestialBodies.add(moon);
        MOON = celestialBodies.indexOf(moon, true);

        float marsDistanceToSun = MARS_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 marsPosition = new Vector2(sunPosition.x + marsDistanceToSun + sunRadius, sunPosition.y);
        float marsRadius = MARS_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody mars = createCelestialBody(BodyDef.BodyType.DynamicBody, marsRadius, MARS_ORBIT_SPEED, marsPosition, "moon", sun, marsDistanceToSun);
        celestialBodies.add(mars);
        MARS = celestialBodies.indexOf(mars, true);

        float jupiterDistanceToSun = JUPITER_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 jupiterPosition = new Vector2(sunPosition.x + jupiterDistanceToSun + sunRadius, sunPosition.y);
        float jupiterRadius = JUPITER_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody jupiter = createCelestialBody(BodyDef.BodyType.DynamicBody, jupiterRadius, JUPITER_ORBIT_SPEED, jupiterPosition, "moon", sun, jupiterDistanceToSun);
        celestialBodies.add(jupiter);
        JUPITER = celestialBodies.indexOf(jupiter, true);

        float saturnDistanceToSun = SATURN_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 saturnPosition = new Vector2(sunPosition.x + saturnDistanceToSun + sunRadius, sunPosition.y);
        float saturnRadius = SATURN_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody saturn = createCelestialBody(BodyDef.BodyType.DynamicBody, saturnRadius, SATURN_ORBIT_SPEED, saturnPosition, "saturn", sun, saturnDistanceToSun);
        celestialBodies.add(saturn);
        SATURN = celestialBodies.indexOf(saturn, true);

        float uranusDistanceToSun = URANUS_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 uranusPosition = new Vector2(sunPosition.x + uranusDistanceToSun + sunRadius, sunPosition.y);
        float uranusRadius = URANUS_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody uranus = createCelestialBody(BodyDef.BodyType.DynamicBody, uranusRadius, URANUS_ORBIT_SPEED, uranusPosition, "uranus", sun, uranusDistanceToSun);
        celestialBodies.add(uranus);
        URANUS = celestialBodies.indexOf(uranus, true);

        float neptuneDistanceToSun = NEPTUNE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS;
        Vector2 neptunePosition = new Vector2(sunPosition.x + neptuneDistanceToSun + sunRadius, sunPosition.y);
        float neptuneRadius = NEPTUNE_RADIUS_PIXELS / PIXELS_TO_METERS;
        CelestialBody neptune = createCelestialBody(BodyDef.BodyType.DynamicBody, neptuneRadius, NEPTUNE_ORBIT_SPEED, neptunePosition, "moon", sun, neptuneDistanceToSun);
        celestialBodies.add(neptune);
        NEPTUNE = celestialBodies.indexOf(neptune, true);

    }

    private Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
        String name = "sprites/anim/" + regionName + ".png";
        Texture texture = assetManager.get(name, Texture.class);

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
                                              Vector2 position, String texturePathSuffix, CelestialBody orbitedBody, float distanceToOrbitedBody) {

        Animation<TextureRegion> animation = createAnimationFromAssetManager(texturePathSuffix);
        return new CelestialBody(world, texturePathSuffix, bodyType, radius, orbitSpeed, position, spriteBatch, animation, orbitedBody, distanceToOrbitedBody);
    }

    public void manageOrbits() {
        for (CelestialBody body : celestialBodies) {
            body.updateOrbit();
        }
    }

    public void adjustOrbitVelocity(CelestialBody moon, CelestialBody earth, float maxOrbitDistance, float minOrbitDistance) {
        Vector2 centerToBody = moon.body.getPosition().sub(earth.body.getPosition());
        float currentDistance = centerToBody.len();

        // Calculate the tangent velocity for the orbiting body
        Vector2 tangentVelocity = new Vector2(-centerToBody.y, centerToBody.x).nor().scl(moon.getOrbitSpeed());

        // Adjust velocity if the current distance is outside the allowed range
        if (currentDistance > maxOrbitDistance) {
            // Calculate a correction vector that points towards the central body to decrease distance
            Vector2 correctionVector = centerToBody.scl(-1).nor().scl(moon.getOrbitSpeed() * 0.5f); // Apply a gentle pull towards the central body
            tangentVelocity.add(correctionVector);
        } else if (currentDistance < minOrbitDistance) {
            // Calculate a correction vector that points away from the central body to increase distance
            Vector2 correctionVector = centerToBody.nor().scl(moon.getOrbitSpeed() * 0.5f); // Apply a gentle push away from the central body
            tangentVelocity.add(correctionVector);
        }

        // Adjust the orbiting body's velocity, considering the central body's movement as well
        Vector2 adjustedVelocity = tangentVelocity.add(earth.body.getLinearVelocity());
        moon.body.setLinearVelocity(adjustedVelocity.x, adjustedVelocity.y);
    }

    private void drawSolarSystem() {
        for (CelestialBody body : celestialBodies) {
            body.draw(stateTime);
        }
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

    public Array<CelestialBody> getCelestialBodies() {
        return celestialBodies;
    }
}