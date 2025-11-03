package com.appstronautstudios.horizontalbargraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appstronautstudios.generalutils.Boxer;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalDotGraph extends LinearLayout {

    public HorizontalDotGraph(Context context) {
        super(context);
    }

    public HorizontalDotGraph(Context context, HashMap<String, Integer> dotCounts) {
        super(context);

        // best and worst not supplied. Infer best and worst values from the hashset are the endpoints
        int bestValue = 0;
        int worstValue = 0;
        for (String key : dotCounts.keySet()) {
            int value = Boxer.unbox(dotCounts.get(key));
            if (value < worstValue) worstValue = value;
            if (value > bestValue) bestValue = value;
        }

        configureWithData(dotCounts, bestValue, worstValue);
    }

    public HorizontalDotGraph(Context context, HashMap<String, Integer> dotCounts, int dotBest, int dotWorst) {
        super(context);

        configureWithData(dotCounts, dotBest, dotWorst);
    }

    private void configureWithData(HashMap<String, Integer> dotCounts, int dotBest, int dotWorst) {
        // generate the colour set to use. Allow best to be lower than worst
        int[] colourSet = HBGUtils.generateRainbowGradient(Color.RED, Color.GREEN, Math.abs(dotBest - dotWorst) + 1);

        configureWithData(colourSet, dotCounts, dotBest, dotWorst);
    }

    private void configureWithData(int[] colours, HashMap<String, Integer> dotCounts, int dotBest, int dotWorst) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout container = (LinearLayout) inflate(getContext(), R.layout.view_horizontal_dot_breakdown, null);
        container.setLayoutParams(params);

        TextView emptyTextTV = container.findViewById(R.id.no_data_tv);

        addView(container);

        // safety
        if (dotCounts == null || dotCounts.isEmpty()) {
            // show no data view
            emptyTextTV.setVisibility(View.VISIBLE);
        } else {
            emptyTextTV.setVisibility(View.GONE);
        }

        // if best is LOWER than worst reverse colour array
        int[] finalColours;
        if (dotBest < dotWorst) {
            int[] reversed = new int[colours.length];
            for (int i = 0; i < colours.length; i++) {
                reversed[i] = colours[colours.length - 1 - i];
            }
            finalColours = reversed;
        } else {
            finalColours = colours;
        }

        int index = 1;
        for (String key : dotCounts.keySet()) {
            int dotValue = Boxer.unbox(dotCounts.get(key));
            // create legend segment with colour, title and percent
            View legendSegment = inflate(getContext(), R.layout.view_type_dot_cell, null);
            CircleImageView typeLegend = legendSegment.findViewById(R.id.type_legend);
            TextView typeTitle = legendSegment.findViewById(R.id.type_title);
            TextView typeValue = legendSegment.findViewById(R.id.type_value);
            typeLegend.setImageDrawable(new ColorDrawable(finalColours[dotValue]));

            typeTitle.setText(index + ". " + key);
            typeValue.setText(dotValue + "");

            index++;

            container.addView(legendSegment);
        }
    }
}
