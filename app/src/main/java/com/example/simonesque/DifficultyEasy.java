// ======================================================================================
// CLASS: DifficultyEasy
// ======================================================================================
// Subclass of DifficultyBasis that contains the implementation of generateSequence(),
// a method whose behaviors are defined only in subclasses.
// --------------------------------------------------------------------------------------

package com.example.simonesque;
import java.util.Random;

// Class is almost identical to parent, but implements generateSequence() in a unique fashion
public class DifficultyEasy extends DifficultyBasis {

   private String[] possibleSquares = new String[3];

   // --------------------------------------------------------------------------------------
   // Constructors
   // --------------------------------------------------------------------------------------

   DifficultyEasy()
   {
      super();
   }
   DifficultyEasy(int difficulty)
   {
      super(difficulty);
   }

   // --------------------------------------------------------------------------------------
   // Class methods
   // --------------------------------------------------------------------------------------

   // In EASY difficulty, this sequence only generates three keys and has frequent double presses
   // of the same square
   @Override
   public String[] generateSequence()
   {
      // Chooses a pattern of 3 possible squares if sequence has not yet built
      if (getCurrentSequenceElement() == 0)
      {
         pickSquares();
      }

      while (getSequenceLength() > getCurrentSequenceElement())
      {
         // setSequenceArray will ultimately break this loop
         boolean test = setSequenceArray(generateKey());
         if (test == false)
         {
            break;
         }
      }

      String[] temp = getSequenceArray().clone();
      return temp;
   }

   // --------------------------------------------------------------------------------------
   // Private helper methods
   // --------------------------------------------------------------------------------------

   // Helper method only used in easy. Randomly picks a pattern of 3 squares.
   private void pickSquares()
   {
      int pattern = new Random().nextInt(3) + 1;

      if (pattern == 1)
      {
         possibleSquares[0] = "UP";
         possibleSquares[1] = "RT";
         possibleSquares[2] = "LT";
      }
      if (pattern == 2)
      {
         possibleSquares[0] = "DN";
         possibleSquares[1] = "RT";
         possibleSquares[2] = "LT";
      }
      if (pattern == 3)
      {
         possibleSquares[0] = "DN";
         possibleSquares[1] = "LT";
         possibleSquares[2] = "UP";
      }
   }

   // Helper method picks a random arrow to place next in the sequence
   private String generateKey()
   {
      int key = new Random().nextInt(3) + 1;

      switch (key)
      {
         case 1:
            return possibleSquares[0];
         case 2:
            return possibleSquares[1];
         case 3:
            return possibleSquares[2];
         default:
            return "UP";
      }
   }
}