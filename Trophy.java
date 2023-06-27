import javafx.scene.paint.Color;
// template for trophies and blockers
// basically holds information about each one in an organized way
public class Trophy {
	private int trophyX;
	private int trophyY;
	private int trophyWidth;
	private int trophyHeight;
	private int trophyDY;
	private boolean collide = false;
	
	private double modX;
	private double modY;
	private double modHeight;
	private double width = 10; // width is always 10 cuz that wont affect gameplay too much anyway
	private double modDY;
	private int modColor;
	
	
	private Color color = Color.BLACK; // default colour s black
	
	// default constructor
	public Trophy() {
		rollStats(); // just rolls dice
	}
	public Trophy( int trophyX ) { // used in earlier stages for testing
		rollStats();
		this.setX(trophyX);
	}
	// specific constructor, for te blocker mainly
	public Trophy( int trophyX, int trophyY, int trophyWidth, int trophyHeight, int trophyDY ) {
		this.setX( trophyX );
		this.setY( trophyY );
		this.setWidth( trophyWidth );
		this.setHeight( trophyHeight );
		this.setRate( trophyDY );
		
	}
 	 // rollStats
	public void rollStats() {
		// randomly generate values in an appropriate range
		modX = Math.random() * ( 750 - 350 ) + 350;
		modY = Math.random() * 200;
		modHeight = Math.random() * ( 100 - 25 ) + 25;
		width = 10;
		modDY = Math.random() * ( 2 - -2 ) - -2;
		
		// colour is a dice roll, 0 to 5
		modColor = (int)( Math.random() * 6 );
		switch( modColor ) { // each face corresponds to a colour
			case  0 : color = Color.GREEN; break;
			case  1 : color = Color.RED; break;
			case  2 : color = Color.BLUE;break;
			case  3 : color = Color.PURPLE;break;
			case  4 : color = Color.YELLOW;break;
			case  5 : color = Color.ORANGE;break;
			case  6 : color = Color.GREEN;break; // this never should roll but just in case
			// easier to implement than a try catch thats for sure
		}
		
		// and sets the values to these random stuff
		this.setX( (int)modX );
		this.setY( (int)modY );
		this.setWidth( (int)width );
		this.setHeight( (int)modHeight );
		this.setRate( (int)modDY );
		// you could put the randoms in directly
		// but i think its easier to read this way
	}
	
	// getters
	public Color getColor() {
		return color;
	}
	public int getX() {
		return trophyX;
	}
	public int getY() {
		return trophyY;
	}
	public int getWidth() {
		return trophyWidth;
	}
	public int getHeight() {
		return trophyHeight;
	}
	public int getRate() {
		return trophyDY;
	}
	public boolean isCollide() {
		return collide;
	}
	
	// setters
	public void setX( int trophyX ) {
		this.trophyX = trophyX;
	}
	public void setY( int trophyY ) {
		this.trophyY = trophyY;
	}
	public void setWidth( int trophyWidth ) {
		this.trophyWidth = trophyWidth;
	}
	public void setHeight( int trophyHeight ) {
		this.trophyHeight = trophyHeight;
	}
	public void setRate( int trophyDY ) {
		this.trophyDY = trophyDY;
	}
	public void setCollide( boolean collide ) {
		this.collide = collide;
	}
	
	// ez way to let the trophy bounce
	public void invertRate() {
		trophyDY = trophyDY * -1;
		// alternative : setRate( getRate() * -1 )
		// ez to use and intuitive name
	}
	
	
}
