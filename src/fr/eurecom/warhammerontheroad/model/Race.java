package fr.eurecom.warhammerontheroad.model;

public enum Race {
	RACE_HUMAN(0,"Human"),
	RACE_ELF(1,"Elf"),
	RACE_DWARF(2,"Dwarf"),	
	RACE_HOBBIT(3,"Hobbit"),
	RACE_BANDIT(4,"Bandit"),
	RACE_GOBLIN(5,"Goblin"),
	RACE_GUARD(6,"Guard"),
	RACE_SKELETON(7,"Skeleton"),
	RACE_ORC(8,"Orc");

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

	public static Race raceFromIndex(int index){
		switch(index){
		case 0:
			return RACE_HUMAN;
		case 1:
			return RACE_ELF;
		case 2:
			return RACE_DWARF;
		case 3:
			return RACE_HOBBIT;
		case 4:
			return RACE_BANDIT;
		case 5:
			return RACE_GOBLIN;
		case 6:
			return RACE_GUARD;
		case 7:
			return RACE_SKELETON;
		case 8:
			return RACE_ORC;
		default:
			return null;
		}
	}

}
