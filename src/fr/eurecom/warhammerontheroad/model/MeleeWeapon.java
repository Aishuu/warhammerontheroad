package fr.eurecom.warhammerontheroad.model;

public class MeleeWeapon extends Weapon{
	
	public MeleeWeapon(String name, String description) {
		super(name, description);
	}

	public int getDegats()
	{
		return degats;
	}

}
