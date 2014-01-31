package fr.eurecom.warhammerontheroad.model;

public class Vide extends Case {
	public Vide(int x, int y) {
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
		return "0";
	}
}
