// ======================================================================================
// ACTIVITY: MainActivity
// ======================================================================================
// Controls the simon game and contains the bulk of game logic.
// All actions taken by the game are generated here, scheduled sequentially on a timer
// thread.
// --------------------------------------------------------------------------------------

package com.example.simonesque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.MotionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

   // Max rounds set to 10 as default, updated when activity comes to foreground
   public static final int MAX_ROUNDS = 10;
   public static final int MAX_SCORE = 99999;
   private static int playerScore, roundNumber, gameContinues, passedDifficulty;

   // Takes a copy of the simon sequence from DifficultyBasis classes
   String[] currentSequence;
   private String playerInput = "";
   private boolean allowInput = false;
   private static boolean gameOver = false;

   private TextView txtRoundMsg, txtScore, txtContinues;
   private Button btnUp, btnLft, btnRt, btnDwn;
   private DifficultyBasis GameObject;

   MediaPlayer beep;

   // --------------------------------------------------------------------------------------
   // Activity lifecycle and listener methods
   // --------------------------------------------------------------------------------------

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main_activity);

      roundNumber = 1;
      gameContinues = 3;
      playerScore = 0;

      // UI Objects
      txtRoundMsg = findViewById(R.id.txtRoundMsg);
      txtScore = findViewById(R.id.txtScore);
      txtScore.setText("Score: " + playerScore);
      txtContinues = findViewById(R.id.txtContinues);
      txtContinues.setText("Continues: " + gameContinues);

      btnUp = findViewById(R.id.btnUp);
      btnUp.setOnTouchListener(this);
      btnLft = findViewById(R.id.btnLft);
      btnLft.setOnTouchListener(this);
      btnRt = findViewById(R.id.btnRt);
      btnRt.setOnTouchListener(this);
      btnDwn = findViewById(R.id.btnDwn);
      btnDwn.setOnTouchListener(this);

      //beep = MediaPlayer.create(MainActivity.this,R.raw.simonbeep);


   }

   // On touch used so button colors can be reset to their default state after mouse clicks
   @Override
   public boolean onTouch(View v, MotionEvent e)
   {
      if (allowInput)
      {
         if (e.getAction() == MotionEvent.ACTION_DOWN)
         {
            switch (v.getId())
            {
               case R.id.btnUp:
                  btnUp.setBackgroundColor(0xFF327434);
                  playerInput = "UP";
                  break;
               case R.id.btnDwn:
                  btnDwn.setBackgroundColor(0xFF615C16);
                  playerInput = "DN";
                  break;
               case R.id.btnLft:
                  btnLft.setBackgroundColor(0xFF114F81);
                  playerInput = "LT";
                  break;
               case R.id.btnRt:
                  btnRt.setBackgroundColor(0xFF861139);
                  playerInput = "RT";
                  break;
               default:
                  // Shouldn't be possible to reach here
            }
            isInputsCorrect();

         }
         if (e.getAction() == MotionEvent.ACTION_UP)
         {
            clearColors();
         }
      }
      return false;
   }

   @Override
   public void onResume()
   {
      super.onResume();

      Bundle extras = getIntent().getExtras();

      // Create the correct difficulty object
      if (extras != null)
      {
         passedDifficulty = extras.getInt("Start");
         switch (passedDifficulty)
         {
            case 1:
               GameObject = new DifficultyEasy();
               break;
            case 2:
               // Difficulty MED object
               break;
            case 3:
               // Difficulty HARD object
               break;
            default:
               //
         }
         runGame("newRound");
      }
   }

   // --------------------------------------------------------------------------------------
   // / ! \ Game process: runGame
   // --------------------------------------------------------------------------------------

   // Creates an action queue on a timer that runs events in the game.
   public void runGame(String messageSpecial)
   {
      allowInput = false;
      // initialize the wait time for stack
      int tileWaitLeastTime = 0;

      // Check if continuing is possible before running round
      if(isContinuePossible())
      {
         currentSequence = getSequence();

         clearTileHighlights( 500);
         tileWaitLeastTime += 500;

         if (messageSpecial == "newRound")
         {
            writeTipText("Get ready...", 1000);
            tileWaitLeastTime += 500;

         }
         else if (messageSpecial == "wrongKey")
         {
            writeTipText("Try again!", 1000);
            tileWaitLeastTime += 500;
         }

         writeTipText("Round " + roundNumber, tileWaitLeastTime + 2000);
         tileWaitLeastTime += 2000;

         // Schedule at least 4 seconds for the thread
         tileWaitLeastTime += 1000;

         // Loops over the generated simon sequence and flashes tiles
         for(int i = 0; i <= GameObject.getSequenceLength() - 1; i++)
         {
            flashTileSequence(GameObject, tileWaitLeastTime, i);
            tileWaitLeastTime += GameObject.getPatternSpeed() - 400;
            stopAudio(tileWaitLeastTime + 10);
            tileWaitLeastTime += 10;
            clearTileHighlights(tileWaitLeastTime);
            if (i < GameObject.getSequenceLength())
               tileWaitLeastTime += 400;
            else
               tileWaitLeastTime += 50;
         }

         allowButtonInteractions(tileWaitLeastTime);
      }
      else
      {
         // Game cannot continue. Begin ending sequences.
         writeTipText("Game over!", 100);
         tileWaitLeastTime += 100;
         clearTileHighlights(2000 + tileWaitLeastTime);
         tileWaitLeastTime += 2000;
         System.out.println("Accessing scoreboard activity.");
         goToScoreboard(tileWaitLeastTime);
      }
   }

   // --------------------------------------------------------------------------------------
   // Timer thread events
   // --------------------------------------------------------------------------------------
   // These methods are designed to be scheduled with the timer thread. They cannot be
   // used outside of this context.
   // --------------------------------------------------------------------------------------

   // Runs on the timer thread to briefly clear the tile colors before next selection takes place
   public void allowButtonInteractions(int lastTimeScheduled)
   {
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  allowInput = true;
               }
            });
         }
         // code executed after n seconds
      }, lastTimeScheduled);
   }

   public void goToScoreboard(int lastTimeScheduled)
   {
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  scoreboard();
               }
            });
         }
         // code executed after n seconds
      }, lastTimeScheduled);
   }

   public void stopAudio(int lastTimeScheduled)
   {
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  beep.pause();
                  beep.reset();
               }
            });
         }
         // code executed after n seconds
      }, lastTimeScheduled);
   }

   public void clearTileHighlights(int lastTimeScheduled)
   {
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  System.out.println("trying to clear colors.");
                  clearColors();
               }
            });
         }
         // code executed after n seconds
      }, lastTimeScheduled);
   }

   // Runs the pattern sequence that the player must watch and duplicate
   public void flashTileSequence(DifficultyBasis obj, int lastTimeScheduled, final int sequencePosition)
   {
      int speed = obj.getPatternSpeed();

      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  clearColors();

                  // Prepare and run beep sounds
                  beep = MediaPlayer.create(MainActivity.this,R.raw.simonbeep);
                  try {
                     beep.prepare();
                  }
                  catch (Exception e) {
                     System.out.println("ok");
                  }
                  beep.start();

                  System.out.println("IT IS NOW " + currentSequence[sequencePosition]);
                  if (currentSequence[sequencePosition] == "UP")
                  {
                     btnUp.setBackgroundColor(0xFF327434);
                  }
                  else if (currentSequence[sequencePosition] == "RT")
                  {
                     btnRt.setBackgroundColor(0xFF861139);
                  }
                  else if (currentSequence[sequencePosition] == "DN")
                  {
                     btnDwn.setBackgroundColor(0xFF615C16);
                  }
                  else if (currentSequence[sequencePosition] == "LT")
                  {
                     btnLft.setBackgroundColor(0xFF114F81);
                  }
                  else if (currentSequence[sequencePosition] == "ED")
                  {
                     clearColors();
                  }
               }
            });
         }
         // code executed after n seconds
      }, lastTimeScheduled);
   }

   public void writeTipText(String text, int time)
   {
      final String settingText = text;

      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  txtRoundMsg.setText(settingText);
               }
            });
         }
         // code executed after n seconds
      }, time);
   }

   // --------------------------------------------------------------------------------------
   // Private helper methods
   // --------------------------------------------------------------------------------------

   // Determines if the player's input correctly follows the pattern
   // If correct, check if round is over, then add score and start new round
   // If wrong, remove a continue and repeat current pattern
   private void isInputsCorrect()
   {
      // Determine if the player's input is good
      if (GameObject.checkPlayerInput(playerInput) == true)
      {
         // Check if round is over before running game
         if (GameObject.isRoundOver() == true)
         {
            GameObject.calculateScore(playerScore);
            txtScore.setText("Score: " + playerScore);
            runGame("");
         }

      }
      else
      {
         if (gameContinues != 0)
            removeContinue();
         txtContinues.setText("Continues: " + gameContinues);
         runGame("wrongKey");
      }
   }

   private void removeContinue()
   {
      gameContinues -= 1;
   }

   // Helper function that sets colors of tiles back to normal
   private void clearColors()
   {
      btnUp.setBackgroundColor(0xFF4CAF50);
      btnRt.setBackgroundColor(0xFFE91E63);
      btnDwn.setBackgroundColor(0xFFFFEB3B);
      btnLft.setBackgroundColor(0xFF2196F3);
   }

   private void scoreboard()
   {
      finish();
      startActivity(new Intent(this, ScoreboardActivity.class));
   }

   // Calls the current game difficulty object to generate a sequence and store a copy of
   // it locally.
   private String[] getSequence()
   {
      return GameObject.generateSequence();
   }


   // --------------------------------------------------------------------------------------
   // Class methods
   // --------------------------------------------------------------------------------------
   // These methods perform repetitive functions, but don't take or return values

   public static void incrementRound()
   {
      roundNumber += 1;
   }

   public boolean isContinuePossible()
   {
      if (gameContinues != 0 && roundNumber <= MAX_ROUNDS)
      {
         return true;
      }
      else
      {
         gameOver = true;
         return false;
      }
   }

   // --------------------------------------------------------------------------------------
   // Class mutators
   // --------------------------------------------------------------------------------------

   // Used by methods outside of MainActivity
   public static boolean addScore(int newScore)
   {
      if (playerScore + newScore <= MAX_SCORE)
      {
         playerScore += newScore;
         return true;
      }
      else
      {
         // Cannot add score. Over maximum error.
         return false;
      }

   }

   // --------------------------------------------------------------------------------------
   // Class accessors
   // --------------------------------------------------------------------------------------

   // Returns current round, mainly used for other classes
   public static int getCurrentRound()
   {
      return roundNumber;
   }

   public static int getScore()
   {
      return playerScore;
   }

   // State 0 = game over, 1 = running/not started
   public static int getGameState()
   {
      if (gameOver)
      {
         return 0;
      }
      else
      {
         return 1;
      }
   }

}