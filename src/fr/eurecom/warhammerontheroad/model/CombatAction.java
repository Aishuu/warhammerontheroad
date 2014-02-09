package fr.eurecom.warhammerontheroad.model;

public enum CombatAction {
	VISER(0, "Aim"),
	MOVE(1, "Move"),
	STD_ATTACK(2, "Standard Attack"),
	CHARGE(3, "Charge"),
	DEGAINER(4, "Draw"),
	RECHARGER(5, "Reload"),
	ATTAQUE_RAPIDE(6, "Fast Attack"),
	DESENGAGER(7, "Disengage");
	
	private int index;
	private String label;
	
	CombatAction(int index, String label){
		this.index=index;
		this.label = label;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString(){
		return Integer.toString(index);
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public boolean equals(CombatAction ca){
		if(this.index==ca.getIndex()) return true;
		else return false;
	}
	
	public static CombatAction fromIndex(int index){
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
		case 7:
			return DESENGAGER;
		default:
			return null;
		}
	}

}
