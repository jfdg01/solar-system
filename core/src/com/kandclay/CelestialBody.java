package com.kandclay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.kandclay.Constants.*;

public class CelestialBody extends Actor {

    public String name;
    public Body body;
    private float radius;
    private float orbitSpeed;
    private Animation<TextureRegion> animation;
    private final Batch spriteBatch;
    private final float distanceToAnchorBody;
    private final CelestialBody orbitedBody;


    public CelestialBody(World world, String name, BodyDef.BodyType bodyType, float radius, float orbitSpeed, Vector2 position,
                         Batch spriteBatch, Animation<TextureRegion> animation, CelestialBody orbitedBody, float distanceToAnchorBody) {
        this.name = name;
        this.radius = radius;
        this.orbitSpeed = orbitSpeed;
        this.spriteBatch = spriteBatch;
        this.animation = animation;
        this.distanceToAnchorBody = distanceToAnchorBody;
        this.orbitedBody = orbitedBody;
        createBody(world, bodyType, radius, position);
    }

    public void updateOrbit() {
        if (this.orbitedBody != null) { // Ensure there is a central body to orbit around
            float maxOrbitDistance = this.distanceToAnchorBody * 1.1f;
            float minOrbitDistance = this.distanceToAnchorBody * 0.9f;
            adjustOrbitVelocity(this.orbitedBody, maxOrbitDistance, minOrbitDistance);
        }
    }

    public void addClickListener() {
        System.out.printf("Adding click listener to " + this.name + "\n");
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Celestial body clicked!");
            }
        });
    }

    public void adjustOrbitVelocity(CelestialBody centralBody, float maxOrbitDistance, float minOrbitDistance) {
        if (centralBody == null) {
            return;
        }

        Vector2 centerToBody = this.body.getPosition().sub(centralBody.body.getPosition());
        float currentDistance = centerToBody.len();

        Vector2 tangentVelocity = new Vector2(-centerToBody.y, centerToBody.x).nor().scl(this.getOrbitSpeed());

        if (currentDistance > maxOrbitDistance) {
            Vector2 correctionVector = centerToBody.scl(-1).nor().scl(this.getOrbitSpeed() * 0.5f);
            tangentVelocity.add(correctionVector);
        } else if (currentDistance < minOrbitDistance) {
            Vector2 correctionVector = centerToBody.nor().scl(this.getOrbitSpeed() * 0.5f);
            tangentVelocity.add(correctionVector);
        }

        Vector2 adjustedVelocity = tangentVelocity.add(centralBody.body.getLinearVelocity());
        this.body.setLinearVelocity(adjustedVelocity.x, adjustedVelocity.y);
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

    public void draw(float stateTime, float scaleFactor) {

        TextureRegion currentRegion = this.animation.getKeyFrame(stateTime, true);

        // Calculate the drawing parameters.
        Vector2 position = this.body.getPosition();
        float scalePixels = this.radius * scaleFactor;
        float scale = scalePixels > 0 ? (scalePixels * 2) / currentRegion.getRegionWidth() : 1;
        float screenX = position.x - currentRegion.getRegionWidth() / 2f;
        float screenY = position.y - currentRegion.getRegionHeight() / 2f;
        float rotation = MathUtils.radiansToDegrees * this.body.getAngle();

        spriteBatch.draw(currentRegion, screenX, screenY, currentRegion.getRegionWidth() / 2f, currentRegion.getRegionHeight() / 2f,
                currentRegion.getRegionWidth(), currentRegion.getRegionHeight(), scale, scale, rotation);
    }

    public void draw(float stateTime) {

        TextureRegion currentRegion = this.animation.getKeyFrame(stateTime, true);

        // Calculate the drawing parameters.
        Vector2 position = this.body.getPosition();
        float scalePixels = this.radius;
        float scale = scalePixels > 0 ? (scalePixels * 2) / currentRegion.getRegionWidth() : 1;
        float screenX = position.x - currentRegion.getRegionWidth() / 2f;
        float screenY = position.y - currentRegion.getRegionHeight() / 2f;
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

    public float getDistanceToAnchorBody() {
        return distanceToAnchorBody;
    }

    @Override
    public String toString() {
        return name + "{" +
                "radius=" + radius +
                ", orbitSpeed=" + orbitSpeed +
                ", distanceToAnchorBody=" + distanceToAnchorBody +
                '}';
    }

    public void addListener(ClickListener clickListener) {
    }
}

