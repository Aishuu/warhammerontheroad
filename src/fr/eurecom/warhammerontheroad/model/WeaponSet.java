package fr.eurecom.warhammerontheroad.model;

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
		this.printStackTrace();
		if(rightHand == null)
			rightHand = weapon;
		else
			leftHand = weapon;
	}
	
	public void addRange(RangedWeapon weapon){
		this.printStackTrace();
		bothHands = weapon;
	}
	
	public boolean canChange()
	{
		this.printStackTrace();
		return !(bothHands == null);
	}
	
	public void changeStyle()
	{
		this.printStackTrace();
		if(melee && !(bothHands == null))
			melee = false;
		else
			melee = true;
	}
	
	public Weapon getWeapon()
	{
		this.printStackTrace();
		if (melee)
			return rightHand;
		else
			return bothHands;
	}
	
	public Weapon getLeftHand()
	{
		this.printStackTrace();
		if(melee)
			return leftHand;
		else
			return null;
	}
	
	private void printStackTrace() {
		for(StackTraceElement s: Thread.currentThread().getStackTrace())
			Log.d("WeaponSet", s.toString());
	}
}
