package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class CelestialBody {
    public Body body;
    private Texture texture;
    private TextureRegion region;
    private float radius;
    private float orbitSpeed;

    public CelestialBody(World world, String texturePath, BodyDef.BodyType bodyType, float radius, float orbitSpeed, Vector2 position, float density) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.region = new TextureRegion(texture);
        this.radius = radius;
        this.orbitSpeed = orbitSpeed;
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
}

