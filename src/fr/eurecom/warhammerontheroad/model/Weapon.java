package fr.eurecom.warhammerontheroad.model;

public abstract class Weapon extends GeneralItem{
	
	private int groupe;
	protected int degats;
	private int attribut;
	
	public Weapon (String name, String description)
	{
		super(name);
		String[] parameters = description.split(" ");
		groupe = Integer.parseInt(parameters[0]);
		degats = Integer.parseInt(parameters[1]);
		attribut = Integer.parseInt(parameters[2]);
	}
	
	public abstract int getDegats();

}
