package fr.eurecom.warhammerontheroad.model;

public class ChatMessage {
	private String color;
	private String message;
	private String name;
	private boolean prive;

	public ChatMessage(String name, String message, Game game, boolean prive) {
		if(name.equals(WotrService.GM_NAME)) {
			color="#8B0000";
			this.name = WotrService.GM_NAME;
		}
		else {
			Player p = game.getPlayer(name);
			if(p == null) {
				this.name = "???";
				this.color = "#000000";
			}
			else {
				this.name = name;
				this.color=p.getColor().getValue();
			}
		}
		this.message = message;
		this.prive = prive;
	}
	
	public ChatMessage(Game game, String message, boolean prive) {
		if(game.isGM()) {
			this.name = WotrService.GM_NAME;
			this.color = "#8B0000";
		}
		else {
			this.color = game.getMe().getColor().getValue();
			this.name = game.getMe().getName();
		}
		this.message = message;
		this.prive = prive;
	}
	
	public ChatMessage(String message) {
		this.message = message;
		this.name = null;
		this.prive = false;
		this.color = "#444444";
	}
	
	public String describeAsHTML() {
		String s = "<font color="+this.color+">";
		if(this.prive)
			s += "<i>";
		if(this.name != null)
			s += this.name+": ";
		s += this.message;
		if(this.prive)
			s += "</i>";
		return s+"</font>";
	}
}
