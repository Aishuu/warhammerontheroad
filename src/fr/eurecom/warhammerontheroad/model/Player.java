package fr.eurecom.warhammerontheroad.model;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.content.Context;
import android.util.Log;

public class Player extends Hero {	
	private final static String TAG =		"Player";
	
	private String name;
	private Color color;
	
	public Player(Context context, String name) {
		super(context);
		this.setId(0);
		this.name = name;
	}

	public Player(Context context, Race race, String name) {
		super(context);
		this.setId(0);
		this.name = name;
		init(race);
	}
	
	public void setColor(Color color) {
		this.color = color;
		
		// TODO: change this according to color...
		this.resource = R.drawable.mage;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String representInString() {
		return this.name;
	}
	
	@Override
	public String describeAsString() {
		String result = NetworkParser.constructStringFromArgs(color.toString(), super.describeAsString());
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		String[] parts = s.split(NetworkParser.SEPARATOR, 2);
		if(parts.length < 2) {
			Log.e(TAG, "Not enough arguments !");
			return;
		}
		super.constructFromString(service, parts[1]);
		try {
			this.setColor(Color.colorFromIndex(Integer.parseInt(parts[0])));
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number !");
		}
	}
}
