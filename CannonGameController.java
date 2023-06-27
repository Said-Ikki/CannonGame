import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CannonGameController {

	// fxml stuffs to interact with system
    @FXML
    private Canvas canvas;
    @FXML
    private Pane paneContainer;
    @FXML
    private Label timeLabel;
    
    // used to draw stuff, define it once in initialize 
    //and all good for other functions
    private GraphicsContext magicBrush;
    
    // -- non-class-- cuz its only a handful of variables each 
    
    // cannon circle info
    private int radius = 70;
    private int cannonPosX = 0 - radius/2 ;
    private int cannonPosY; // needs canvas to define it properly
    // needs to be center of canvas
    // canvas isnt real yet so cant intialize it here
    
    // rectangular piece attached to cannon circle
    private int cannonPieceWidth = 70;
    private int cannonPieceHeight = 30;
    private int cannonPieceX = 0;
    private int cannonPieceY; // needs canvas to define properly
    
    // -- class -- cuz we use the similar thing multiple times
    
    // list of shootable trophies
    private ArrayList<Trophy> trophyList = new ArrayList<Trophy>();
    // bad guy blocker trophy with rigid definitions
    private Trophy blocker = new Trophy(250, 100, 10, 100, 1);
    
    // time info
    private long gameStart = System.currentTimeMillis(); // when start
    private long timeRemaining = gameStart + 10000; // when game ends
    private float usableTimeRemaining; // used later to display float 
    //instead of milliseconds, it displays seconds with decimals
    
    // win condition stuff
    private int level = 1; // winning = progressing a level
    private boolean loseCondition = false; // losing mean this turns on and everything resets
    //long timeRemaining = 30;
    
    // sound file loaders
    private Media blockerFile = new Media( getClass().getResource( "blocker_hit.wav" ).toExternalForm() );
    private Media cannonFile = new Media( getClass().getResource( "cannon_fire.wav" ).toExternalForm() );
    private Media targetFile = new Media( getClass().getResource( "target_hit.wav" ).toExternalForm() );
    // play sound file later stuffs
    private MediaPlayer blockerPlayer = new MediaPlayer(blockerFile);
    private MediaPlayer cannonPlayer = new MediaPlayer(cannonFile);
    private MediaPlayer targetPlayer = new MediaPlayer(targetFile);
    
    // for adjusting fire rate
    private boolean isFired = false;
    
    @FXML // when aiming while moving mouse on canvas
    public void adjustPosition(MouseEvent event) {
    	
    	if(loseCondition == true) { // if lost
    		return; // dont do anything to the screen
    	}
    	
    	// clear screen and get mouse positions
    	magicBrush.setFill(Color.WHITE);
    	magicBrush.fillRect( 0, 0, canvas.getWidth() , canvas.getWidth() );
    	double mouseX = event.getX();
    	double mouseY = event.getY() - (cannonPieceY - 5);
    	// y needs to be adjusted cuz it is relative to top right corner, not cannon
    	// the -5 is completely aesthetic
    	
    	// find amount to rotate based on mouse pos and trig
    	double degrees = Math.toDegrees( Math.atan( mouseY/mouseX ) );
    	// move to point of rotation and rotate
    	magicBrush.translate( 0, cannonPieceY + 10);
    	magicBrush.rotate(degrees);
    	magicBrush.translate( 0, -cannonPieceY - 10);
    	// draw rotated stuff
    	magicBrush.setFill(Color.RED);
    	magicBrush.fillRect(cannonPieceX, cannonPieceY + cannonPieceHeight/2, 2000, 2);
    	magicBrush.setFill(Color.BLACK);
    	magicBrush.fillRect(cannonPieceX, cannonPieceY, cannonPieceWidth, cannonPieceHeight);
    	// reset rotation and colours
    	magicBrush.setFill(Color.BLACK);
    	magicBrush.translate( 0, cannonPieceY + 10);
    	magicBrush.rotate(-degrees);
    	magicBrush.translate( 0, -cannonPieceY - 10);
    	// redraw circle 
    	magicBrush.fillOval(cannonPosX, cannonPosY, radius, radius);
    } // end of adjustPosition

    @FXML // when firing by right clicking
    public void fireInTheHole(MouseEvent event) {
    	if( isFired == true ) { // if cannon ball already fired
    		return; // dont go
    	} 
    	if(loseCondition == true) { // if lost
    		loseCondition = false; // reset everything
    		loadTrophies();
    		gameStart = System.currentTimeMillis();
    	    timeRemaining = gameStart + 30000;
    	    level = 1;
    	    loseCondition = false;
    	    adjustPosition(event); // refreshes screen 
    		return;
    	}
    	//isFired = true; // turns on the lowered fire rate
    	cannonPlayer.play(); // play cannonball fire sound
    	cannonPlayer.seek(cannonPlayer.getStartTime());
    	
    	// make the cannonball move around
    	AnimationTimer moveBall = new AnimationTimer() {
    		// variables denoting size, rate, position etc
    		double x = 0;
    		double y = cannonPieceY + 10; 
    		double dy = 5;
    		double dx = 5;
    		double ballRad = 10;
    		// determine angular angularity to move at
    		double mouseX = event.getX();
    		double mouseY = event.getY() - (cannonPieceY - 5);
    		
    		public void handle(long now) {
    			// find angle and clear previous cannonball
    			double degrees = ( Math.atan( mouseY/mouseX ) );
    			magicBrush.setFill(Color.WHITE);
    			magicBrush.fillRect(x-5, y-5, 15, 15); // little extra padding to be sure
    			
    			// move the ball a certain amount in each direction
    			x = x + dx*Math.cos(degrees);
    			y = y + dy*Math.sin(degrees);
    
    			magicBrush.setFill(Color.BLACK); // draw black ball
    			magicBrush.fillOval(x, y, ballRad, ballRad);
    			
    			// logicking
    			// if it collides with canvas edge
    			if( true == collideWithCanvasEdge( y, ballRad ) ) {
    				dy = dy * -1; // bounce off it
    			}
    			// if hits the blocker trophy
    			if( collideWithTrophy(x, y, ballRad, ballRad, blocker) == true ) {
    				blockerPlayer.play(); // play sound
    		    	blockerPlayer.seek(blockerPlayer.getStartTime());
    		    	isFired = false; // allow another cannonball
					timeRemaining -= 3000; // take off some time
    		    	magicBrush.setFill(Color.WHITE);
        			magicBrush.fillRect(x-5, y-5, 15, 15); // little extra padding to be sure
        			
    		    	this.stop(); // terminate animation for this ball
    			}
    			// if goes past the screen width
    			if(x > canvas.getWidth()) {
    				isFired = false; // allow another to fire
    		    	
    				this.stop(); // free resources
    		    	// and make sure this isnt called repeatedly 
    		    	// that accidently allows for full auto
    			}
    			// check collide with every non blocker trophy
    			for( Trophy t : trophyList ) {
    				// if it does
    				if( collideWithTrophy(x, y, ballRad, ballRad, t) == true 
    						&& t.isCollide() == false) {
    					magicBrush.setFill(Color.WHITE); // clear the trophy
    	    			magicBrush.fillRect( t.getX(), 
    	    								 t.getY(),
    	    								 t.getWidth(),
    	    								 t.getHeight());
    					t.setCollide(true); // say the trophy collided so they handle appropriately
    					targetPlayer.play(); // play sound
        		    	targetPlayer.seek(targetPlayer.getStartTime()); // rewind to start of sound file

    					timeRemaining += 3000; // bonus time
        		    	isFired = false; // allow another shot
        		    	magicBrush.setFill(Color.WHITE);
            			magicBrush.fillRect(x-5, y-5, 15, 15); // little extra padding to be sure
            			
        		    	this.stop(); // stop animation
    					
    				}
    			}
    			
    			
    		}
    	};
    	moveBall.start(); // play this animation 
    } // end of fireInTheHole

    // helper function loads up trophies
    // private cuz i only want the class to load trophies 
    private void loadTrophies() {
    	trophyList.clear(); // empties list
    	for(int i = 0; i < 8; i++) { // adds a bunch of trophies
    		trophyList.add( new Trophy() );
    	}
    } // end of loadTrophies
    
    // what should be done when the game starts?
    public void initialize() {
    	
    	// test text 
    	timeLabel.setTextFill(Color.BLACK);
    	timeLabel.setText("Hello");
    	// set the cannon positions
    	cannonPosY = (int)canvas.getHeight()/2 - radius/4;
    	cannonPieceY = cannonPosY + 20; 
    	// 20 is an arbitrary correction number
    	
    	// define the thing that allows drawing
    	magicBrush = canvas.getGraphicsContext2D();
    	// ready up all trophies
    	loadTrophies();
    	// ready up how they move
    	// this is also where win/lose conditions are tested
    	// cuz they are directly related to both 
    	AnimationTimer moveTrophies = new AnimationTimer() {
    		
    		public void handle(long now) {
    			//timeRemaining;
    			// calulate how much time is left
    			// called usableTimeRemaining cuz its more intuitive to work with
    			usableTimeRemaining = ( (float)(timeRemaining - System.currentTimeMillis())/1000) ;
    			String time = String.format("Time : %.3f %nLevel : %d", usableTimeRemaining, level );
    			timeLabel.setText(time); // print the remaining time
    			
    			int counter = 0; // used to check win condition
    			// resets every call
    			
    			for( Trophy t : trophyList ) { // for every trophy
    				if(t.isCollide() == false ) { // if they didnt get killed
    					magicBrush.setFill(Color.WHITE); // clear previous rectangle
    	    			magicBrush.fillRect( t.getX(), 
    	    								 t.getY(),
    	    								 t.getWidth(),
    	    								 t.getHeight());
    	    			
    	    			magicBrush.setFill( t.getColor() ); // get their color
    	    			t.setY( t.getY() + t.getRate() ); // change position  
    	    			magicBrush.fillRect( t.getX(), // redraw
    										 t.getY(),
    										 t.getWidth(),
    										 t.getHeight());
    	    			// if hits edge
    	    			if( true == collideWithCanvasEdge( t.getY(), t.getHeight() ) ) {
    	    				t.invertRate(); // bounce
    	    			}
    				} // check if it has collided
    				if( t.isCollide() == true ) {
    					counter++; // increase counter if it did
    				}
    			} // end of per trophy loop
    			// win/lose condition testing
    			
    			// if counter = list size, all trophies got hit
    			if( counter == trophyList.size() ) { // win condition
    				for( Trophy t : trophyList ) { // reset trophies
    					t.setCollide(false); // make them alive again
    					t.rollStats(); // reroll their random values
    				}
    				trophyList.add( new Trophy() ); // add another trophy
    				timeRemaining = System.currentTimeMillis() + 30000; // reset time
    				level++; // and move on to the next level
    			}
    			// if time is zero or lower
    			if(usableTimeRemaining <= 0) { // lose condition
    				loseCondition = true; // say globaly it lost
    				magicBrush.setFill(Color.BLACK); // clear screen
    				magicBrush.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());
    				magicBrush.setFill(Color.WHITE); // print msg to screen
    				magicBrush.setTextAlign(TextAlignment.LEFT);
    				//magicBrush.setTextBaseline(200);
    				magicBrush.setFont( new Font( "Arial", 30 ) );
    				String lose = String.format("Game Over %nClick to Restart");
    				magicBrush.fillText(lose, 0, canvas.getHeight() / 2, 10000);
    			}
    		}
    		
    	}; // end of trophy list animation timer
    	
    	// we gotta move the blocker around tho
    	AnimationTimer moveBlocker = new AnimationTimer() {
    		public void handle(long now) { 
    			// logic for collision handled at cannonball
    			// so it just needs to move around here
    			
    			// clear rectangle
    			magicBrush.setFill(Color.WHITE);
    			magicBrush.fillRect( blocker.getX(), 
    								 blocker.getY(),
    								 blocker.getWidth(),
    								 blocker.getHeight());
    			
    			magicBrush.setFill(Color.BLACK); // update positions
    			blocker.setY( blocker.getY() + blocker.getRate() );
    			magicBrush.fillRect( blocker.getX(),  // redraw
									 blocker.getY(),
									 blocker.getWidth(),
									 blocker.getHeight());
    			magicBrush.setFill(Color.BLACK);
    	    	magicBrush.fillOval(cannonPosX, cannonPosY, radius, radius);
    	    	
    			
    			// if edge hit
    			if( true == collideWithCanvasEdge( blocker.getY(), blocker.getHeight() ) ) {
    				blocker.invertRate(); // bounce
    			}
    			
    		}
    	};
    	// draw everything for the first time
    	magicBrush.setFill(Color.BLACK);
    	magicBrush.fillRect(cannonPieceX, cannonPieceY, cannonPieceWidth, cannonPieceHeight);
    	magicBrush.fillOval(cannonPosX, cannonPosY, radius, radius);
    	moveBlocker.start(); // and start animations
    	moveTrophies.start();
    } // end of initialize
    
    // collision logic, helper functions
    // private cuz nobody outside the class needs to know
    // how collision works
    
    // if it hits bounds of canvas
    private boolean collideWithCanvasEdge(double y, double height) {
    	// if its bottom piece hits the bottom
    	final boolean BOTTOM = ( y + height + 1 >= canvas.getHeight() );
    	// or top piece hits the top
    	final boolean TOP = ( y-1 <= 0 );	
    	return (BOTTOM || TOP);
    } // end of collideWithCanvasEdge
    
    // general rectangular collision
    private boolean collideWithTrophy(double x, double y, double height, double width, Trophy t) {
    	// check it is in bounds of the trophy
    	final boolean BOTTOM = y <= (t.getHeight() + t.getY() );
    	final boolean TOP = y >= t.getY();
    	final boolean RIGHT = x <= ( t.getX() + t.getWidth() );
    	final boolean LEFT = x >= t.getX();
    	// to be in bounds, all must be correct
    	return ( BOTTOM && TOP && LEFT && RIGHT );
    } // end of collideWithTrophy
}

