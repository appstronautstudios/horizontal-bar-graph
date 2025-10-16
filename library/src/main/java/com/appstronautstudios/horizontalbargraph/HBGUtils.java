package com.appstronautstudios.horizontalbargraph;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Random;

public class HBGUtils {

    private static final String[] COLOUR_SPECTRUM = new String[]{"#80C2E9", "#79e9d6", "#71e995", "#89e96a", "#cce963", "#e9bc5b", "#e96954"};
    private static final int STEPS_BETWEEN_COLOURS = 3;

    /**
     * @param size - size of set desired
     * @return - array of colour ints taken from the default lib colour set. If the desired array
     * is larger than the size of default lib colour set it will loop back through the set as many
     * times as necessary
     */
    public static int[] generateDefaultColourSet(int size) {
        int[] colours = new int[size];
        final float colourIncrement = 1.0f / (STEPS_BETWEEN_COLOURS - 1);
        float colourDiff = 0;
        int colourIndex = 0;
        int stepCounter = 0;

        for (int i = 0; i < size; i++) {
            // calculate next colour and update colour blend diff. Diff 0 = colour1, diff 1 = colour2
            colours[i] = ColorUtils.blendARGB(Color.parseColor(COLOUR_SPECTRUM[colourIndex]), Color.parseColor(COLOUR_SPECTRUM[colourIndex + 1]), colourDiff);
            colourDiff += colourIncrement;

            stepCounter++;
            if (stepCounter >= (STEPS_BETWEEN_COLOURS - 1)) {
                // we've used up blend increments between colours. Reset blend factor and jump
                // to next colours. E.g. start from purple - blue for 3 steps and then go from
                // blue to cyan for 3 steps
                stepCounter = 0;
                colourDiff = 0;
                colourIndex++;
                if (colourIndex >= COLOUR_SPECTRUM.length - 1) {
                    // we've gone through all the colours. Start again from the beginning
                    colourIndex = 0;
                }
            }
        }

        return colours;
    }

    /**
     * @param size - size of the set desired
     * @return - array of colour ints generated at random.
     */
    public static int[] getRandomColourSetOfSize(int size) {
        ArrayList<Integer> colourSet = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            if (i < COLOUR_SPECTRUM.length) {
                int colour = Color.parseColor(COLOUR_SPECTRUM[i]);
                colourSet.add(colour);
            } else {
                Random rnd = new Random();
                int colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                colourSet.add(colour);
            }
        }

        int[] primitive = new int[size];
        for (int i = 0; i < size; ++i) {
            primitive[i] = colourSet.get(i);
        }

        return primitive;
    }

    /**
     * @param steps - number of steps in between the two
     * @return - array of colour ints that smoothly blend between red and green
     */
    public static int[] generateRedToGreenGradient(int steps) {
        return generateGradient(Color.RED, Color.GREEN, steps);
    }

    /**
     * @param startColor - int of starting colour
     * @param endColor   - int of ending colour
     * @param steps      - number of steps in between the two
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
     * @param startColor - int of starting colour
     * @param endColor   - int of ending colour
     * @param steps      - number of steps in between the two
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

    /**
     * @param number  - number to operate on
     * @param sigFigs - number of decimal points to round to
     * @param signed  - true if you want to see a plus sign when positive. - sign always present
     * @return - formatted string of number using given params
     */
    public static String getNumberString(double number, int sigFigs, boolean signed) {
        DecimalFormatSymbols DFS = new DecimalFormatSymbols();
        DFS.setDecimalSeparator('.');
        DecimalFormat myFormatter;

        switch (sigFigs) {
            default:
            case 0: {
                myFormatter = new DecimalFormat("#");
                break;
            }
            case 1: {
                myFormatter = new DecimalFormat("#.#");
                break;
            }
            case 2: {
                myFormatter = new DecimalFormat("#.##");
                break;
            }
            case 3: {
                myFormatter = new DecimalFormat("#.###");
                break;
            }
        }
        myFormatter.setDecimalFormatSymbols(DFS);
        if (signed) {
            String sign = "-";
            if (number > 0) {
                sign = "+";
            } else {
                sign = "";
            }
            return sign + myFormatter.format(number);
        } else {
            return myFormatter.format(number);
        }
    }

    public static int unbox(Integer i) {
        return i != null ? i : 0;
    }
}
