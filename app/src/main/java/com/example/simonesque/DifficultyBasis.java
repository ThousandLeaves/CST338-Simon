// ======================================================================================
// ABSTRACT CLASS: DifficultyBasis
// ======================================================================================
// This class implements the majority of the methods of the game rules object.
// It contains rules for each difficulty level and is the location where Simon
// sequences are generated and accessed by MainActivity. Player inputs are sent to this
// class to check for correctness against the master sequence.
// --------------------------------------------------------------------------------------

package com.example.simonesque;

public abstract class DifficultyBasis
{
   // 99th + 1 slot is reserved for a terminator marker
   public static final int MAX_ARRAY_SIZE = 98;
   private static final int MAX_COMBO = 10;
   private static final int[] COMBO_MULTIPLIER = new int[] {1, 2, 4, 10};

   private int sequenceLength, patternSpeed, sequenceAddon, playerSequencePosition,
           currentMultiplierElement;
   private String[] sequenceArray = new String[100];
   // Used for pushing new values to arrays
   private int currentSequenceElement;

   // --------------------------------------------------------------------------------------
   // Constructors
   // --------------------------------------------------------------------------------------

   DifficultyBasis()
   {
      playerSequencePosition = 0;
      sequenceLength = 1;
      patternSpeed = 1000;
      sequenceAddon = 1;
      currentMultiplierElement = 0;
      currentSequenceElement = 0;
   }

   // Class constructor with parameter
   DifficultyBasis(int difficulty)
   {
      playerSequencePosition = 0;
      sequenceLength = 1;
      patternSpeed = 1000 / difficulty;
      sequenceAddon = difficulty;
      currentMultiplierElement = 0;
      currentSequenceElement = 0;
   }

   // --------------------------------------------------------------------------------------
   // Accessors
   // --------------------------------------------------------------------------------------

   public int getSequenceLength()
   {
      return sequenceLength;
   }

   public int getCurrentSequenceElement()
   {
      return currentSequenceElement;
   }

   public int getPatternSpeed()
   {
      return patternSpeed;
   }

   public String[] getSequenceArray()
   {
      return sequenceArray;
   }

   // --------------------------------------------------------------------------------------
   // Mutators
   // --------------------------------------------------------------------------------------

   // Sets the next open (unused) element in the array
   public boolean setSequenceArray(String newElement)
   {
      if (sequenceLength > MAX_ARRAY_SIZE)
      {
         return false;
      }
      else
      {
         sequenceArray[currentSequenceElement] = newElement;
         // Caps off end of array
         sequenceArray[currentSequenceElement + 1] = "ED";
         currentSequenceElement += 1;
         return true;
      }
   }

   public boolean isRoundOver()
   {
      if (playerSequencePosition == sequenceLength)
      {
         playerSequencePosition = 0;
         return true;
      }
      else
      {
         return false;
      }
   }

   // --------------------------------------------------------------------------------------
   // Class methods
   // --------------------------------------------------------------------------------------

   // Runs whenever a colored button is pressed in MainActivity. Checks the player's button
   // against the value in sequenceArray[n].
   public boolean checkPlayerInput(String input)
   {

      if (input == sequenceArray[playerSequencePosition])
      {
         if (COMBO_MULTIPLIER[currentMultiplierElement] < MAX_COMBO)
         {
            currentMultiplierElement += 1;
         }
         playerSequencePosition += 1;
         return true;
      }
      else
      {
         playerSequencePosition = 0;
         currentMultiplierElement = 0;
         return false;
      }
   }

   // Updates score and round number
   public boolean calculateScore(int score)
   {
      if (score > -1 && score < 99999)
      {
         int returnedScore = 500 * COMBO_MULTIPLIER[currentMultiplierElement];
         MainActivity.addScore(returnedScore);
         if (MainActivity.getCurrentRound() < MainActivity.MAX_ROUNDS)
         {
            MainActivity.incrementRound();
            // Extend the sequence by the amount appropriate for the difficulty level
            sequenceLength += sequenceAddon;
         }
         else
         {
            // Max round reached, increment to 11th round to end game
            MainActivity.incrementRound();
         }

      }
      else
      {
         // Score has either reached maximum value or is illegal
         return false;
      }
      return true;
   }

   // --------------------------------------------------------------------------------------
   // Abstract methods
   // --------------------------------------------------------------------------------------

   // This method definition is dependent on derived class behaviors
   public abstract String[] generateSequence();

}