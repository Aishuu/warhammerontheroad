package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import android.util.Log;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

public class SimulatedDice extends Dice {
	private int index;
	
	public SimulatedDice() {
		this.index = 0;
		this.num = new ArrayList<Integer>();
	}
	
	public SimulatedDice(String s) {
		this.index = 0;
		this.num = new ArrayList<Integer>();
		this.fromString(s);
	}

	public void queue(int n) {
		this.num.add(n);
	}

	public void fromString(String s) {
		Log.d("SimulatedDice", "DEBUG : new SimulatedDice \""+s+"\"");
		String[] numbers = s.split(NetworkParser.SEPARATOR, 0);
		for(String n: numbers) {
			try {
				this.num.add(Integer.parseInt(n));
				Log.d("SimulatedDice", "DEBUG : "+n);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private int dequeue() {
		int result = this.num.get(index);
		this.index ++;
		Log.d("SimulatedDice", "Dequeue #"+this.index);
		return result;
	}

	public int tenDice(){
		return dequeue();
	}

	public int twentyDice(){
		return dequeue();
	}

	public int hundredDice(){
		return dequeue();
	}
}
