package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utilities.DateUtility;

import java.util.List;

public class TweetsAdapter extends  RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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

        public void bind(Tweet tweet) {


            String timeStamp = DateUtility.getRelativeTimeAgo(tweet.getCreatedAt());
            Log.d(TAG, "New tweet's timeStamp " + timeStamp);
            if(timeStamp.equals("In0") || timeStamp.equals("In1")){
                //Condition handles case when tweet is manually inserted (without refreshing)
                timeStamp = "Just now";
            }

            binding.timestamp.setText(timeStamp);
            binding.displayName.setText(tweet.user.getScreenName());
            binding.tweetBody.setText(tweet.getBody());

            Glide.with(context)
                    .load(tweet.user.getProfileImageUrl())
                    .into(binding.profileImage);

        }
    }
}
