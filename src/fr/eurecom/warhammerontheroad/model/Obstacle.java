package fr.eurecom.warhammerontheroad.model;

public class Obstacle extends Case {
	public Obstacle(int x, int y) {
		super(x, y);
	}

	@Override
	public String describeAsString() {
		return representInString();
	}

	@Override
	public void constructFromString(WotrService service, String s) {		
	}

	@Override
	public String representInString() {
		return "-1";
	}
}
