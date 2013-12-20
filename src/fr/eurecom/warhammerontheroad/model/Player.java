package fr.eurecom.warhammerontheroad.model;

import android.content.Context;

public class Player {
	private Context context;
	private String name;
	private Hero hero;
	
	public Player(Context context, String name) {
		this.context = context;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void createHero(int race) {
		this.hero = new Hero(this.context, race);
	}
	
	public Hero getHero() {
		return this.hero;
	}
}
