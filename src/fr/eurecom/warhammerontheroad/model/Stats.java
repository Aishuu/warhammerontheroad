package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.network.Describable;

public interface Stats extends Describable {

	public int getStats(int index);
	public ArrayList<String> getFullStats();
}