package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public class PrimaryStats implements Stats{

	private int CC, CT, F, E, Ag, Int, FM, Soc, A, B, BF, BE, M, Mag, PF, PD;

	public PrimaryStats(Race race) {
		Dice d = new Dice();
		int randB = d.tenDice();
		int randPD = d.tenDice();
		Int = 20;
		FM  = 20;
		A   =  1;

		switch (race){
		case HUMAN : 
			CC  = 20;
			CT  = 20;
			F   = 20;
			E   = 20;
			Ag  = 20;
			Soc = 20;
			if (randB == 10)
				B = 13;
			else if (randB > 6)
				B = 12;
			else if (randB > 3)
				B = 11;
			else B = 10;
			if (randPD > 4)
				PD = 3;
			else PD = 2;
			M = 4;
			break;

		case ELF :
			CC  = 20;
			CT  = 30;
			F   = 20;
			E   = 20;
			Ag  = 30;
			Soc = 20;
			if (randB == 10)
				B = 12;
			else if (randB > 6)
				B = 11;
			else if (randB > 3)
				B = 10;
			else B = 9;
			if (randPD > 4)
				PD = 2;
			else PD = 1;
			M = 5;
			break;

		case DWARF :
			CC  = 30;
			CT  = 20;
			F   = 20;
			E   = 30;
			Ag  = 10;
			Soc = 10;
			if (randB == 10)
				B = 14;
			else if (randB > 6)
				B = 13;
			else if (randB > 3)
				B = 12;
			else B = 11;
			if (randPD > 7)
				PD = 3;
			else if (randPD > 4)
				PD = 2;
			else PD = 1;
			M = 3;
			break;

		case BANDIT :
			CC = 29;
			CT = 42;
			F = 30;
			E = 31;
			Ag = 35;
			Int = 30;
			FM = 28;
			Soc = 25;
			A = 1;
			B = 12;
			BF = 3;
			BE = 3;
			M = 4;
			Mag = 0;
			PF = 0;
			PD = 0;
			return;
			
		case GOBLIN :
			CC = 25;
			CT = 30;
			F = 30;
			E = 30;
			Ag = 25;
			Int = 25;
			FM = 30;
			Soc = 20;
			A = 1;
			B = 8;
			BF = 3;
			BE = 3;
			M = 4;
			Mag = 0;
			PF = 0;
			PD = 0;
			return;
			
		case GUARD:
			CC = 31;
			CT = 31;
			F = 33;
			E = 41;
			Ag = 30;
			Int = 38;
			FM = 28;
			Soc = 30;
			A = 1;
			B = 12;
			BF = 3;
			BE = 4;
			M = 4;
			Mag = 0;
			PF = 0;
			PD = 0;
			return;
			
		case SKELETON:
			CC = 25;
			CT = 20;
			F = 30;
			E = 30;
			Ag = 25;
			Int = 0;
			FM = 0;
			Soc = 0;
			A = 1;
			B = 10;
			BF = 3;
			BE = 3;
			M = 4;
			Mag = 0;
			PF = 0;
			PD = 0;
			return;
			
		case ORC:
			CC = 35;
			CT = 35;
			F = 35;
			E = 45;
			Ag = 25;
			Int = 25;
			FM = 30;
			Soc = 20;
			A = 1;
			B = 12;
			BF = 3;
			BE = 3;
			M = 4;
			Mag = 0;
			PF = 0;
			PD = 0;
			return;
			
		case HOBBIT :
			CC  = 10;
			CT  = 30;
			F   = 10;
			E   = 10;
			Ag  = 30;
			Soc = 30;
			if (randB == 10)
				B = 11;
			else if (randB > 6)
				B = 10;
			else if (randB > 3)
				B = 9;
			else B = 8;
			if (randPD > 7)
				PD = 3;
			else PD = 2;
			M = 4;
			break;
		default:
			break;
		}
		CC  += d.twentyDice();
		CT  += d.twentyDice();
		F   += d.twentyDice();
		E   += d.twentyDice();
		Ag  += d.twentyDice();
		Int += d.twentyDice();
		FM  += d.twentyDice();
		Soc += d.twentyDice();
		BF = F/10;
		BE = E/10;
		PF = 0;
		Mag = 0;
	}

	@Override
	public int getStats(int index) {
		switch(index){
		case 0:
			return CC;
		case 1 :
			return CT;
		case 2 :
			return F;
		case 3 :
			return E;
		case 4 :
			return Ag;
		case 5 :
			return Int;
		case 6 :
			return FM;
		case 7 :
			return Soc;
		case 8 :
			return A;
		case 9 :
			return B;
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
		stats.add(CC + "%");
		stats.add(CT + "%");
		stats.add(F + "%");
		stats.add(E + "%");
		stats.add(Ag + "%");
		stats.add(Int + "%");
		stats.add(FM + "%");
		stats.add(Soc + "%");
		stats.add(Integer.toString(A));
		stats.add(Integer.toString(B));
		stats.add(Integer.toString(BF));
		stats.add(Integer.toString(BE));
		stats.add(Integer.toString(M));
		stats.add(Integer.toString(Mag));
		stats.add(Integer.toString(PF));
		stats.add(Integer.toString(PD));
		return stats;
	}
	
	@Override
	public String describeAsString() {
		String result = Integer.toString(CC)+"PSP"+
				                                              Integer.toString(CT)+"PSP"+
				                                              Integer.toString(F)+"PSP"+
				                                              Integer.toString(E)+"PSP"+
				                                              Integer.toString(Ag)+"PSP"+
				                                              Integer.toString(Int)+"PSP"+
				                                              Integer.toString(FM)+"PSP"+
				                                              Integer.toString(Soc)+"PSP"+
				                                              Integer.toString(A)+"PSP"+
				                                              Integer.toString(B)+"PSP"+
				                                              Integer.toString(BF)+"PSP"+
				                                              Integer.toString(BE)+"PSP"+
				                                              Integer.toString(M)+"PSP"+
				                                              Integer.toString(Mag)+"PSP"+
				                                              Integer.toString(PF)+"PSP"+
				                                              Integer.toString(PD);
		return result;
	}
	@Override
	public void constructFromString(WotrService service, String s){
		String[] parts = s.split("PSP");
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
