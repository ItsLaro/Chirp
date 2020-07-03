package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.adapters.UsersAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserFollowingListBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utilities.EndlessRecyclerViewScrollListener;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class UserFollowingListActivity extends AppCompatActivity {

    private static String TAG = "UserFollowingListActivity";

    private ActivityUserFollowingListBinding binding;

    private TwitterClient client;
    private UsersAdapter usersAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;

    private User selectedUser;
    private List<User> users = new ArrayList<>();
    private int lastVisitedTweetPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.following_title);

        //Setting up View Binds
        binding = ActivityUserFollowingListBinding.inflate(getLayoutInflater());
        View userListView = binding.getRoot();
        setContentView(userListView);

        //Client
        client = TwitterApp.getRestClient(this);

        //Getting parcel from last Activity
        selectedUser = Parcels.unwrap(getIntent().getParcelableExtra("user_object"));
        Log.d(TAG, "Loading following for user: " + selectedUser.getUsername());

        //Adapter
        UsersAdapter.OnClickListener onClickListener = new UsersAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent UserFollowingListIntent = new Intent(UserFollowingListActivity.this,
                        UserProfileActivity.class);

                /* Storing position so we can refresh recycle view
                and smooth scroll back to where we were */
                lastVisitedTweetPosition = position;

                //Passing data to the intent
                UserFollowingListIntent.putExtra("user_object", Parcels.wrap(users.get(position)));

                startActivity(UserFollowingListIntent);
            }
        };

        usersAdapter = new UsersAdapter(this, users, onClickListener);
        layoutManager = new LinearLayoutManager(this);
        binding.followingRecycleView.setLayoutManager(layoutManager);
        binding.followingRecycleView.setAdapter(usersAdapter);

        populateFollowingList();

    }

    private void populateFollowingList() {
        client.getFollowing(selectedUser.getUsername(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                Log.d(TAG, statusCode + ", Success: " + json.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray usersJSONArray = jsonObject.getJSONArray("users");
                    Log.d(TAG, "result: " + usersJSONArray);
                    usersAdapter.clear();
                    usersAdapter.addAll(User.fromJSONArray(usersJSONArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.d(TAG, statusCode + ", Failure:" + response + "; Error: " + e);
            }
        });
    }
}