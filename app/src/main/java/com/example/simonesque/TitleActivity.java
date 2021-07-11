// ======================================================================================
// ACTIVITY: TitleActivity
// ======================================================================================
// App begins on this screen. It is a simple button-driven menu with game options.
// --------------------------------------------------------------------------------------

package com.example.simonesque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TitleActivity extends AppCompatActivity implements View.OnClickListener {

   private static int difficulty;
   private Button btnEasy, btnMed, btnHard, btnScores, btnQuit;
   private TextView txtSorry;

   // --------------------------------------------------------------------------------------
   // Activity lifecycle and listener methods
   // --------------------------------------------------------------------------------------

   // sets up listeners when layout is created
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.title_activity);
      btnEasy = findViewById(R.id.btnEasy);
      btnEasy.setOnClickListener(this);
      btnHard = findViewById(R.id.btnHard);
      btnHard.setOnClickListener(this);
      btnMed = findViewById(R.id.btnMed);
      btnMed.setOnClickListener(this);
      btnScores = findViewById(R.id.btnScores);
      btnScores.setOnClickListener(this);
      btnQuit = findViewById(R.id.btnQuit);
      btnQuit.setOnClickListener(this);

      txtSorry = findViewById(R.id.txtSorry);

   }

   // Listens to detect clicked buttons and then determines the next execution
   @Override
   public void onClick(View v)
   {
      switch (v.getId())
      {
         case R.id.btnEasy:
            difficulty = 1;
            break;
         case R.id.btnHard:
            difficulty = 3;
            setFriendlyMessage("Sorry! Only easy game implemented.");
            eraseMessage(2000);
            break;
         case R.id.btnMed:
            difficulty = 2;
            setFriendlyMessage("Sorry! Only easy game implemented.");
            eraseMessage(2000);
            break;
         case R.id.btnScores:
            // 0 difficulty triggers the scoreboard screen
            difficulty = 0;
            break;
         case R.id.btnQuit:
            // -1 difficulty causes app to exit
            difficulty = -1;
            break;
         default:
            // No valid option, fall back to difficulty 1
            difficulty = 1;
      }

      // Determine whether to load MainActivity, scoreboard, or quit
      if (difficulty >= 1 && difficulty <= 1)
      {
         Intent intent = new Intent(this, MainActivity.class);
         intent.putExtra("Start", difficulty);
         this.startActivity(intent);
         //startActivity(new Intent(this, MainActivity.class));
      }
      else if (difficulty == 0)
      {
         startActivity(new Intent(this, ScoreboardActivity.class));
      }
      else if (difficulty == -1)
      {
         finishAffinity();
      }

   }

   // --------------------------------------------------------------------------------------
   // Class methods
   // --------------------------------------------------------------------------------------
   private void eraseMessage(int time)
   {
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  setFriendlyMessage("");
               }
            });
         }
         // code executed after n seconds
      }, time);
   }

   // --------------------------------------------------------------------------------------
   // Mutators
   // --------------------------------------------------------------------------------------

   public void setFriendlyMessage(String msg)
   {
      txtSorry.setText(msg);
   }

   // --------------------------------------------------------------------------------------
   // Accessors (Deprecated)
   // --------------------------------------------------------------------------------------

   // Accessed by other activities to determine game difficulty
   static public int getDifficulty()
   {
      return difficulty;
   }
}
