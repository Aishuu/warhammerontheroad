package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public class ActualStats implements Stats {
	
	private PrimaryStats primary;
	private SecondaryStats secondary;
	
	public ActualStats(PrimaryStats primary, SecondaryStats secondary){
		this.primary = primary;
		this.secondary = secondary;
	}
	
	public ActualStats(PrimaryStats primary){
		this.primary = primary;
	}

	public void SetSecondaryStats(SecondaryStats secondary)
	{
		this.secondary = secondary;
	}
	@Override
	public int getStats(int index) {
		if (index == 10 || index == 11)
		{
			if (index == 10)
				return ((primary.getStats(2)+secondary.getStats(2))/10);
			else
				return ((primary.getStats(3)+secondary.getStats(3))/10);
		}else{
			return primary.getStats(index)+secondary.getStats(index);
		}
	}

	@Override
	public ArrayList<String> getFullStats() {
		ArrayList<String> stats = new ArrayList<String>();
		int i;
		for (i = 0; i<8; i++)
		{
			stats.add(Integer.toString(primary.getStats(i)+secondary.getStats(i)));
		}
		for (i = 8; i < 16; i++)
		{
			if (i == 10 || i == 11)
			{
				if (i == 10)
					stats.add(Integer.toString(primary.getStats(2)+secondary.getStats(2)/10));
				else
					stats.add(Integer.toString(primary.getStats(3)+secondary.getStats(3)/10));
			}else{
				stats.add(Integer.toString(primary.getStats(i)+secondary.getStats(i)));
			}
		}
		return stats;
	}
	
	public ArrayList<String> getPrimaryStats()
	{
		return primary.getFullStats();
	}
	
	public ArrayList<String> getSecondaryStats()
	{
		return secondary.getFullStats();
	}

	@Override
	public String describeAsString() {
		String result = primary.describeAsString() + "SSS" + secondary.describeAsString();
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		String[] parts = s.split("SSS");
		primary.constructFromString(service, parts[0]);
		this.secondary = new SecondaryStats();
		secondary.constructFromString(service, parts[1]);
	}

	@Override
	public String representInString() {
		return null;
	}

}
