package fr.eurecom.warhammerontheroad.model;

public class MeleeWeapon extends Weapon{
	
	public MeleeWeapon(String name, int groupe, int degats, int attribut) {
		super(name, groupe, degats, attribut);
	}

	public int getDegats()
	{
		return degats;
	}

}
