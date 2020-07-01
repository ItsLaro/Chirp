package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    public static final String TAG = "TweetDetailsActivity"; //logging purposes

    private ActivityTweetDetailsBinding binding;

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View binding
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        View timelineView = binding.getRoot();
        setContentView(timelineView);
        setTitle(R.string.tweet_details_title);

        //Getting parcel from last Activity
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet_object"));
        Log.d(TAG, "Loaded details for tweet: " + tweet.getBody());


        //Setting views to passed Tweet data
        binding.displayName.setText(tweet.user.getName());
        binding.userHandle.setText(tweet.user.getScreenName());
        binding.tweetBody.setText(tweet.getBody());
        binding.timestamp.setText(tweet.getCreatedAt());

        //Media
        Glide.with(this)
                .load(tweet.user.getProfileImageUrl())
                .transform(new CircleCrop())
                .into(binding.profileImage);

        //If there's an image
        if(tweet.getMediaUrls().size() > 0){
            //Set media
            Glide.with(this)
                    .load(tweet.getMediaUrl(0))
                    .centerCrop()
                    .transform(new RoundedCorners(30))
                    .into(binding.tweetMedia);

            //Recovers visibility on a recycled item after it had been toggled off
            binding.tweetMedia.setVisibility(View.VISIBLE);
        }
        else{
            //No image? Hide the view.
            binding.tweetMedia.setVisibility(View.GONE);
        }

        //Setting onClickListeners for tweet actions in the action pane
        binding.actionComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Reply clicked on tweet: " + tweet.getBody());
            }
        });

        binding.actionRT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "RT clicked on tweet: " + tweet.getBody());
            }
        });

        binding.actionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Favorite clicked on tweet: " + tweet.getBody());
            }
        });

        binding.actionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Share clicked on tweet: " + tweet.getBody());
            }
        });
    }
}