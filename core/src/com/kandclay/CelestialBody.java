package com.kandclay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;

public class CelestialBody {
    public Body body;
    private float radius;
    private float orbitSpeed;
    private Animation<TextureRegion> animation;
    private final Batch spriteBatch;

    public CelestialBody(World world, BodyDef.BodyType bodyType, float radius, float orbitSpeed, Vector2 position,
                         Batch spriteBatch, Animation<TextureRegion> animation) {
        this.radius = radius;
        this.orbitSpeed = orbitSpeed;
        this.spriteBatch = spriteBatch;
        this.animation = animation;
        createBody(world, bodyType, radius, position);
    }


    private void createBody(World world, BodyDef.BodyType bodyType, float radius, Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = (float) 1.0;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void draw(float stateTime) {

        TextureRegion currentRegion = this.animation.getKeyFrame(stateTime, true);

        // Calculate the drawing parameters.
        Vector2 position = this.body.getPosition();
        float scalePixels = this.radius * Constants.PIXELS_TO_METERS * 2;
        float scale = scalePixels > 0 ? (scalePixels * 2) / currentRegion.getRegionWidth() : 1;
        float screenX = position.x * Constants.PIXELS_TO_METERS - currentRegion.getRegionWidth() / 2f;
        float screenY = position.y * Constants.PIXELS_TO_METERS - currentRegion.getRegionHeight() / 2f;
        float rotation = MathUtils.radiansToDegrees * this.body.getAngle();

        spriteBatch.draw(currentRegion, screenX, screenY, currentRegion.getRegionWidth() / 2f, currentRegion.getRegionHeight() / 2f,
                currentRegion.getRegionWidth(), currentRegion.getRegionHeight(), scale, scale, rotation);
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

}

