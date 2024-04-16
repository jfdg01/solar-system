// CelestialBodyFactory.java
package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class CelestialBodyFactory {
    private final AssetManager assetManager;
    private final Group planetGroup;
    private final Array<CelestialBodyActor> celestialBodies;

    public CelestialBodyFactory(AssetManager assetManager, Group planetGroup, Array<CelestialBodyActor> celestialBodies) {
        this.assetManager = assetManager;
        this.planetGroup = planetGroup;
        this.celestialBodies = celestialBodies;
    }

    public CelestialBodyActor createSun() {
        float sunRadius = Constants.Radius.SUN_PIXELS;
        Animation<TextureRegion> sunAnimation = createAnimationFromAssetManager("sun");

        float sunX = (Gdx.graphics.getWidth() / 2f) - sunRadius;
        float sunY = (Gdx.graphics.getHeight() / 2f) - sunRadius;

        CelestialBodyActor sun = new CelestialBodyActor("sun", sunRadius, sunAnimation, null, 0, 0);

        sun.setPosition(sunX, sunY);
        planetGroup.addActor(sun);
        sun.setZIndex(2);
        celestialBodies.add(sun);
        Constants.CelestialBody.SUN.ordinal();

        return sun;
    }

    public CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                           float orbitSpeed, String texturePathSuffix) {
        return createPlanet(distanceToOrbitedBody, radiusPixels, orbitSpeed, texturePathSuffix, celestialBodies.get(Constants.CelestialBody.SUN.ordinal()));
    }

    public CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                           float orbitSpeed, String texturePathSuffix, CelestialBodyActor orbitedBody) {

        float orbitedBodyX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
        float orbitedBodyY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

        float planetX = orbitedBodyX + distanceToOrbitedBody;
        float planetY = orbitedBodyY - radiusPixels;

        Animation<TextureRegion> animation = createAnimationFromAssetManager(texturePathSuffix);

        CelestialBodyActor planet = new CelestialBodyActor(texturePathSuffix, radiusPixels, animation, orbitedBody, distanceToOrbitedBody, orbitSpeed);

        planet.setPosition(planetX, planetY);

        planetGroup.addActor(planet);
        celestialBodies.add(planet);

        return planet;
    }

    private Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
        String name = "sprites/anim/" + regionName + ".png";
        Texture texture = assetManager.get(name, Texture.class);

        int frameWidth = texture.getWidth() / Constants.Animation.NUM_COLS;
        int frameHeight = texture.getHeight() / Constants.Animation.NUM_ROWS;

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[Constants.Animation.NUM_ROWS * Constants.Animation.NUM_COLS];

        int index = 0;
        for (int i = 0; i < Constants.Animation.NUM_ROWS; i++) {
            for (int j = 0; j < Constants.Animation.NUM_COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<TextureRegion>(Constants.Animation.FRAME_DURATION, animationFrames);
    }
}