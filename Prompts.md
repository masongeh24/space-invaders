Prompt 1: I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window. 

    Observation: I used the Gemini 3 flash agent to generate this simple initial code, and it followed my instructions by creating the three files following the MVC model. I checked that there was no Swing import in the GameModel.java file. When run, the program opens a blank window with the title "Space Invaders". No logic has been implemented yet, but the structure is in place.

Prompt 2: Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.

Prompt 3: Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.

    Observation: I used Gemini 3 flash to fill in the GameModel.java file following the instructions in Prompt 2. I then compiled and ran the program to verify that the game logic was implemented correctly. The program compiles and runs, but isn't much of a game yet. The player rectangle can move left and right, but not fire a bullet. The alien rectangles move in formation, but the bullets tcome at the player too fast. Still, a good output for just the second prompt so far. 
    The AI did follow my instructions to not modify the game state in the GameView.java file.

Prompt 4: Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.

    Observation: I used Gemini 3 flash to fill in the GameController.java file following the instructions in Prompt 4. I then compiled and ran the program to verify that the game logic was implemented correctly. When the program runs, the player can move left and right and now fire bullets with the spacebar.

Prompt 5: Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.

    Observation: The agent did a pretty good job by creating the ModelTester.java file with a main method that creates a GameModel and calls its methods directly, printing PASS or FAIL for each check. I then compiled and ran the program to verify that the game logic was implemented correctly. 
    There wasn't any swing or awt imports in the model tester and the tests called the real methods and not just stubs.



Fleshing out the game now that the basic structure is in place and tested.
Basic extensions:

Prompt 6: In GameModel.java, add 4 evenly spaced shield rectangles positioned between the player and the alien formation. When hit by a bullet, the shield is partially dissolved from the hit. No Swing imports.

    Observation: I used Gemini 3 flash to fill in the GameModel.java file following the instructions in Prompt 6. I then compiled and ran the program to verify that the game logic was implemented correctly. The agent created a new class called Shield.java and updated the GameView.java file to draw the shields. I then ran the program again to make sure it compiled and ran correctly. The shields are now in place and work as expected. I think it would be better if the shields were a bit weaker. It takes too many bullets to break them down.

    Iteration: Adjust the alien and player bullets to do more vertical damage to the shields. They should be able to be pierced through if hit twice in the same vertical slice.

    Observation: The shields now take 2 hits to break down vertically.

Bugs observed: The game does not stop when the player loses all lives. (Player can still move and shoot after losing all lives, lives remaining can go negative) I believe this is a bug in the GameModel.java file that can be fixed by adding an if statement to the tick method that checks if the lives are less than or equal to 0 and if so, stops the game.

    Prompt to fix bugs: Can you fill in the game over logic in GameModel.java so that when lives <= 0, the game timer is stopped. (No swing imports) 
    Then in GameController.java, Player input should be disabled when lives are <= 0.
    In GameView.java, when lives are <= 0 just the bullets should be cleared from the screen.

    Observation: I used Gemini 3 flash to fill in the game over logic in GameModel.java, GameController.java, and GameView.java. I then compiled and ran the program to verify that the game logic was implemented correctly. The game now stops when the player loses all lives and the player input is disabled and the bullets are cleared from the screen when the game is over.
    This was accomplished by adding a lives <= 0 check to the tick() method in GameModel.java, and one to the GameController.java to stop input.

Prompt 7: In GameModel. java, increase the alien movement speed each time an alien is destroyed. Expose a method the Controller can call to get the current recommended timer interval. Do not touch the View.

    Observation: The agent added a getRecommendedTimerInterval() method to GameModel.java to calculate the recommended timer interval based on the number of aliens remaining. It then updates the timer every tick. I don't like how fast the aliens move so I changed the getRecommendedTimerInterval() method to return a higher value to slow them down.

    Iteration: I'm noticing that the bullets are tied to the timer, so when the timer is adjusted to slow down the aliens, the bullets slow down too. I'll try creating a separate timer for the bullets.

    Prompt: Within GameModel.java, decouple the alien movement timer from the game tick so that the alien speed is not tied to the game speed.

    Observation: Removed the getRecommendedTimerInterval() method from GameModel.java and added a statement to the hit code so that it updates the alien movement timer when an alien is destroyed.

