package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import fr.eurecom.warhammerontheroad.model.Hero;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Hero> heroes;

	public ImageAdapter(Context c, ArrayList<Hero> h) {
		this.context=c;
		this.heroes=h;
	}

	@Override
	public int getCount() {
		return heroes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		 ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(context);
	            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 10, 0, 0);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageDrawable(heroes.get(position).getResource());
	        return imageView;

	}

}
