package fr.eurecom.warhammerontheroad.model;

public class MeleeWeapon extends Weapon{
	
	public MeleeWeapon(String name, int groupe, int degats, int attribut) {
		super(name, groupe, degats, attribut);
	}

	public int getDegats()
	{
		return degats;
	}
	
	public void show(){
		super.show();
	}
	
	public String[] toArrayString(){
		String[] result = new String[5];
		result[0] = name;
		result[1] = Integer.toString(degats);
		result[2] = "";
		result[3] = "";
		if (attribut == 1)
			result[4] = "defensive, speciale";
		else
			result[4] = "";
		return result;
	}

}
