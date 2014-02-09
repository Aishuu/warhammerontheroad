package fr.eurecom.warhammerontheroad.model;

public enum Color {
	BLUE(0,"#0000CD"),
	GREEN(1,"#006400"),
	VIOLET(2,"#8B008B"),
	ORANGE(3,"#A0522D");

	private int index;
	private String value;

	Color(int index, String value){
		this.index=index;
		this.value=value;
	}

	public int getIndex() {
		return index;
	}

	public String getValue() {
		return value;
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
