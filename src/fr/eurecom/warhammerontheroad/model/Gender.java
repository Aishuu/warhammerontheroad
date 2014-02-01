package fr.eurecom.warhammerontheroad.model;

public enum Gender {
	FEMALE(0),
	MALE(1);

	private int index;

	Gender(int index){
		this.index=index;
	}

	public int getIndex() {
		return index;
	}

	public String toString(){
		return Integer.toString(index);		
	}

	public boolean equals(Gender other){
		if(this.index==other.getIndex())
			return true;
		else return false;
	}

	public static Gender fromIndex(int index){
		switch(index){
		case 0:
			return FEMALE;
		case 1:
			return MALE;
		default:
			return null;
		}
	}
}
