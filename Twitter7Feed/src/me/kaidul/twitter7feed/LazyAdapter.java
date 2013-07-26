package me.kaidul.twitter7feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	
	void clear() {
		data.clear();
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_row, null);
		TextView name = (TextView) vi.findViewById(R.id.name);
		TextView tweet = (TextView) vi.findViewById(R.id.text);
		TextView date = (TextView) vi.findViewById(R.id.created_at);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.profile_image_url);
		TextView screenName = (TextView) vi.findViewById(R.id.screen_name);

		HashMap<String, String> tweets = new HashMap<String, String>();
		tweets = data.get(position);

		name.setText(tweets.get(CommonUtils.KEY_NAME));
		
		String feed = tweets.get(CommonUtils.KEY_TXT);
		
		Pattern mentionPattern = Pattern.compile("(@[A-Za-z0-9_-]+)");
		Pattern hashtagPattern = Pattern.compile("(#[A-Za-z0-9_-]+)");
		Pattern urlPattern = Patterns.WEB_URL;
		
		StringBuffer sb = new StringBuffer(feed.length());
		Matcher o = hashtagPattern.matcher(feed);

		while (o.find()) {
		    o.appendReplacement(sb, "<font color=\"#00ACEE\">" + o.group(1) + "</font>");
		}
		o.appendTail(sb);

		Matcher n = mentionPattern.matcher(sb.toString());
		sb = new StringBuffer(sb.length());

		while (n.find()) {
		    n.appendReplacement(sb, "<font color=\"#00ACEE\">" + n.group(1) + "</font>");
		}
		n.appendTail(sb);

		Matcher m = urlPattern.matcher(sb.toString());
		sb = new StringBuffer(sb.length());

		while (m.find()) {
		    m.appendReplacement(sb, "<font color=\"#00ACEE\">" + m.group(1) + "</font>");
		}
		m.appendTail(sb);

		tweet.setText(Html.fromHtml(sb.toString()));
	    
		screenName.setText(tweets.get(CommonUtils.KEY_SCREEN_NAME));
		date.setText(tweets.get(CommonUtils.KEY_DATE));
		imageLoader.DisplayImage(tweets.get(CommonUtils.KEY_PROFILE_IMG),
				thumb_image);
		return vi;
	}
}