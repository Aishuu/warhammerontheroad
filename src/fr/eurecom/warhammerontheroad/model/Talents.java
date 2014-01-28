package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class Talents {
	
	private int id;
	private String name;
	
	public Talents(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean compare(Talents s) {
		if (s.id == id)
			return true;
		return false;
	}
	
	public void show(){
		Log.d("talent", name);
	}

}
