package com.kandclay;

public class Constants {
    public static final float ZOOM_IN_FACTOR = 0.9f;
    public static final float ZOOM_OUT_FACTOR = 1.1f;
    public static final int PHYSICS_TIME_STEP = 240;
    public static final int PHYSICS_VELOCITY_ITERATIONS = 6;
    public static final int PHYSICS_POSITION_ITERATIONS = 2;
    public static final float VIEWPORT_WIDTH_PIXELS_INIT = 400;
    public static final float VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    public static final float CAMERA_MOVE_SPEED = 400f;
    public static final float LERP_FACTOR = 8f / 100f;

    // Animation
    public static final int ANIMATION_NUM_ROWS = 28;
    public static final int ANIMATION_NUM_COLS = 28;
    public static final float FRAME_DURATION = 5f / 100f;
    public static final float SPEED = 50f;

    // Radius in pixels
    public static final float SUN_RADIUS_PIXELS = 200;
    public static final float MERCURY_RADIUS_PIXELS = 8; // Smaller than Earth
    public static final float VENUS_RADIUS_PIXELS = 15; // Slightly smaller than Earth
    public static final float EARTH_RADIUS_PIXELS = 16; // Original value
    public static final float MARS_RADIUS_PIXELS = 12; // Smaller than Earth
    public static final float JUPITER_RADIUS_PIXELS = 60; // Larger than Saturn, smaller than the Sun
    public static final float SATURN_RADIUS_PIXELS = 80; // Adjusted to fit between Jupiter and Uranus
    public static final float URANUS_RADIUS_PIXELS = 60; // Adjusted to fit between Saturn and Neptune
    public static final float NEPTUNE_RADIUS_PIXELS = 58; // Slightly smaller than Uranus
    public static final float MOON_RADIUS_PIXELS = 6; // Relative to Earth

    // Orbit speed
    public static final float MERCURY_ORBIT_SPEED = SPEED * 4f; // Faster than Earth
    public static final float VENUS_ORBIT_SPEED = SPEED * 3.5f; // Slightly faster than Earth
    public static final float EARTH_ORBIT_SPEED = SPEED * 2f; // Original value
    public static final float MARS_ORBIT_SPEED = SPEED * 1.8f; // Slightly slower than Earth
    public static final float JUPITER_ORBIT_SPEED = SPEED * 1.3f; // Between Saturn and Uranus
    public static final float SATURN_ORBIT_SPEED = SPEED * 1.5f; // Original value
    public static final float URANUS_ORBIT_SPEED = SPEED * 1.2f; // Slower, further than Saturn
    public static final float NEPTUNE_ORBIT_SPEED = SPEED * 1.1f; // The slowest, further than Uranus
    public static final float MOON_ORBIT_SPEED = SPEED * 3f; // Relative to Earth

    // Distance to another celestial body in pixels
    public static final float MERCURY_DISTANCE_TO_SUN_PIXELS = 200 + SUN_RADIUS_PIXELS; // Closest to the Sun
    public static final float VENUS_DISTANCE_TO_SUN_PIXELS = 300 + SUN_RADIUS_PIXELS; // Closer than Earth
    public static final float EARTH_DISTANCE_TO_SUN_PIXELS = 500 + SUN_RADIUS_PIXELS; // Original value
    public static final float MARS_DISTANCE_TO_SUN_PIXELS = 800 + SUN_RADIUS_PIXELS; // Slightly further than Earth
    public static final float JUPITER_DISTANCE_TO_SUN_PIXELS = 1000 + SUN_RADIUS_PIXELS; // Between Mars and Saturn
    public static final float SATURN_DISTANCE_TO_SUN_PIXELS = 1500 + SUN_RADIUS_PIXELS; // Original value
    public static final float URANUS_DISTANCE_TO_SUN_PIXELS = 1900 + SUN_RADIUS_PIXELS; // Adjusted for order
    public static final float NEPTUNE_DISTANCE_TO_SUN_PIXELS = 2400 + SUN_RADIUS_PIXELS; // The farthest listed
    public static final float MOON_DISTANCE_TO_EARTH_PIXELS = 100 + EARTH_RADIUS_PIXELS; // Relative to Earth

    // Enum
    public static int SUN = 0;
    public static int MERCURY = 1;
    public static int VENUS = 2;
    public static int EARTH = 3;
    public static int MOON = 4;
    public static int MARS = 5;
    public static int JUPITER = 6;
    public static int SATURN = 7;
    public static int URANUS = 8;
    public static int NEPTUNE = 9;

}
