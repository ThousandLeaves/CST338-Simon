// ======================================================================================
// ACTIVITY: ScoreboardActivity
// ======================================================================================
// Simple code that maintains the scoreboard.
// This activity is mainly static, but in the fully implemented version it would
// allow the player to rank onto the scoreboard as well.
// --------------------------------------------------------------------------------------

package com.example.simonesque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreboardActivity extends AppCompatActivity implements View.OnClickListener {

   private int finalScore;

   private Button btnTitle;
   private TextView txtFinalScore;

   // --------------------------------------------------------------------------------------
   // Activity lifecycle and listener methods
   // --------------------------------------------------------------------------------------

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.scoreboard_activity);

      btnTitle = findViewById(R.id.btnTitle);
      btnTitle.setOnClickListener(this);
      txtFinalScore = findViewById(R.id.txtFinalScore);
   }

   @Override
   public void onWindowFocusChanged(boolean hasFocus) {
      super.onWindowFocusChanged(hasFocus);
      if(MainActivity.getGameState() == 0)
      {
         finalScore = MainActivity.getScore();
         txtFinalScore.setText("Your score: " + finalScore);
      }
   }

   public void onClick(View v)
   {
      if(v.getId() == R.id.btnTitle)
      {
         finish();
         startActivity(new Intent(this, TitleActivity.class));
      }
   }
}