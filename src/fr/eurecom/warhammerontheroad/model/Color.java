package fr.eurecom.warhammerontheroad.model;

public enum Color {
	BLUE(0),
	GREEN(1),
	VIOLET(2),
	ORANGE(3);

	private int index;

	Color(int index){
		this.index=index;
	}

	public int getIndex() {
		return index;
	}

	public String toString(){
		return Integer.toString(index);
	}

	public boolean equals(Color color){
		if(this.index==color.getIndex()) return true;
		else return false;
	}

	public static Color fromIndex(int index){
		switch(index){
		case 0:
			return BLUE;
		case 1:
			return GREEN;
		case 2:
			return VIOLET;
		case 3:
			return ORANGE;
		default:
			return null;
		}
	}
}
