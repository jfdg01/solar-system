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
    private World world;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;

    // Planetary bodies
    public class CelestialBody {
        public Body body;
        private Texture texture;
        private TextureRegion region;
        private float radius;
        private float orbitSpeed;

        public CelestialBody(String texturePath, BodyDef.BodyType bodyType, float radius, float orbitSpeed, Vector2 position, float density) {
            this.texture = new Texture(Gdx.files.internal(texturePath));
            this.region = new TextureRegion(texture);
            this.radius = radius;
            this.orbitSpeed = orbitSpeed;
            createBody(bodyType, radius, position, density);
        }

        private void createBody(BodyDef.BodyType bodyType, float radius, Vector2 position, float density) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = bodyType;
            bodyDef.position.set(position);

            body = world.createBody(bodyDef);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;

            body.createFixture(fixtureDef);
            shape.dispose();
        }

        public void draw() {
            Vector2 bodyPos = body.getPosition();
            float rotation = MathUtils.radiansToDegrees * body.getAngle();
            float scale = (float) (radius * 2) / region.getRegionWidth();
            float screenX = bodyPos.x * PIXELS_TO_METERS - region.getRegionWidth() / 2f;
            float screenY = bodyPos.y * PIXELS_TO_METERS - region.getRegionHeight() / 2f;

            spriteBatch.draw(region, screenX, screenY, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), scale, scale, rotation);
        }
    }

    private CelestialBody sun, earth, icePlanet, moon;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
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

    private void createSolarSystem() {
        Vector2 sunPosition = new Vector2(Gdx.graphics.getWidth() / 2f / PIXELS_TO_METERS, Gdx.graphics.getHeight() / 2f / PIXELS_TO_METERS);
        sun = new CelestialBody("sprites/Lava.png", BodyDef.BodyType.StaticBody, SUN_RADIUS_PIXELS / PIXELS_TO_METERS, 0, sunPosition, 1f);

        Vector2 earthPosition = sun.body.getPosition().cpy().add(EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, 0);
        earth = new CelestialBody("sprites/Terran.png", BodyDef.BodyType.DynamicBody, EARTH_RADIUS_PIXELS / PIXELS_TO_METERS, EARTH_ORBIT_SPEED, earthPosition, 1f);

        Vector2 icePlanetPosition = sun.body.getPosition().cpy().add(ICE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, 0);
        icePlanet = new CelestialBody("sprites/Ice.png", BodyDef.BodyType.DynamicBody, ICE_RADIUS_PIXELS / PIXELS_TO_METERS, ICE_ORBIT_SPEED, icePlanetPosition, 1f);

        Vector2 moonPosition = earth.body.getPosition().cpy().add(MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS, 0);
        moon = new CelestialBody("sprites/Baren.png", BodyDef.BodyType.DynamicBody, MOON_RADIUS_PIXELS / PIXELS_TO_METERS, MOON_ORBIT_SPEED, moonPosition, 1f);
    }

    public void manageOrbits() {
        adjustOrbitVelocity(moon, earth, MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(earth, sun, EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(icePlanet, sun, ICE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
    }

    public void adjustOrbitVelocity(CelestialBody orbitingBody, CelestialBody centralBody, float desiredOrbitDistance) {
        Vector2 centerToBody = orbitingBody.body.getPosition().sub(centralBody.body.getPosition());
        float currentDistance = centerToBody.len();

        // Calculate the tangent velocity for a stable orbit
        Vector2 tangentVelocity = new Vector2(-centerToBody.y, centerToBody.x).nor().scl(orbitingBody.orbitSpeed);

        // If the moon is drifting away, adjust the speed to bring it closer, or slow it down if it's too close
        if (currentDistance > desiredOrbitDistance) {
            tangentVelocity.scl((desiredOrbitDistance / currentDistance));
        } else if (currentDistance < desiredOrbitDistance) {
            tangentVelocity.scl((currentDistance / desiredOrbitDistance));
        }

        Vector2 adjustedVelocity = tangentVelocity.add(centralBody.body.getLinearVelocity());
        orbitingBody.body.setLinearVelocity(adjustedVelocity.x, adjustedVelocity.y);
    }


    private void drawSolarSystem() {
        drawSun();
        drawEarth();
        drawIce();
        drawMoon();
    }

    @Override
    public void render(float delta) {
        manageOrbits();
        clearScreen();
        stepWorld(delta);

        spriteBatch.begin();
        drawSolarSystem();
        spriteBatch.end();

        updateWorldStage(delta);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void stepWorld(float delta) {
        float PHYSICS_STEP_PER_SECOND = 60;
        world.step(1f / PHYSICS_STEP_PER_SECOND, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
    }

    private void drawSun() {
        Vector2 bodyPos = sun.body.getPosition();
        float rotation = MathUtils.radiansToDegrees * sun.body.getAngle();
        drawCelestialBody(sun.region, bodyPos, rotation, SUN_RADIUS_PIXELS);
    }

    private void drawEarth() {
        Vector2 bodyPos = earth.body.getPosition();
        drawCelestialBody(earth.region, bodyPos, 0, EARTH_RADIUS_PIXELS);
    }

    private void drawIce() {
        Vector2 bodyPos = icePlanet.body.getPosition();
        drawCelestialBody(icePlanet.region, bodyPos, 0, ICE_RADIUS_PIXELS);
    }

    private void drawMoon() {
        Vector2 bodyPos = moon.body.getPosition();
        drawCelestialBody(moon.region, bodyPos, 0, MOON_RADIUS_PIXELS);
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
        sun.texture.dispose();
        earth.texture.dispose();
        icePlanet.texture.dispose();
        moon.texture.dispose();
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