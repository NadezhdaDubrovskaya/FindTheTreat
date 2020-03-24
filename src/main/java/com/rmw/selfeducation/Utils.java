package com.rmw.selfeducation;

import java.util.Random;

public class Utils {

    private Utils() {
    }

    private static final Random RANDOM = new Random();

    public static int getRandomInt(final int max) {
        return RANDOM.nextInt(max);
    }

    public static float generateRandomWeight() {
        return RANDOM.nextFloat() * 2f - 1f;
    }

    public static int getRandomNumberInRange(final int min, final int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return RANDOM.nextInt((max - min) + 1) + min;
    }

}
