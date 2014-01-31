package fr.eurecom.warhammerontheroad.model;

public class RangedWeapon extends Weapon{
	
	public RangedWeapon(String name, String description) {
		super(name, description);
	}

	public int getDegats()
	{
		return degats;
	}

}
