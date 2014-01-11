package fr.eurecom.warhammerontheroad.network;

import fr.eurecom.warhammerontheroad.model.WotrService;

public interface Describable {
	public String describeAsString();
	public void constructFromString(WotrService service, String s);
	public String representInString();
}
