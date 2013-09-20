/*
 * Last updated: 9.19.13
 * 
 */

package com.codepath.apps.simpletwitterclient.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TweetsAdapter;
import com.codepath.apps.simpletwitterclient.TwitterApp;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

// Uses the Twitter Rest client to show a list of tweets from any user. Retrieves the user's screen_name from SharedPreferences. 
public class UserTimelineFragment extends TweetsListFragment {
	private static final int iNumberOfTweets = 25;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		User user;
		
		RequestParams params = new RequestParams();
		params.put("count", String.valueOf(iNumberOfTweets));
		try
		{
			user = User.fromJson(new JSONObject(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("userProfile", "")));
			params.put("screen_name", user.getScreenName());
		} catch (JSONException e) {
			e.printStackTrace();
		}			      
        
		TwitterApp.getRestClient().getUserTimeline(params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
				getAdapter().addAll(tweets);
				
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				Editor edit = pref.edit();
				edit.putString("usertimeline", jsonTweets.toString());
				edit.commit();
			}
			
			// If we are unsuccessful at getting live data, we will get data from local storage
			@Override
			public void onFailure(Throwable error, String content) {
				Toast.makeText(getActivity().getApplicationContext(), "Twitter is having issues. Offline mode.", Toast.LENGTH_SHORT).show();
				
				try
				{
					ArrayList<Tweet> tweets = Tweet.fromJson(new JSONArray(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("usertimeline", "")));
					
					ListView lvTweets = (ListView)getActivity().findViewById(R.id.lvTweets);
					TweetsAdapter adapter = new TweetsAdapter(getActivity(), tweets);
					lvTweets.setAdapter(adapter);				
				} catch (JSONException e) {
					e.printStackTrace();					
				}
			} 
		});

	}

}
