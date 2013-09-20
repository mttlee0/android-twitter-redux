/*
 * Last updated: 9.19.13
 * 
 */

package com.codepath.apps.simpletwitterclient;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

// Controller for the Profile screen
public class ProfileActivity extends FragmentActivity {
	TextView tvFullName;
	TextView tvTagline;
	TextView tvFollowers;
	TextView tvFollowing;
	ImageView ivProfile;
	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		loadProfileInfo();
	}
	
	// This method populates the textviews, imageview and Action Bar with the user information.
	// The user information is retrieved from SharedPreferences
	private void loadProfileInfo() {
		try 
		{
			tvFullName = (TextView)findViewById(R.id.tvFullName);
			tvTagline = (TextView)findViewById(R.id.tvTagline);
			tvFollowers = (TextView)findViewById(R.id.tvFollowers);
			tvFollowing = (TextView)findViewById(R.id.tvFollowing);
			ivProfile = (ImageView) findViewById(R.id.ivProfile);	        
			
			user = User.fromJson(new JSONObject(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userProfile", "")));
			ActionBar actionBar = getActionBar();
			actionBar.setTitle("@" + user.getScreenName());
			
			ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfile);
			tvFullName.setText(user.getName().toString());
			tvTagline.setText(user.getTagline().toString());
			tvFollowers.setText(String.valueOf(user.getFollowersCount()) + " Followers");
			tvFollowing.setText(String.valueOf(user.getFriendsCount()) + " Followings");
		} catch (JSONException e) {
			e.printStackTrace();					
		}						
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}
