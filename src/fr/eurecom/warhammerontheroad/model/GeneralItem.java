package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class GeneralItem {
	
	private String name;
	private int enc;
	private int price;
	
	public GeneralItem(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void show(){
		Log.d("item", name);
	}

}
