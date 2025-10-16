package com.appstronautstudios.horizontalbargraph;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalBarGraph extends LinearLayout {

    public HorizontalBarGraph(Context context) {
        super(context);
    }

    public HorizontalBarGraph(Context context, HashMap<String, Integer> barCounts) {
        super(context);

        configureWithData(HBGUtils.generateDefaultColourSet(barCounts.size()), barCounts);
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
            totalCount += HBGUtils.unbox(count);
        }

        Set<Map.Entry<String, Integer>> set = barCounts.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int result = (o2.getValue()).compareTo(o1.getValue());
                if (result != 0) {
                    return result;
                } else {
                    return o1.getKey().compareTo(o2.getKey());
                }
            }
        });

        // config
        if (totalCount > 0) {
            int currentBar = 0;
            for (Map.Entry<String, Integer> barKey : list) {
                int count = barKey.getValue();
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
                typeTitle.setText(barKey.getKey());
                typePercent.setText(HBGUtils.getNumberString(percent * 100, 1, false) + "%");
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
}
