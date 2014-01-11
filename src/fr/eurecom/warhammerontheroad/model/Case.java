package fr.eurecom.warhammerontheroad.model;

public abstract class Case {
	private int x,y;
	
	protected Case() {
		this.x = -1;
		this.y = -1;
	}
	
	protected Case(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getResource() {
		return -1;
	}
	
	@Override
	public String toString() {
		return "0";
	}
}
