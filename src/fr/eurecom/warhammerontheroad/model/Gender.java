package fr.eurecom.warhammerontheroad.model;

public enum Gender {
	FEMALE(0,"Female"),
	MALE(1,"Male");

	private int index;
	private String name;

	Gender(int index,String name){
		this.index=index;
		this.name=name;
	}

	public String getName() {
		return name;
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
