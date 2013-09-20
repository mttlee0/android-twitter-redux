/*
 * Last updated: 9.19.13
 * 
 */

package com.codepath.apps.simpletwitterclient.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

// Uses the Twitter Rest client to get a list of mention tweets.
public class MentionsFragment extends TweetsListFragment {
	private static final int iNumberOfTweets = 25;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RequestParams params = new RequestParams();
        params.put("count", String.valueOf(iNumberOfTweets));
        
		TwitterApp.getRestClient().getMentions(params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
				getAdapter().addAll(tweets);
				
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				Editor edit = pref.edit();
				edit.putString("mentions", jsonTweets.toString());
				edit.commit();
			}
			
			// If we are unsuccessful at getting live data, we will get data from local storage
			@Override
			public void onFailure(Throwable error, String content) {
				Toast.makeText(getActivity().getApplicationContext(), "Twitter is having issues. Offline mode.", Toast.LENGTH_SHORT).show();
				
				try
				{
					ArrayList<Tweet> tweets = Tweet.fromJson(new JSONArray(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("mentions", "")));
					
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
