package com.example.gamememory;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;


public class GameActivity extends AppCompatActivity {

    GameView view;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle arguments = getIntent().getExtras();
        int size = (int) arguments.get(getString(R.string.size));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainlayout);

        view = new GameView(GameActivity.this, (size + 1) * 2, width, height);
        mainLayout.addView(view);
    }

}