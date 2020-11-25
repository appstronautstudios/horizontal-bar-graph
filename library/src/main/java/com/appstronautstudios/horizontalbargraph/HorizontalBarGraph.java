package com.appstronautstudios.horizontalbargraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

import androidx.core.graphics.ColorUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalBarGraph extends LinearLayout {

    private final String[] COLOUR_SPECTRUM = new String[]{"#80C2E9", "#79e9d6", "#71e995", "#89e96a", "#cce963", "#e9bc5b", "#e96954"};
    private final int STEPS_BETWEEN_COLOURS = 3;

    public HorizontalBarGraph(Context context) {
        super(context);
    }

    public HorizontalBarGraph(Context context, HashMap<String, Integer> barCounts) {
        super(context);

        configureWithData(generateColourSteps(barCounts.size()), barCounts);
    }

    public HorizontalBarGraph(Context context, int[] colours, HashMap<String, Integer> barCounts) {
        super(context);

        configureWithData(colours, barCounts);
    }

    private void configureWithData(int[] colours, HashMap<String, Integer> barCounts) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout container = (LinearLayout) inflate(getContext(), R.layout.view_horizontal_bar_breakdown, null);
        container.setLayoutParams(params);

        LinearLayout barContainer = container.findViewById(R.id.bar_layout);
        LinearLayout legendContainer = container.findViewById(R.id.legend_container);
        TextView emptyTextTV = container.findViewById(R.id.no_data_tv);

        addView(container);

        // clear first
        barContainer.removeAllViews();
        legendContainer.removeAllViews();

        // get total count for generating percentages
        int totalCount = 0;
        for (Integer count : barCounts.values()) {
            totalCount += unbox(count);
        }

        // config
        if (totalCount > 0) {
            int currentBar = 0;
            for (String barKey : barCounts.keySet()) {
                int count = unbox(barCounts.get(barKey));
                float percent = ((float) count / totalCount);

                // scaled bar segment. Only add if has value
                if (count > 0) {
                    View barSegment = inflate(getContext(), R.layout.view_horizontal_bar_segment, null);
                    barSegment.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, percent));
                    barSegment.setBackground(new ColorDrawable(colours[currentBar]));
                    barContainer.addView(barSegment);
                }

                // spacer. Only add if has value
                if (count > 0) {
                    Space space = new Space(getContext());
                    space.setLayoutParams(new TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT));
                    barContainer.addView(space);
                }

                // create legend segment with colour, title and percent
                View legendSegment = inflate(getContext(), R.layout.view_horizontal_bar_legend, null);
                CircleImageView typeLegend = legendSegment.findViewById(R.id.type_legend);
                TextView typeTitle = legendSegment.findViewById(R.id.type_title);
                TextView typePercent = legendSegment.findViewById(R.id.type_percent);
                typeLegend.setImageDrawable(new ColorDrawable(colours[currentBar]));
                typeTitle.setText(barKey);
                typePercent.setText(getNumberString(percent * 100, 1, false) + "%");
                legendContainer.addView(legendSegment);

                // make sure we don't index out of bounds. Looping preferable to crash
                currentBar++;
                if (currentBar >= colours.length) {
                    currentBar = 0;
                }
            }
            emptyTextTV.setVisibility(View.GONE);
            barContainer.setVisibility(View.VISIBLE);
        } else {
            //show no data view
            emptyTextTV.setVisibility(View.VISIBLE);
            barContainer.setVisibility(View.GONE);
        }
    }

    private int[] generateColourSteps(int size) {
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
     * @param number  - number to operate on
     * @param sigFigs - number of decimal points to round to
     * @param signed  - true if you want to see a plus sign when positive. - sign always present
     * @return - formatted string of number using given params
     */
    private static String getNumberString(double number, int sigFigs, boolean signed) {
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

    private static int unbox(Integer i) {
        return i != null ? i : 0;
    }
}
