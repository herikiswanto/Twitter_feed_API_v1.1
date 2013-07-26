package me.kaidul.twitter7feed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Twitter7FeedActivity extends Activity {

	LazyAdapter adapter;
	ListView list;

	JSONObject jsonObj = null;
	JSONArray jsonArray = null;
	ArrayList<HashMap<String, String>> tweets = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> screenName = new HashMap<String, String>();
	String[] names = { "Emisoras Unidas 89.7", "MuniGuate",
			"Publinews Guatemala", "El Periódico", "Prensa Libre",
			"Diario La Hora", "elQuetzalteco", "Nuestro Diario", "Siglo.21" };

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_twitter7_feed);
		
		list = (ListView) findViewById(R.id.list);
		
		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.header);
		TextView date = (TextView) rLayout.findViewById(R.id.date);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		String currentDateandTime = sdf.format(new Date());
		date.setText(currentDateandTime);
		
		String[] users = getResources().getStringArray(R.array.users);
		for (int i = 0; i < users.length; i++) {
			screenName.put(names[i], users[i]);
		}
		
		new GetFeedTask().execute(CommonUtils.BEARER_TOKEN, CommonUtils.URL);
//		callAsynchronousTask();
	}

	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							new GetFeedTask().execute(CommonUtils.BEARER_TOKEN,
									CommonUtils.URL);
						} catch (Exception e) {
							// fuck will happens here
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 30000);
	}

	protected class GetFeedTask extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(
				Twitter7FeedActivity.this);

		protected void onPreExecute() {
			this.dialog.setMessage("Please wait");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				DefaultHttpClient httpclient = new DefaultHttpClient(
						new BasicHttpParams());
				HttpGet httpget = new HttpGet(params[1]);
				httpget.setHeader("Authorization", "Bearer " + params[0]);
				httpget.setHeader("Content-type", "application/json");

				InputStream inputStream = null;
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inputStream.close();
				return sb.toString();
			} catch (Exception e) {
				Log.e("GetFeedTask", "Error:" + e.getMessage());
				return null;
			}
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String jsonText) {

			try {
				jsonArray = new JSONArray(jsonText);
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject obj = jsonArray.getJSONObject(i);
					map.put(CommonUtils.KEY_TXT, obj.getString("text"));
					String dateString = obj.getString("created_at");
					Date date;
					try {
						date = new java.text.SimpleDateFormat(
								"E MMM d HH:mm:ss Z yyyy").parse(dateString);
						long diffMSec = new Date().getTime() - date.getTime();
						if (diffMSec > 86400000)
							break;
						int flag = 0;
						long diff = diffMSec / 1000;
						if (diff >= 60) {
							diff /= 60;
							flag = 1;
						}
						if (diff >= 60) {
							diff /= 60;
							flag = 2;
						}
						String time = "" + diff;
						String postfix = null;
						if (flag == 0)
							postfix = "s";
						else if (flag == 1)
							postfix = "m";
						else
							postfix = "hr";
						time += postfix;

						map.put(CommonUtils.KEY_DATE, time);
					} catch (ParseException e) {
						// fuck will happen
					}
					JSONObject user = obj.getJSONObject(CommonUtils.KEY_USER);
					map.put(CommonUtils.KEY_NAME,
							user.getString(CommonUtils.KEY_NAME));
					String screen_name = screenName.get(user
							.getString(CommonUtils.KEY_NAME)) != null ? " @"
							+ screenName.get(user
									.getString(CommonUtils.KEY_NAME)) : "";

					map.put(CommonUtils.KEY_SCREEN_NAME, screen_name);
					map.put(CommonUtils.KEY_PROFILE_IMG,
							user.getString(CommonUtils.KEY_PROFILE_IMG));

					tweets.add(map);
				}
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
			adapter = new LazyAdapter(Twitter7FeedActivity.this, tweets);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			list.setAdapter(adapter);
//			adapter.notifyDataSetChanged();
		}
	}
}
