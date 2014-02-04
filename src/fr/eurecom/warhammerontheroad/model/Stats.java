package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public interface Stats {

	public int getStats(int index);
	public ArrayList<String> getFullStats();
	public String describeAsString();
	public void constructFromString(String s);
}