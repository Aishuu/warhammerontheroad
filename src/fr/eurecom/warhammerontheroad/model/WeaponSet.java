package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import android.util.Log;

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
	
	public void show()
	{
		Log.d("weaponset","melee :");
		rightHand.show();
		if (!(leftHand == null))
			leftHand.show();
		Log.d("weaponset","ranged :");
		if (!(bothHands == null))
			bothHands.show();
	}
	
	public ArrayList<String[]> toArrayString()
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		result.add(rightHand.toArrayString());
		if(leftHand != null)
			result.add(leftHand.toArrayString());
		if(bothHands != null)
			result.add(bothHands.toArrayString());
		return result;
	}
}
