package fr.eurecom.warhammerontheroad.model;

public enum CombatAction {
	VISER(0),
	MOVE(1),
	STD_ATTACK(2),
	CHARGE(3),
	DEGAINER(4),
	RECHARGER(5),
	ATTAQUE_RAPIDE(6);
	
	private int index;
	
	CombatAction(int index){
		this.index=index;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString(){
		return Integer.toString(index);
	}
	
	public boolean equals(CombatAction ca){
		if(this.index==ca.getIndex()) return true;
		else return false;
	}
	
	public static CombatAction combatActionFromIndex(int index){
		switch(index){
		case 0:
			return VISER;
		case 1:
			return MOVE;
		case 2:
			return STD_ATTACK;
		case 3:
			return CHARGE;
		case 4:
			return DEGAINER;
		case 5:
			return RECHARGER;
		case 6:
			return ATTAQUE_RAPIDE;
		default:
			return null;
		}
	}

}
