package com.kandclay;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Constants {
    public static final float ZOOM_IN_FACTOR = 0.9f;
    public static final float ZOOM_OUT_FACTOR = 1.1f;
    public static final int PIXELS_TO_METERS = 50;
    public static final int PHYSICS_TIME_STEP = 60;
    public static final int PHYSICS_VELOCITY_ITERATIONS = 6;
    public static final int PHYSICS_POSITION_ITERATIONS = 2;
    public static final float VIEWPORT_WIDTH_PIXELS_INIT = 400;
    public static final float VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    public static final float NO_SCALING = -1;

    // Animation
    public static final int ANIMATION_NUM_ROWS = 16;
    public static final int ANIMATION_NUM_COLS = 15;
    public static final float FRAME_DURATION = 0.1f;

    // Sun
    public static final float SUN_RADIUS_PIXELS = 100;

    // Earth
    public static final float EARTH_ORBIT_SPEED = 2;
    public static final float EARTH_RADIUS_PIXELS = 16;
    public static final float EARTH_DISTANCE_TO_SUN_PIXELS = 500;
    // Ice
    public static final float ICE_ORBIT_SPEED = 1.5f;
    public static final float ICE_RADIUS_PIXELS = 16;
    public static final float ICE_DISTANCE_TO_SUN_PIXELS = 750;

    // Moon
    static final float MOON_ORBIT_SPEED = 3f;
    static final float MOON_RADIUS_PIXELS = 5;
    static final float MOON_DISTANCE_TO_EARTH_PIXELS = 50;
}
