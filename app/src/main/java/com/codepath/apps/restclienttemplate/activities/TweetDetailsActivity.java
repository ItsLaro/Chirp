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
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    public static final String TAG = "TweetDetailsActivity"; //logging purposes

    TwitterClient twitterClient;
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

        twitterClient = TwitterApp.getRestClient(this);

        //Getting parcel from last Activity
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet_object"));
        Log.d(TAG, "Loaded details for tweet: " + tweet.getBody());


        //Setting views to passed Tweet data
        binding.displayName.setText(tweet.user.getName());
        binding.userHandle.setText(tweet.user.getScreenName());
        binding.tweetBody.setText(tweet.getBody());
        binding.timestamp.setText(tweet.getCreatedAt());

        if(tweet.isFavorited()){
            binding.actionFavorite.setSelected(true);
        }

        if(tweet.isRetweet()){
            binding.actionRT.setSelected(true);
        }
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

        //Click listener on 'RT' button
        binding.actionRT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "RT clicked on tweet: " + tweet.getBody());

                if(tweet.isRetweet()){
                    //Pressing favorite on favorited tweet will trigger POST request to unlike it
                    twitterClient.postUnretweet(tweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to unRT succeeded: " + json.toString());
                            tweet.toggleRetweeted(); //Updates model
                            binding.actionRT.setSelected(false); //Updates visual
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Request to unRT failed: " + throwable);

                        }
                    });
                }
                else{
                    //Pressing favorite on unfavorited tweet will trigger POST request to like it
                    twitterClient.postRetweet(tweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to RT succeeded: " + json.toString());
                            tweet.toggleRetweeted(); //Updates model
                            binding.actionRT.setSelected(true); //Updated visual
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Request to RT failed: " + throwable);
                        }
                    });
                }
            }
        });

        //Click listener on 'Like' button
        binding.actionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Favorite clicked on tweet: " + tweet.getBody());

                if(tweet.isFavorited()){
                    //Pressing favorite on favorited tweet will trigger POST request to unlike it
                    twitterClient.postUnlike(tweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to unlike succeeded: " + json.toString());
                            tweet.toggleFavorited(); //Updates model
                            binding.actionFavorite.setSelected(false); //Updates visual
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Request to unlike failed: " + throwable);

                        }
                    });
                }
                else{
                    //Pressing favorite on unfavorited tweet will trigger POST request to like it
                    twitterClient.postLike(tweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to like succeeded: " + json.toString());
                            tweet.toggleFavorited(); //Updates model
                            binding.actionFavorite.setSelected(true); //Updated visual
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Request to like failed: " + throwable);
                        }
                    });
                }
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