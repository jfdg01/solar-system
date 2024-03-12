package com.kandclay;

public class Constants {
    public static final float ZOOM_IN_FACTOR = 0.9f;
    public static final float ZOOM_OUT_FACTOR = 1.1f;
    public static final int PIXELS_TO_METERS = 50;
    public static final int PHYSICS_TIME_STEP = 240;
    public static final int PHYSICS_VELOCITY_ITERATIONS = 6;
    public static final int PHYSICS_POSITION_ITERATIONS = 2;
    public static final float VIEWPORT_WIDTH_PIXELS_INIT = 400;
    public static final float VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    public static final float CAMERA_MOVE_SPEED = 400f;
    public static final float LERP_FACTOR = 8f / 100f;

    // Animation
    public static final int ANIMATION_NUM_ROWS = 32;
    public static final int ANIMATION_NUM_COLS = 32;
    public static final float FRAME_DURATION = 3f / 100f;

    // Sun
    public static final float SUN_RADIUS_PIXELS = 100;

    // Earth
    public static final float EARTH_ORBIT_SPEED = 2;
    public static final float EARTH_RADIUS_PIXELS = 16;
    public static final float EARTH_DISTANCE_TO_SUN_PIXELS = 400;

    // Saturn
    public static final float SATURN_ORBIT_SPEED = 1.5f;
    public static final float SATURN_RADIUS_PIXELS = 80;
    public static final float SATURN_DISTANCE_TO_SUN_PIXELS = 1200;

    // Moon
    static final float MOON_ORBIT_SPEED = 3f;
    static final float MOON_RADIUS_PIXELS = 6;
    static final float MOON_DISTANCE_TO_EARTH_PIXELS = 150;

    static final int SUN = 0;
    static final int EARTH = 1;
    static final int SATURN = 2;
    static final int MOON = 3;
}