Tweak: I'm noticing that the movement doesn't feel very responsive so I'll try to adjust that in Game Controller.

    Prompt: Can you tweak the player movement code in  GameController.java to include detection for  when a direction key is held down. It feels like when holding down the direction key the player moves once and there is a slight delay before continuously moving.

    Observation: I used Gemini 3 flash to implement responsive player movement in GameController.java. When the user holds down the left or right arrow key, the player now moves smoothly in that direction without the previous delay due to tracking key presses in a boolean flag and updating with the timer. However, now the player moves faster than before so I will adjust the movement speed from +&-10 to +&-5.

Prompt 8: In GameModel.java, add a reset() method that will reset the game to its initial state. This should include resetting the player's position, the alien formation, the bullets, the score, and the lives remaining. No swing imports. 
In GameController.java, add code so that pressing the R key will call the reset() method in GameModel.java. 

    Observation: I used Gemini 3 flash to implement the reset() method in GameModel.java and the R key functionality in GameController.java. I then compiled and ran the program to verify that the game logic was implemented correctly. The game now resets to its initial state when the R key is pressed.

Prompt 9: In GameModel.java (Do not use swing imports in GameModel.java), add a method that will pause the game for three seconds when a life is lost, also forwarding call(s) to GameView.java to make the player box flash white three times at one second intervals while the game is paused.

    Observation: I used Gemini 3 flash to implement the pause and flash functionality in GameModel.java and GameView.java. I then compiled and ran the program to verify that the game logic was implemented correctly. The game now pauses for three seconds when a life is lost and the player box flashes white three times at one second intervals while the game is paused.

Prompt 10: Add a text file called Score.txt in the same directory as the game that will store the high score. When the game starts, the high score should be loaded from the text file in GameModel.java. When the game ends, the high score should be updated if the current score is higher than the high score in GameModel.java. Add a method in GameView.java to display the high score in the top right corner of the screen.

    Observation: The agent correctly implemented the high score functionality in GameModel.java and GameView.java. The high score is now stored in a text file, parsed on program start up and when the game ends, and displayed in the top right corner of the screen. I then compiled and ran the program to verify that the game logic was implemented correctly. The game now displays the high score and updates it when the score is higher than the high score.

Prompt 11: In GameModel.java, implement code for a bonus ship that will fly across the top of the screen and will give 300 points when shot by the player. The bonus ship should appear randomly coming from either the left or right side of the screen every 10-30 seconds. Do not use swing imports in GameModel.java.
In GameView.java, the bonus ship should be drawn as a red rectangle, the same size as the player.

    Observation: The agent correctly implemented the bonus ship functionality in GameModel.java and GameView.java. The bonus ship now appears randomly coming from either the left or right side of the screen every 10-30 seconds and gives 300 points if shot by the player. This works by using a timer that counts down from 10-30 seconds (randomized) and when it reaches 0, it spawns the bonus ship. Then, if it is shot by the player, it is removed from the game and the timer is reset. If it reaches either edge of the screen without being hit, it also deactivates itself and resets the timer. The hit code is added onto the check collisions method in GameModel.java. 
    I then compiled and ran the program to verify that the game logic was implemented correctly. The game now has a bonus ship that appears randomly and gives 300 points when shot by the player.


Additional Enhancements: Change points distribution, add sound effects, add sprites, and multiple waves instead of a win screen.

Prompt 12: In GameModel.java, implement code that makes the bottom two rows of aliens worth 10 points each, the middle two rows worth 20 points each, and the top row worth 30 points each. Do not use swing imports in GameModel.java.

    Observation: I used Gemini 3 flash to implement the alien point system in GameModel.java. It was suprisingly simple to just add if statements to the initAliens() method to assign points based on the row and update the score by the alien's points when hit instead of a fixed 10 points. I then compiled and ran the program to verify that the game logic was implemented correctly. The bottom two rows of aliens are now worth 10 points each, the middle two rows are worth 20 points each, and the top row is worth 30 points each.

Prompt 13: Every time the aliens move, I want them to swap between two different sprites. 
GameModel.java should track a boolean animframe, then GameView.java draws different shapes for the aliens based on the animframe. Lets start with the top alien row. I have attached images of the two sprites for the top alien row. [I attached the 8-bit pixel art images.]
I then added the middle and bottom alien sprites one prompt at a time.

    Observation: I used Gemini 3 flash to implement the alien animation in GameModel.java and GameView.java. The aliens now swap between two different sprites every time they move. I then compiled and ran the program to verify that the game logic was implemented correctly. The aliens now animate between the two sprites. 
    The agent created the sprites by storing the pixel data as binary literals in the form of int arrays in GameView.java.

