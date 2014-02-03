package fr.eurecom.warhammerontheroad.model;

public abstract class Weapon extends GeneralItem{
	
	private int groupe;
	protected int degats;
	private int attribut;
	
	public Weapon (String name, int groupe, int degats, int attribut)
	{
		super(name);
		this.groupe = groupe;
		this.degats = degats;
		this.attribut = attribut;
	}
	
	public abstract int getDegats();
}
