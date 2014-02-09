package fr.eurecom.warhammerontheroad.model;

public enum Race {
	HUMAN(0,"Human"),
	ELF(1,"Elf"),
	DWARF(2,"Dwarf"),	
	HOBBIT(3,"Halfling"),
	BANDIT(4,"Bandit"),
	GOBLIN(5,"Goblin"),
	GUARD(6,"Guard"),
	SKELETON(7,"Skeleton"),
	ORC(8,"Orc");

	private int index;
	private String name;

	Race(int index, String name){
		this.index=index;
		this.name=name;
	}

	public int getIndex() {
		return index;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Race race){
		if(this.index==race.getIndex()) return true;
		else return false;
	}

	public static Race fromIndex(int index){
		switch(index){
		case 0:
			return HUMAN;
		case 1:
			return ELF;
		case 2:
			return DWARF;
		case 3:
			return HOBBIT;
		case 4:
			return BANDIT;
		case 5:
			return GOBLIN;
		case 6:
			return GUARD;
		case 7:
			return SKELETON;
		case 8:
			return ORC;
		default:
			return null;
		}
	}

}
