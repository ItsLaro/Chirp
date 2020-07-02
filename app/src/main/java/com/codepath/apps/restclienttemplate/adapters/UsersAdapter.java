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
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.activities.UserProfileActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.databinding.ItemUserBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utilities.DateUtility;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private static final String TAG = "TweetsAdapter";

    Context context;
    TwitterClient twitterClient;

    List<User> users;
    OnClickListener clickListener;

    public interface  OnClickListener{
        void onItemClick(int position);
    }

    public UsersAdapter(Context context, List<User> users, OnClickListener clickListener) {
        this.context = context;
        this.users = users;
        this.clickListener = clickListener;
        twitterClient = TwitterApp.getRestClient(context);;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ItemUserBinding binding;

        public ViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final User user) {

            binding.displayName.setText(user.getName());
            binding.userHandle.setText(user.getUsername());
            binding.userBio.setText(user.getBio());

            //Loading profile picture
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.default_profilepic)
                    .into(binding.profileImage);

            //LISTENERS

            //User item itself
            binding.userContainer.setOnClickListener(new View.OnClickListener() {
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
