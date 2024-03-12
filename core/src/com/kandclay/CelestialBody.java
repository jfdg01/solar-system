package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;

public class CelestialBody {
    public Body body;
    private Texture texture;
    private TextureRegion region;
    private float radius;
    private float orbitSpeed;
    private Animation<TextureRegion> animation;
    private Texture animationTexture;
    private Batch spriteBatch;

    public CelestialBody(World world, BodyDef.BodyType bodyType, float radius, float orbitSpeed, Vector2 position,
                         float density, Batch spriteBatch, TextureRegion region) {
        this.radius = radius;
        this.orbitSpeed = orbitSpeed;
        this.spriteBatch = spriteBatch;
        this.region = region;
        createBody(world, bodyType, radius, position, density);
    }

    private void createBody(World world, BodyDef.BodyType bodyType, float radius, Vector2 position, float density) {
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

    public void draw(float stateTime) {
        if (this.getAnimation() != null) {
            TextureRegion currentFrame = this.getAnimation().getKeyFrame(stateTime, true);
            this.setRegion(currentFrame);
        }

        Vector2 position = this.body.getPosition();
        TextureRegion region = this.getRegion();
        float scalePixels = this.getRadius() * Constants.PIXELS_TO_METERS * 2;
        float scale = scalePixels > 0 ? (scalePixels * 2) / region.getRegionWidth() : 1;
        float screenX = position.x * Constants.PIXELS_TO_METERS - region.getRegionWidth() / 2f;
        float screenY = position.y * Constants.PIXELS_TO_METERS - region.getRegionHeight() / 2f;
        float rotation = MathUtils.radiansToDegrees * this.body.getAngle();

        spriteBatch.draw(region, screenX, screenY, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f,
                region.getRegionWidth(), region.getRegionHeight(), scale, scale, rotation);
    }

    public TextureRegion getRegion() {
        return region;
    }

    public float getOrbitSpeed() {
        return orbitSpeed;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOrbitSpeed(float orbitSpeed) {
        this.orbitSpeed = orbitSpeed;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public Texture getAnimationTexture() {
        return animationTexture;
    }

    public void setAnimationTexture(Texture sunAnimTexture) {
        this.animationTexture = sunAnimTexture;
    }
}

