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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Objects;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private final AssetManager assetManager;
    private CameraController cameraController;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private Stage uiStage;
    private ScreenViewport uiViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    private final Array<CelestialBodyActor> celestialBodies;
    String backgroundPath;
    private Group planetGroup;
    private Skin skin;
    private Slider orbitAngleSlider;
    private Slider orbitCurrentAngleSlider;
    private TextButton speed05xButton;
    private TextButton speed1xButton;
    private TextButton speed2xButton;
    private TextButton speed5xButton;
    private TextButton speed10xButton;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        celestialBodies = new Array<CelestialBodyActor>();
        planetGroup = new Group();

        loadAssets();
    }

    private void loadAssets() {
        backgroundPath = "sprites/static/backgroundSimple.png";

        assetManager.load(backgroundPath, Texture.class);
        assetManager.load("skin/default/skin/uiskin.json", Skin.class);
        assetManager.load("sprites/anim/earth.png", Texture.class);
        assetManager.load("sprites/anim/saturn.png", Texture.class);
        assetManager.load("sprites/anim/moon.png", Texture.class);
        assetManager.load("sprites/anim/sun.png", Texture.class);
        assetManager.load("sprites/anim/uranus.png", Texture.class);
        assetManager.load("sprites/anim/venus.png", Texture.class);
        assetManager.load("sprites/anim/jupiter.png", Texture.class);
        assetManager.load("sprites/anim/mars.png", Texture.class);
        assetManager.load("sprites/anim/neptune.png", Texture.class);
        assetManager.load("sprites/anim/mercury.png", Texture.class);
        assetManager.load("sprites/static/ice.png", Texture.class);

        assetManager.finishLoading();
    }

    @Override
    public void show() {
        initializeCameraAndViewport();
        initializeStage();
        initializeBackground();
        skin = assetManager.get("skin/default/skin/uiskin.json");
        initializeUI();

        setupInputProcessors();
        createSolarSystem();
    }

    private void initializeUI() {
        uiViewport = new ScreenViewport();
        uiStage = new Stage(uiViewport, spriteBatch);
        initializeOrbitalPlaneSlider();
        initializeOrbitCurrentAngleSlider();
        initializeSpeedButtons();
    }

    private void initializeSpeedButtons() {
        speed05xButton = new TextButton("0.5x", skin);
        speed1xButton = new TextButton("1x", skin);
        speed2xButton = new TextButton("2x", skin);
        speed5xButton = new TextButton("5x", skin);
        speed10xButton = new TextButton("10x", skin);

        TextButton[] buttons = {speed05xButton, speed1xButton, speed2xButton, speed5xButton, speed10xButton};
        float[] speeds = {0.5f, 1f, 2f, 5f, 10f};

        for (int i = 0; i < buttons.length; i++) {
            TextButton button = buttons[i];
            float speed = speeds[i];

            button.setSize(100, 30);
            button.setPosition(i * (button.getWidth() + 10), 0); // 10 is the gap between the buttons

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    for (CelestialBodyActor body : celestialBodies) {
                        if (!Objects.equals(body.getName(), "sun")) {
                            body.setOrbitSpeed(body.getBaseOrbitSpeed() * speed);
                        }
                    }
                }
            });

            uiStage.addActor(button);
        }
    }

    private void initializeOrbitalPlaneSlider() {
        orbitAngleSlider = new Slider(0, 1, 0.01f, false, skin);
        orbitAngleSlider.setValue(1f);
        // Height seems to be not taken into account
        orbitAngleSlider.setSize(200, 20);
        // Position the slider at the top left corner of the screen
        orbitAngleSlider.setPosition(0, Gdx.graphics.getHeight() - orbitAngleSlider.getHeight());
        orbitAngleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float targetEllipseAxisRatio = orbitAngleSlider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    body.setTargetEllipseAxisRatio(targetEllipseAxisRatio);
                }
            }
        });

        uiStage.addActor(orbitAngleSlider);
    }

    private void initializeOrbitCurrentAngleSlider() {
        orbitCurrentAngleSlider = new Slider(1f, 1.01f, 0.0001f, false, skin);
        orbitCurrentAngleSlider.setValue(0f);
        orbitCurrentAngleSlider.setSize(200, 20);
        orbitCurrentAngleSlider.setPosition(0, orbitAngleSlider.getY() - orbitCurrentAngleSlider.getHeight() - 10); // 10 is the gap between the two sliders
        orbitCurrentAngleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float multiplier = orbitCurrentAngleSlider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    if (!Objects.equals(body.getName(), "sun")) {
                        float currentOrbitAngle = body.getCurrentOrbitAngleRadians();
                        body.setCurrentOrbitAngleRadians(currentOrbitAngle * multiplier);
                    }
                }
            }
        });

        uiStage.addActor(orbitCurrentAngleSlider);
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
        cameraController = new CameraController(worldCamera, CAMERA_MOVE_SPEED, ZOOM_IN_FACTOR, ZOOM_OUT_FACTOR, this.getCelestialBodies());
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void initializeBackground() {
        Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);

        Background backgroundActor = new Background(backgroundTexture, worldCamera);

        backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());

        backgroundActor.setPosition(0, 0);

        // Set lowest Zindex
        backgroundActor.toBack();

        worldStage.addActor(backgroundActor);
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(worldStage);
        multiplexer.addProcessor(uiStage);
        // Add other input processors as needed
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        CelestialBodyActor sun = createSun();

        CelestialBodyActor mercury = createPlanet(MERCURY_DISTANCE_TO_SUN_PIXELS, MERCURY_RADIUS_PIXELS, MERCURY_ORBIT_SPEED, "mercury");
        MERCURY = celestialBodies.indexOf(mercury, true);
        CelestialBodyActor venus = createPlanet(VENUS_DISTANCE_TO_SUN_PIXELS, VENUS_RADIUS_PIXELS, VENUS_ORBIT_SPEED, "venus");
        VENUS = celestialBodies.indexOf(venus, true);
        CelestialBodyActor earth = createPlanet(EARTH_DISTANCE_TO_SUN_PIXELS, EARTH_RADIUS_PIXELS, EARTH_ORBIT_SPEED, "earth");
        EARTH = celestialBodies.indexOf(earth, true);
        // Moon orbiting Earth
        CelestialBodyActor moon = createPlanet(MOON_DISTANCE_TO_EARTH_PIXELS, MOON_RADIUS_PIXELS, MOON_ORBIT_SPEED, "moon", earth);
        MOON = celestialBodies.indexOf(moon, true);
        CelestialBodyActor mars = createPlanet(MARS_DISTANCE_TO_SUN_PIXELS, MARS_RADIUS_PIXELS, MARS_ORBIT_SPEED, "mars");
        MARS = celestialBodies.indexOf(mars, true);
        CelestialBodyActor jupiter = createPlanet(JUPITER_DISTANCE_TO_SUN_PIXELS, JUPITER_RADIUS_PIXELS, JUPITER_ORBIT_SPEED, "jupiter");
        JUPITER = celestialBodies.indexOf(jupiter, true);
        CelestialBodyActor saturn = createPlanet(SATURN_DISTANCE_TO_SUN_PIXELS, SATURN_RADIUS_PIXELS, SATURN_ORBIT_SPEED, "saturn", sun);
        SATURN = celestialBodies.indexOf(saturn, true);
        CelestialBodyActor uranus = createPlanet(URANUS_DISTANCE_TO_SUN_PIXELS, URANUS_RADIUS_PIXELS, URANUS_ORBIT_SPEED, "uranus");
        URANUS = celestialBodies.indexOf(uranus, true);
        CelestialBodyActor neptune = createPlanet(NEPTUNE_DISTANCE_TO_SUN_PIXELS, NEPTUNE_RADIUS_PIXELS, NEPTUNE_ORBIT_SPEED, "neptune");
        NEPTUNE = celestialBodies.indexOf(neptune, true);

        worldStage.addActor(planetGroup);
    }

    private CelestialBodyActor createSun() {
        float sunRadius = SUN_RADIUS_PIXELS;
        Animation<TextureRegion> sunAnimation = createAnimationFromAssetManager("sun");

        // Calculate the sun's position to be at the center of the screen
        float sunX = (Gdx.graphics.getWidth() / 2f) - sunRadius; // Center the sun on the screen
        float sunY = (Gdx.graphics.getHeight() / 2f) - sunRadius; // Center the sun on the screen

        CelestialBodyActor sun = new CelestialBodyActor("sun", sunRadius, sunAnimation, null, 0, 0);

        sun.setPosition(sunX, sunY);
        planetGroup.addActor(sun);
        sun.setZIndex(2);
        celestialBodies.add(sun);
        SUN = celestialBodies.indexOf(sun, true);

        return sun;
    }

    // Reduce visual clutter
    private CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                            float orbitSpeed, String texturePathSuffix) {
        return createPlanet(distanceToOrbitedBody, radiusPixels, orbitSpeed, texturePathSuffix, celestialBodies.get(SUN));
    }

    private CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                            float orbitSpeed, String texturePathSuffix, CelestialBodyActor orbitedBody) {

        float orbitedBodyX = orbitedBody.getX() + orbitedBody.getWidth() / 2; // Center X of the orbited body
        float orbitedBodyY = orbitedBody.getY() + orbitedBody.getHeight() / 2; // Center Y of the orbited body

        // For simplicity, initially place the planet right of its orbited body. Adjust as needed for actual orbit.
        float planetX = orbitedBodyX + distanceToOrbitedBody;
        float planetY = orbitedBodyY - radiusPixels; // Adjust if you want the initial position differently

        Animation<TextureRegion> animation = createAnimationFromAssetManager(texturePathSuffix);

        // Create the planet CelestialBody without needing Box2D physics parameters
        CelestialBodyActor planet = new CelestialBodyActor(texturePathSuffix, radiusPixels, animation, orbitedBody, distanceToOrbitedBody, orbitSpeed);

        // Set the planet's position
        planet.setPosition(planetX, planetY);

        // Add the planet to the stage and celestial bodies list
        planetGroup.addActor(planet);
        celestialBodies.add(planet);

        return planet;
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

    @Override
    public void render(float delta) {

        clearScreen();

        uiStage.act(delta);
        updateWorldStage(delta);
        cameraController.update(delta);

        worldStage.draw(); // First draw the world stage
        uiStage.draw(); // Then draw the UI on top
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    private void updateWorldStage(float delta) {
        worldStage.act(delta);
        worldCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        uiViewport.update(width, height, true);

        Actor backgroundActor = worldStage.getActors().first();
        if (backgroundActor instanceof Background) {
            backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
        }
    }

    @Override
    public void dispose() {
        worldStage.dispose();
        uiStage.dispose();
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

    public Array<CelestialBodyActor> getCelestialBodies() {
        return celestialBodies;
    }
}