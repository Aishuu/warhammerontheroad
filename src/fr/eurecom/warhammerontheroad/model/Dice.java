package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.network.NetworkParser;

public class Dice {
	protected ArrayList<Integer> num;
	
	public Dice() {
		this.num = new ArrayList<Integer>();
	}
	
	public int tenDice(){
		int result;
		result = (int)(Math.random()*10) + 1;
		this.num.add(result);
		return result;
	}
	
	public int twentyDice(){
		int result = tenDice() + tenDice();
		this.num.add(result);
		return result;
	}
	
	public int hundredDice(){
		int result;
		result = (int)(Math.random()*100) + 1;
		this.num.add(result);
		return result;
	}
	
	@Override
	public String toString() {
		if(this.num == null || this.num.size() == 0)
			return "";
		String s = "";
		for(Integer i: this.num)
			s += i+NetworkParser.SEPARATOR;
		return s.substring(0, s.length()-1);
	}
}
