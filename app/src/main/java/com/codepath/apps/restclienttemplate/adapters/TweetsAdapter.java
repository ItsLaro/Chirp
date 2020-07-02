package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.activities.UserProfileActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utilities.DateUtility;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends  RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final String TAG = "TweetsAdapter";

    Context context;
    TwitterClient twitterClient;

    List<Tweet> tweets;
    OnClickListener clickListener;

    public interface  OnClickListener{
        void onItemClick(int position);
    }

    public TweetsAdapter(Context context, List<Tweet> tweets, OnClickListener clickListener) {
        this.context = context;
        this.tweets = tweets;
        this.clickListener = clickListener;
        twitterClient = TwitterApp.getRestClient(context);;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTweetBinding binding = ItemTweetBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ItemTweetBinding binding;

        public ViewHolder(@NonNull ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Tweet tweet) {

            String timeStamp = DateUtility.getRelativeTimeAgo(tweet.getCreatedAt());
            Log.d(TAG, "New tweet's timeStamp " + timeStamp);

            if(timeStamp.equals("In0") || timeStamp.equals("In1")){
                //Condition handles case when tweet is manually inserted (without refreshing)
                timeStamp = "Just now";
            }

            binding.timestamp.setText(timeStamp);
            binding.displayName.setText(tweet.user.getName());
            binding.userHandle.setText(tweet.user.getUsername());
            binding.tweetBody.setText(tweet.getBody());

            if(tweet.isFavorited()){
                binding.actionFavorite.setSelected(true);
            }
            else{
                binding.actionFavorite.setSelected(false);
            }

            if(tweet.isRetweet()){
                binding.actionRT.setSelected(true);
            }
            else{
                binding.actionRT.setSelected(false);
            }

            //Loading profile picture
            Glide.with(context)
                    .load(tweet.user.getProfileImageUrl())
                    .transform(new CircleCrop())
                    .into(binding.profileImage);

            //If there's an image
            if(tweet.getMediaUrls().size() > 0){
                //Set media
                Glide.with(context)
                        .load(tweet.getMediaUrl(0))
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

            //Tweet item itself
            binding.tweetItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Sets listener on actual tweet item -> get position
                    and initiate detail-activity from timeline*/
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

            //Reply button
            binding.actionComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Reply clicked on tweet: " + tweet.getBody());
                    //Launches fragment
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();

                    //param is 'true' for a reply tweet
                    ComposeFragment editNameDialogFragment = ComposeFragment.newInstance(tweet);
                    editNameDialogFragment.show(fm, "Compose");

                    Log.d(TAG, "Compose initiated.");

                }
            });

            //RT button
            binding.actionRT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "RT clicked on tweet: " + tweet.getBody());

                    if(tweet.isRetweet()){
                        //Pressing favorite on RTed tweet will trigger POST request to unRTed it
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
                        //Pressing favorite on unRT tweet will trigger POST request to RT it
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

            //Favorite button
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

            //Share button
            binding.actionShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Implement sharing (integration with other apps)
                    Log.d(TAG, "Share clicked on tweet: " + tweet.getBody());
                }
            });

            //Profile Picture button
            binding.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Profile clicked: " + tweet.user.getUsername());

                    Intent userProfileIntent = new Intent(context, UserProfileActivity.class);

                    //Passing data to the intent
                    userProfileIntent.putExtra("user_object", Parcels.wrap(tweet.user));

                    context.startActivity(userProfileIntent);
                }
            });
        }
    }
}
