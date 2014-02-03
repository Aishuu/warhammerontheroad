package fr.eurecom.warhammerontheroad.model;

public class WeaponSet {

	private MeleeWeapon rightHand;
	private MeleeWeapon leftHand;
	private RangedWeapon bothHands;
	private boolean melee;
	
	public WeaponSet(){
		rightHand = null;
		leftHand = null;
		bothHands = null;
		melee = true;
	}
	
	public void addMelee(MeleeWeapon weapon){
		if(rightHand == null)
			rightHand = weapon;
		else
			leftHand = weapon;
		return;
	}
	
	public void addRange(RangedWeapon weapon){
		bothHands = weapon;
		return;
	}
	
	public boolean canChange()
	{
		return !(bothHands == null);
	}
	public void changeStyle()
	{
		if(melee && !(bothHands == null))
			melee = false;
		else
			melee = true;
	}
	
	public Weapon getWeapon()
	{
		if (melee)
			return rightHand;
		else
			return bothHands;
	}
	
	public Weapon getLeftHand()
	{
		if(melee)
			return leftHand;
		else
			return null;
	}
}
