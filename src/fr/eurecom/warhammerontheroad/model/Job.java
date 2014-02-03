
package fr.eurecom.warhammerontheroad.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.R;

public class Job {
	
	private Context context;
	private String name;
	private SecondaryStats secondarystats;
	private int[] skills;
	private int[] talents;
	private int[] items;
	private int index;
	
	public Job(int index, Context context)
	{
		int j;
		int k = 0;
		this.context = context;
		this.index=index;
		InputStream is = context.getResources().openRawResource(R.raw.job);
		if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            try {
				while ( (receiveString = bufferedReader.readLine()) != null) {
					if((k/5) == index)
					{
						switch(k-(k/5)*5){
						case 0:
							name = receiveString;
							break;
							
						case 1:
							secondarystats = new SecondaryStats(receiveString);
							break;
							
						case 2:
							String skillsList[] = receiveString.split(" ");
							skills = new int[skillsList.length];
							for (j = 0; j < skillsList.length; j++)
							{
								skills[j] = Integer.parseInt(skillsList[j]);
							}
							break;
							
						case 3:
							String talentsList[] = receiveString.split(" ");
							talents = new int[talentsList.length];
							for (j = 0; j < talentsList.length; j++)
							{
								talents[j] = Integer.parseInt(talentsList[j]);
							}
							break;
						case 4:
							String itemList[] = receiveString.split(" ");
							items = new int[itemList.length];
							for (j = 0; j < itemList.length; j++)
							{
								items[j] = Integer.parseInt(itemList[j]);
							}
							break;
						}
					}
					k++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public SecondaryStats getSecondaryStats()
	{
		return secondarystats;
	}
	
	public int[] getSkills(){
		return skills;
	}
	
	public int[] getTalents(){
		return talents;
	}
	
	public int[] getItems(){
		return items;
	}

	public int getIndex() {
		return index;
	}
}
		
		
