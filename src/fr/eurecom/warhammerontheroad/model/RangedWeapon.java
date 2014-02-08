package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class RangedWeapon extends Weapon{
	
	private int range;
	private int reload;
	
	public RangedWeapon(String name, int groupe, int degats, int attribut, int range, int reload) {
		super(name, groupe, degats, attribut);
		this.range = range;
		this.reload = reload;
	}

	public int getDegats()
	{
		return degats;
	}
	
	public int getRange()
	{
		return range;
	}
	
	public int getReload()
	{
		return reload;
	}
	
	public void show(){
		super.show();
		Log.d("ranged", "range : "+range+",reload : "+reload);
	}
	
	public String[] toArrayString(){
		String[] result = new String[5];
		result[0] = name;
		result[1] = Integer.toString(degats);
		result[2] = Integer.toString(range);
		result[3] = Integer.toString(reload);
		if (attribut == 2)
			result[4] = "perforante";
		else
			result[4] = "";
		return result;
	}
}
