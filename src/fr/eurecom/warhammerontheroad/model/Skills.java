package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class Skills {
	
	private int id;
	private String name;
	private int type; //0 = base; 1 = advanced
	private int stat; // same order as in the character sheet. begin with 0
    private int level; // 0 = not-usable, 1 = known or basic, 2 = 10% bonus, 3 = 20 % bonus;
    private String speciality;
    
    public Skills(int id, String description)
    {
    	this.id = id;
    	String[] parameters = description.split(" ");
    	name = parameters[0];
    	type = Integer.parseInt(parameters[1]);
    	stat = Integer.parseInt(parameters[2]);
    	if(type == 0)
    		level = 0;
    	else
    		level = 1;
    	speciality = "";
    }
    
    public Skills(int id, String description, String speciality)
    {
    	this.id = id;
    	String[] parameters = description.split(" ");
    	name = parameters[0];
    	type = Integer.parseInt(parameters[1]);
    	stat = Integer.parseInt(parameters[2]);
    	level = 1;
    	this.speciality = speciality;
    }
    
    public void upgrade()
    {
    	if(level < 3)
    		level++;
    }
    
    public int testModificator()
    {
    	if (type == 1 && level == 0)
    		return -1;
    	else
    		return 10*stat+level;
    }
    
    public boolean compare(Skills skillb){
    	if ((skillb.id == this.id) && (skillb.speciality.equals(this.speciality)))
    		return true;
    	return false;
    }
    
    public void show(){
    	String skills = name + " (" + speciality + ")";
    	if (type == 1)
    		skills += " (advanced : ";
    	else
    		skills += " (basic :";
    	switch(stat){
    	case 0:
    		skills += " CC, ";
    		break;
    	case 1:
    		skills += " CT, ";
    		break;
    	case 2:
    		skills += " F, ";
    		break;
    	case 3:
    		skills += " E, ";
    		break;
    	case 4:
    		skills += " Ag, ";
    		break;
    	case 5:
    		skills += " Int, ";
    		break;
    	case 6:
    		skills += " FM, ";
    		break;
    	case 7:
    		skills += " Soc, ";
    		break;
    	}
    	
    	switch(level){
    	case 0:
    		skills += "non aquired)";
    		break;
    		
    	case 1:
    		skills += "aquired)";
    		break;
    		
    	case 2:
    		skills += "mastered +10%";
    		break;
    		
    	case 3:
    		skills += "mastered +20%";
    		break;
    	}
    	Log.d("skill", skills);
    }
}