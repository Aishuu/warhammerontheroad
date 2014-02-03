package fr.eurecom.warhammerontheroad.model;

public enum Place {
	ALTDORF(0,"Altdorf"),
	MARIENBURG(1,"Marienburg"),
	NULN(2,"Nuln");

	private int index;
	private String name;
	public static int numberOfPlaces=3;	

	Place(int index, String name){
		this.index=index;
		this.name=name;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public static int getNumberOfPlaces() {
		return numberOfPlaces;
	}

	public String toString(){
		return Integer.toString(index);
	}

	public boolean equals(Place place){
		if(this.index==place.getIndex())
			return true;
		else return false;
	}

	public static Place fromIndex(int index){
		switch(index){
		case 0:
			return ALTDORF;
		case 1:
			return MARIENBURG;
		case 2:
			return NULN;
		default:
			return null;
		}
	}
}
