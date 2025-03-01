
public class Coordinate {

	private int coordX;
	private int coordY;
	
	public Coordinate(int x, int y) 
	{
		this.setCoordX(x);
		this.setCoordY(y);
	}

	public int getCoordX() {
		return coordX;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

}
