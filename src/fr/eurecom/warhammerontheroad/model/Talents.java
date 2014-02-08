
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

	public static int getStartingTalent(int race){
		int index = new Dice().hundredDice();
		switch(race){
		case 0:
			if (index < 5)
				return 1;
			if (index < 10)
				return 2;
			if (index < 15)
				return 4;
			if (index < 19)
				return 6;
			if (index < 23)
				return 10;
			if (index < 27)
				return 20;
			if (index < 31)
				return 71;
			if (index < 36)
				return 22;
			if (index < 40)
				return 27;
			if (index < 45)
				return 32;
			if (index < 49)
				return 34;
			if (index < 54)
				return 37;
			if (index < 58)
				return 56;
			if (index < 62)
				return 58;
			if (index < 66)
				return 59;
			if (index < 70)
				return 60;
			if (index < 74)
				return 61;
			if (index < 78)
				return 62;
			if (index < 82)
				return 63;
			if (index < 86)
				return 64;
			if (index < 91)
				return 70;
			if (index < 96)
				return 78;
			else
				return 81;
			
		default :
			if (index < 6)
				return 1;
			if (index < 11)
				return 2;
			if (index < 16)
				return 4;
			if (index < 21)
				return 6;
			if (index < 26)
				return 10;
			if (index < 31)
				return 20;
			if (index < 36)
				return 71;
			if (index < 40)
				return 22;
			if (index < 44)
				return 27;
			if (index < 49)
				return 32;
			if (index < 54)
				return 34;
			if (index < 59)
				return 37;
			if (index < 63)
				return 56;
			if (index < 65)
				return 58;
			if (index < 69)
				return 59;
			if (index < 73)
				return 60;
			if (index < 77)
				return 61;
			if (index < 82)
				return 62;
			if (index < 87)
				return 63;
			if (index < 92)
				return 64;
			if (index < 97)
				return 70;
			else
				return 78;
		}
	}

}

