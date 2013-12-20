package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class Stats {

	private int CC, CT, F, E, Ag, Int, FM, Soc, A, B, BF, BE, M, Mag, PF, PD;
	private int type; // 0 = base, 1 = secondaire, 2 = total;

	public Stats(int race) {
		type = 0;
		int randB = Tools.tenDice();
		int randPD = Tools.tenDice();
		Int = 20;
		FM  = 20;
		A   =  1;

		switch (race){
		case 0 : 
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

		case 1 :
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

		case 2 :
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

		default :
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
	
	public Stats(String input)
	{
		type = 1;
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

	public Stats(Stats primary, Stats secondary){
		type = 2;
		CC = primary.CC + (secondary.CC/10)*5;
		CT = primary.CT + (secondary.CT/10)*5;
		F = primary.F + (secondary.F/10)*5;
		E = primary.E + (secondary.E/10)*5;
		Ag = primary.Ag + (secondary.Ag/10)*5;
		Int = primary.Int + (secondary.Int/10)*5;
		FM = primary.FM + (secondary.FM/10)*5;
		Soc = primary.Soc + (secondary.Soc/10)*5;
		A = primary.A + secondary.A/10;
		B = primary.B + secondary.B/10;
		BF = F/10;
		BE = E/10;
		M = primary.M;
		Mag = primary.Mag;
		PF = primary.PF;
		PD = primary.PD;
	}
	
	public void upgrade(int index)
	{
		if(type == 1){
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

	}

	public void show() {
		String stats = "CC : " + CC + "; CT : " + CT + "; F : " + F + "; E : " + E + "; Ag : " + Ag + "; Int : " + Int + "; FM : " + FM + "; Soc : " + Soc + "; A : " + A + "; B : " + B + "; BE : " + BE + "; BF : " + BF + "; M : " + M + "; Mag : " + Mag + "; PF : " + PF + "; PD : " + PD;
		Log.d("stats", stats);
		
	}
}
