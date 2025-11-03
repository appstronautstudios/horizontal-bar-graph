package com.appstronautstudios.horizontalbargraph;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

public class HBGUtils {

    private static final int STEPS_BETWEEN_COLOURS = 3;

    /**
     * @param steps number of steps in between the two
     * @return array of colour ints that smoothly blend between red and green
     */
    public static int[] generateRedToGreenGradient(int steps) {
        return generateGradient(Color.RED, Color.GREEN, steps);
    }

    /**
     * @param startColor int of starting colour
     * @param endColor   int of ending colour
     * @param steps      number of steps in between the two
     * @return - array of colour ints that smoothly blend between the start and end colour
     */
    public static int[] generateGradient(int startColor, int endColor, int steps) {
        int[] colours = new int[steps];
        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / (steps - 1);
            colours[i] = ColorUtils.blendARGB(startColor, endColor, ratio);
        }
        return colours;
    }

    /**
     * @param startColor int of starting colour
     * @param endColor   int of ending colour
     * @param steps      number of steps in between the two
     * @return - array of colour ints that rainbow blend between the start and end colour
     */
    public static int[] generateRainbowGradient(int startColor, int endColor, int steps) {
        int[] colours = new int[steps];

        float[] startHSV = new float[3];
        float[] endHSV = new float[3];
        Color.colorToHSV(startColor, startHSV);
        Color.colorToHSV(endColor, endHSV);

        // handle hue wrap-around properly (e.g. 350° to 10° should go through 0°)
        float hueDiff = endHSV[0] - startHSV[0];
        if (hueDiff > 180) hueDiff -= 360;
        if (hueDiff < -180) hueDiff += 360;

        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / (steps - 1);
            float hue = (startHSV[0] + hueDiff * ratio + 360) % 360;
            float saturation = startHSV[1] + (endHSV[1] - startHSV[1]) * ratio;
            float value = startHSV[2] + (endHSV[2] - startHSV[2]) * ratio;
            colours[i] = Color.HSVToColor(new float[]{hue, saturation, value});
        }

        return colours;
    }
}
