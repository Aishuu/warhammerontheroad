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
}
