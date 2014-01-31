package fr.eurecom.warhammerontheroad.model;

public class Armor extends GeneralItem{
	
	private int localisation;
	private int armorPoints;
	
	public Armor(String name, int localisation, int armorPoints)
	{
		super(name);
		this.localisation = localisation;
		this.armorPoints = armorPoints;
	}
	
	public int getLocalisation()
	{
		return localisation;
	}
	
	public int getArmorPoints()
	{
		return armorPoints;
	}

}
