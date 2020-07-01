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
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utilities.DateUtility;

import java.util.List;

public class TweetsAdapter extends  RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    OnClickListener clickListener;

    public interface  OnClickListener{
        void onItemClick(int position);
    }

    public TweetsAdapter(Context context, List<Tweet> tweets, OnClickListener clickListener) {
        this.context = context;
        this.tweets = tweets;
        this.clickListener = clickListener;
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
            binding.displayName.setText(tweet.user.getName());
            binding.userHandle.setText(tweet.user.getScreenName());
            binding.tweetBody.setText(tweet.getBody());

            if(tweet.isFavorited()){
                binding.actionFavorite.setSelected(true);
            }

            if(tweet.isRetweet()){
                binding.actionRT.setSelected(true);
            }

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

            //Listeners
            binding.tweetItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Sets listener on actual tweet item -> get position
                    and initiate detail-activity from timeline*/
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
