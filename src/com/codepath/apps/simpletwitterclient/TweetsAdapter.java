/*
 * Last updated: 9.19.13
 * 
 */

package com.codepath.apps.simpletwitterclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetsAdapter extends ArrayAdapter<Tweet>{
	
	public TweetsAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = convertView;
	    if (view == null) {
	    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	view = inflater.inflate(R.layout.tweet_item, null);
	    }

        Tweet tweet = getItem(position);
        
        ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), imageView);
        imageView.setTag(tweet.getUser());
        
        // Here we set a ClickListener for the imageview that represents the Profile image.
        // We use the setTag and getTag methods to identify the specific View within the ListView.
        // TODO: Ask Tim & Nathan if there is a more efficient way to identify the specific view. With a large number of 
        // tweets this would set a tag with the User object for every tweet in the timeline.
        imageView.setOnClickListener(new ImageView.OnClickListener() {        
        	public void onClick(View v) {
        		// We put the user information in SharedPreferences to support offline mode.
        		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				Editor edit = pref.edit();
				edit.putString("userProfile", ((User)v.getTag()).getUser().toString());			
				edit.commit(); 
				
				Intent i = new Intent(v.getContext(), ProfileActivity.class);
				v.getContext().startActivity(i);
        	}
        });
        
        TextView nameView = (TextView) view.findViewById(R.id.tvName);

        String formattedName = "<b>" + tweet.getUser().getName() + "</b>" + " <small><font color='#777777'>@" +
                tweet.getUser().getScreenName() + "</font></small>";
        nameView.setText(Html.fromHtml(formattedName));

        TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml(tweet.getBody()));
        
        // Added the timestamp for each tweet
        TextView tvTimestamp = (TextView) view.findViewById(R.id.tvTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat ("MM-dd hh:mm");
        sdf.setTimeZone (TimeZone.getTimeZone ("PST"));
        String timestamp = sdf.format(new Date(tweet.getCreateTime()));
        String formattedTimestamp = "<font color='#777777'>" + timestamp + "</font>";
        tvTimestamp.setText(Html.fromHtml(formattedTimestamp));
        
        return view;
	}	
}
