package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public class SimulatedDice extends Dice {
	public SimulatedDice() {
		this.num = new ArrayList<Integer>();
	}
	
	public SimulatedDice(String s) {
		this.num = new ArrayList<Integer>();
		this.fromString(s);
	}

	public void queue(int n) {
		this.num.add(n);
	}

	public void fromString(String s) {
		String[] numbers = s.split("#", 0);
		for(String n: numbers)
			try {
				this.num.add(Integer.parseInt(n));
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
	}

	private int dequeue() {
		int result = this.num.get(0);
		this.num.remove(0);
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
