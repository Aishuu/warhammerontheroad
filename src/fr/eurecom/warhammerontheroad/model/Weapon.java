package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public abstract class Weapon extends GeneralItem{
	
	private int groupe;
	protected int degats;
	protected int attribut;
	
	public Weapon (String name, int groupe, int degats, int attribut)
	{
		super(name);
		this.groupe = groupe;
		this.degats = degats;
		this.attribut = attribut;
	}
	
	public abstract int getDegats();
	public void show(){
		super.show();
		Log.d("Weapon", "groupe : "+groupe+",degats : "+degats+",attribut :"+attribut);
	}
}
