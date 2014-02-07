package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public class SecondaryStats implements Stats{
	
	private int CC, CT, F, E, Ag, Int, FM, Soc, A, B, BF, BE, M, Mag, PF, PD;
	
	public SecondaryStats(String input)
	{
		String stats[] = input.split(" ");
		CC  = Integer.parseInt(stats[0]);
		CT  = Integer.parseInt(stats[1]);
		F   = Integer.parseInt(stats[2]);
		E   = Integer.parseInt(stats[3]);
		Ag  = Integer.parseInt(stats[4]);
		Int = Integer.parseInt(stats[5]);
		FM  = Integer.parseInt(stats[6]);
		Soc = Integer.parseInt(stats[7]);
		A   = Integer.parseInt(stats[8]);
		B   = Integer.parseInt(stats[9]);
		BF  = 0;
		BE  = 0;
		M   = 0;
		Mag = 0;
		PF  = 0;
		PD  = 0;
	}
	
	public void upgrade(int index)
	{
		int level, max;
		switch(index){
		case 0 :
			level = CC/10;
			max = CC - 10*level;
			if(level<max)
				CC += 10;
			break;
		case 1 :
			level = CT/10;
			max = CT - 10*level;
			if(level<max)
				CT += 10;
			break;
		case 2 :
			level = F/10;
			max = F - 10*level;
			if(level<max)
				F += 10;
			break;
		case 3 :
			level = E/10;
			max = E - 10*level;
			if(level<max)
				E += 10;
			break;
		case 4 :
			level = Ag/10;
			max = Ag - 10*level;
			if(level<max)
				Ag += 10;
			break;
		case 5 :
			level = Int/10;
			max = Int - 10*level;
			if(level<max)
				Int += 10;
			break;
		case 6 :
			level = FM/10;
			max = FM - 10*level;
			if(level<max)
				FM += 10;
			break;
		case 7 :
			level = Soc/10;
			max = Soc - 10*level;
			if(level<max)
				Soc += 10;
			break;
		case 8 :
			level = A/10;
			max = A - 10*level;
			if(level<max)
				A += 10;
			break;
		case 9 :
			level = B/10;
			max = B - 10*level;
			if(level<max)
				B += 10;
			break;
		}
	}

	@Override
	public int getStats(int index) {
		switch(index){
		case 0:
			return (CC/10)*5;
		case 1 :
			return (CT/10)*5;
		case 2 :
			return (F/10)*5;
		case 3 :
			return (E/10)*5;
		case 4 :
			return (Ag/10)*5;
		case 5 :
			return (Int/10)*5;
		case 6 :
			return (FM/10)*5;
		case 7 :
			return (Soc/10)*5;
		case 8 :
			return A/10;
		case 9 :
			return B/10;
		case 10 :
			return BF;
		case 11 :
			return BE;
		case 12 :
			return M;
		case 13 :
			return Mag;
		case 14 :
			return PF;
		default :
			return PD;
		}
	}

	@Override
	public ArrayList<String> getFullStats() {
		ArrayList<String> stats = new ArrayList<String>();
		stats.add(CC == 0 ? "-" : "+" + (CC/10)*5 + "/" + (CC-(CC/10))*5+ "%");
		stats.add(CT == 0 ? "-" : "+" + (CT/10)*5 + "/" + (CT-(CT/10))*5+ "%");
		stats.add(F == 0 ? "-" : "+" + (F/10)*5 + "/" + (F-(F/10))*5+ "%");
		stats.add(E == 0 ? "-" : "+" + (E/10)*5 + "/" + (E-(E/10))*5+ "%");
		stats.add(Ag == 0 ? "-" : "+" + (Ag/10)*5 + "/" + (Ag-(Ag/10))*5+ "%");
		stats.add(Int == 0 ? "-" : "+" + (Int/10)*5 + "/" + (Int-(Int/10))*5+ "%");
		stats.add(FM == 0 ? "-" : "+" + (FM/10)*5 + "/" + (FM-(FM/10))*5+ "%");
		stats.add(Soc == 0 ? "-" : "+" + (Soc/10)*5 + "/" + (Soc-(Soc/10))*5+ "%");
		stats.add(A == 0 ? "-" : "+" + A/10 + "/" + (A-(A/10)));
		stats.add(B == 0 ? "-" : "+" + B/10 + "/" + (B-(B/10)));
		stats.add(BE == 0 ? "-" : "+" + BF);
		stats.add(BF == 0 ? "-" : "+" + BE);
		stats.add(M == 0 ? "-" : "+" + M);
		stats.add(Mag == 0 ? "-" : "+" + Mag);
		stats.add(PF == 0 ? "-" : "+" + PF);
		stats.add(PD == 0 ? "-" : "+" + PD);
		return stats;
	}
	@Override
	public String describeAsString() {
		String result = Integer.toString(CC)+"STS"+
				                                              Integer.toString(CT)+"STS"+
				                                              Integer.toString(F)+"STS"+
				                                              Integer.toString(E)+"STS"+
				                                              Integer.toString(Ag)+"STS"+
				                                              Integer.toString(Int)+"STS"+
				                                              Integer.toString(FM)+"STS"+
				                                              Integer.toString(Soc)+"STS"+
				                                              Integer.toString(A)+"STS"+
				                                              Integer.toString(B)+"STS"+
				                                              Integer.toString(BF)+"STS"+
				                                              Integer.toString(BE)+"STS"+
				                                              Integer.toString(M)+"STS"+
				                                              Integer.toString(Mag)+"STS"+
				                                              Integer.toString(PF)+"STS"+
				                                              Integer.toString(PD);
		return result;
	}
	@Override
	public void constructFromString(WotrService service, String s){
		String[] parts = s.split("STS");
		CC = Integer.parseInt(parts[0]);
		CT = Integer.parseInt(parts[1]);
		F = Integer.parseInt(parts[2]);
		E = Integer.parseInt(parts[3]);
		Ag = Integer.parseInt(parts[4]);
		Int = Integer.parseInt(parts[5]);
		FM = Integer.parseInt(parts[6]);
		Soc = Integer.parseInt(parts[7]);
		A = Integer.parseInt(parts[8]);
		B = Integer.parseInt(parts[9]);
		BF = Integer.parseInt(parts[10]);
		BE = Integer.parseInt(parts[11]);
		M = Integer.parseInt(parts[12]);
		Mag = Integer.parseInt(parts[13]);
		PF = Integer.parseInt(parts[14]);
		PD = Integer.parseInt(parts[15]);
	}

	@Override
	public String representInString() {
		return null;
	}
}
