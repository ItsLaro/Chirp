package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
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
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity implements ComposeFragment.TweetSubmitListener {

    public static final String TAG = "TweetDetailsActivity"; //logging purposes

    TwitterClient twitterClient;
    private ActivityTweetDetailsBinding binding;

    private Tweet detailedTweet;

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
        detailedTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet_object"));
        Log.d(TAG, "Loaded details for tweet: " + detailedTweet.getBody());


        //Setting views to passed Tweet data
        binding.displayName.setText(detailedTweet.user.getName());
        binding.userHandle.setText(detailedTweet.user.getUsername());
        binding.tweetBody.setText(detailedTweet.getBody());
        binding.timestamp.setText(detailedTweet.getCreatedAt());
        binding.numberRT.setText(Integer.toString(detailedTweet.getRetweetCount()));
        binding.numberLikes.setText(Integer.toString(detailedTweet.getFavoriteCount()));


        if(detailedTweet.isFavorited()){
            binding.actionFavorite.setSelected(true);
        }

        if(detailedTweet.isRetweet()){
            binding.actionRT.setSelected(true);
        }

        //Profile picture
        Glide.with(this)
                .load(detailedTweet.user.getProfileImageUrl())
                .transform(new CircleCrop())
                .into(binding.profileImage);

        //If there's an image
        if(detailedTweet.getMediaUrls().size() > 0){
            //Set media
            Glide.with(this)
                    .load(detailedTweet.getMediaUrl(0))
                    .transform(new CenterCrop(), new RoundedCorners(30))
                    .into(binding.tweetMedia);

            //Recovers visibility on a recycled item after it had been toggled off
            binding.tweetMedia.setVisibility(View.VISIBLE);
        }
        else{
            //No image? Hide the view.
            binding.tweetMedia.setVisibility(View.GONE);
        }

        //LISTENERS

        //Click listener on Reply button
        binding.actionComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Reply clicked on tweet: " + detailedTweet.getBody());
                //Launches fragment
                FragmentManager fm = getSupportFragmentManager();

                //param is 'true' for a reply tweet
                ComposeFragment editNameDialogFragment = ComposeFragment.newInstance(detailedTweet);
                editNameDialogFragment.show(fm, "Compose");

                Log.d(TAG, "Compose initiated.");
            }
        });

        //Click listener on 'RT' button
        binding.actionRT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "RT clicked on tweet: " + detailedTweet.getBody());

                if(detailedTweet.isRetweet()){
                    //Pressing favorite on RTed tweet will trigger POST request to unRTed it
                    twitterClient.postUnretweet(detailedTweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to unRT succeeded: " + json.toString());
                            detailedTweet.toggleRetweeted(); //Updates model
                            binding.actionRT.setSelected(false); //Updates visual
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Request to unRT failed: " + throwable);

                        }
                    });
                }
                else{
                    //Pressing favorite on unRT tweet will trigger POST request to RT it
                    twitterClient.postRetweet(detailedTweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to RT succeeded: " + json.toString());
                            detailedTweet.toggleRetweeted(); //Updates model
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
                Log.d(TAG, "Favorite clicked on tweet: " + detailedTweet.getBody());

                if(detailedTweet.isFavorited()){
                    //Pressing favorite on favorited tweet will trigger POST request to unlike it
                    twitterClient.postUnlike(detailedTweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to unlike succeeded: " + json.toString());
                            detailedTweet.toggleFavorited(); //Updates model
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
                    twitterClient.postLike(detailedTweet.getId(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Request to like succeeded: " + json.toString());
                            detailedTweet.toggleFavorited(); //Updates model
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
                //TODO: Implement sharing (integration with other apps)
                Log.d(TAG, "Share clicked on tweet: " + detailedTweet.getBody());
            }
        });

        //Profile Picture button
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Profile clicked: " + detailedTweet.user.getUsername());

                Intent userProfileIntent = new Intent(TweetDetailsActivity.this, UserProfileActivity.class);

                //Passing data to the intent
                userProfileIntent.putExtra("user_object", Parcels.wrap(detailedTweet.user));

                TweetDetailsActivity.this.startActivity(userProfileIntent);
            }
        });
    }

    @Override
    public void sendInput(Tweet postedTweet) {
        Log.d(TAG, "Acquired tweet: " + postedTweet.getBody());
    }

}