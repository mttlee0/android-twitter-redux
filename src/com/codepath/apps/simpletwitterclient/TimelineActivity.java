/*
 * Last updated: 9.19.13
 * 
 */

package com.codepath.apps.simpletwitterclient;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletwitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.MentionsFragment;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

// Controller for the primary screen that shows the home timeline and mentions
// The class extends FragmentActivity and uses TabListener to switch between the home timeline and mentions
public class TimelineActivity extends FragmentActivity implements TabListener {
	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
				
		setupNavigationTabs();
		getAccountInfo();		
	}
	
	// This method sets up the navigation tabs for the HomeTimeline fragment and Mentions fragment
	private void setupNavigationTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		Tab tabHome = actionBar.newTab().setText("Home")
				.setTag("HomeTimelineFragment").setIcon(R.drawable.ic_home_action_bar)
				.setTabListener(this);
		Tab tabMentions = actionBar.newTab().setText("Mentions")
				.setTag("MentionsFragment").setIcon(R.drawable.ic_mention_action_bar)
				.setTabListener(this);
		
		actionBar.addTab(tabHome);
		actionBar.addTab(tabMentions);
		actionBar.selectTab(tabHome);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);		
		return true;
	}	
	
	// Gets the info of the logged-in user and saves it in JSON in SharedPreferences. Reminder to self: commit to SharedPreferences is synchronous.  
	// TODO: A better way would be to save the user info during the Login Activity
	private void getAccountInfo() {
		TwitterApp.getRestClient().getUserInfo(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonUser) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				edit.putString("loggedUser", jsonUser.toString());			
				edit.commit(); 
			}
			
			@Override
			public void onFinish() {
				try
				{
					user = User.fromJson(new JSONObject(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("loggedUser", "")));
					ActionBar actionBar = getActionBar();
					actionBar.setTitle("@" + user.getScreenName());
				} catch (JSONException e) {
					e.printStackTrace();					
				}				
			}
			
		});	
	}
	
	// This method is linked to the Compose Tweet button on the action bar.
	public void onComposeTweetClick(MenuItem item) {
		// Pass the User information to the Compose Activity so we can display the screen name and show the profile image
		Intent i = new Intent(getApplicationContext(), ComposeTweetActivity.class);
		i.putExtra("profileImageUrl", user.getProfileImageUrl());
		i.putExtra("screen_name", user.getScreenName());
		startActivityForResult(i, 7);
	}
	
	// This method is linked to the Profile icon/button on the action bar.
	public void onProfileClick(MenuItem item) {
		// Since the value of "userProfile" might have been overwritten if the user clicked on a friend's profile picture in the timeline,
		// we need to restore the value to the logged-in user when the user clicks the Profile button. The Profile button should always
		// show the info for the logged-in user, however we do reuse the Profile Activity to show the info for other people.
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor edit = pref.edit();
		edit.putString("userProfile", user.getUser().toString());			
		edit.commit(); 
		
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
	}

	// This method will refresh the HomeTimeline after the user finishes composing a tweet.
	/* TODO: Ask Tim & Nathan if there is a better way to avoid the InvalidStateException other than using commitAllowingStateLoss().
	*  I tried moving the fragment transaction to onPostResume but that caused a different problem with getting a Null when
	*  calling getActivity() in the Fragment.
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		FragmentManager manager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();		
		fts.replace(R.id.frame_container, new HomeTimelineFragment());		
		fts.commitAllowingStateLoss();	
	}
		
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		FragmentManager manager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();
		
		if (tab.getTag() == "HomeTimelineFragment") {
			fts.replace(R.id.frame_container, new HomeTimelineFragment());
		} else {
			fts.replace(R.id.frame_container, new MentionsFragment());
		}
		
		fts.commit();
	}	

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub		
	}
}
