package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

public class PrimaryStats implements Stats{

	private int CC, CT, F, E, Ag, Int, FM, Soc, A, B, BF, BE, M, Mag, PF, PD;

	public PrimaryStats(Race race) {
		int randB = Tools.tenDice();
		int randPD = Tools.tenDice();
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
		CC  += Tools.twentyDice();
		CT  += Tools.twentyDice();
		F   += Tools.twentyDice();
		E   += Tools.twentyDice();
		Ag  += Tools.twentyDice();
		Int += Tools.twentyDice();
		FM  += Tools.twentyDice();
		Soc += Tools.twentyDice();
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
}
