package com.appstronautstudios.horizontalbargraphdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.appstronautstudios.horizontalbargraph.HorizontalBarGraph;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configure();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            configure();
        });
    }

    private void configure() {
        LinearLayout defaultLL = findViewById(R.id.default_colours);
        defaultLL.removeAllViews();
        // set up fake data
        LinkedHashMap<String, Integer> fakeData1 = new LinkedHashMap<>();
        int numColours = new Random().nextInt(10) + 10;
        for (int j = 0; j < numColours; j++) {
            fakeData1.put(generateRandomWord(), new Random().nextInt(10));
        }
        // add graph to parent that uses default lib colours
        defaultLL.addView(new HorizontalBarGraph(MainActivity.this, fakeData1));

        LinearLayout customLL = findViewById(R.id.custom_colours);
        customLL.removeAllViews();
        // set up fake data
        LinkedHashMap<String, Integer> fakeData2 = new LinkedHashMap<>();
        for (int j = 0; j < 10; j++) {
            fakeData2.put(generateRandomWord(), new Random().nextInt(10));
        }
        // add graph to parent that uses custom colours
        int[] customColours = new int[]{Color.parseColor("#f1ed92"),
                Color.parseColor("#914b55"),
                Color.parseColor("#ef95af"),
                Color.parseColor("#22fc0d"),
                Color.parseColor("#0e52be"),
                Color.parseColor("#9aa54e"),
                Color.parseColor("#107a05"),
                Color.parseColor("#a57fd1"),
                Color.parseColor("#e4ddaa"),
                Color.parseColor("#172717"),
                Color.parseColor("#4137f9")};
        customLL.addView(new HorizontalBarGraph(MainActivity.this, customColours, fakeData2));
    }

    private static String generateRandomWord() {
        String randomString;
        Random random = new Random();
        char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
        for (int j = 0; j < word.length; j++) {
            word[j] = (char) ('a' + random.nextInt(26));
        }
        randomString = new String(word);
        return randomString;
    }
}
