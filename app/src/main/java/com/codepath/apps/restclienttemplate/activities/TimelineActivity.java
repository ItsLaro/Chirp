package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.TweetSubmitListener {

    public static final String TAG = "TimelineActivity";

    Menu mainMenu;
    TwitterClient client;
    TweetsAdapter tweetsAdapter;
    List<Tweet> tweets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View timelineView = binding.getRoot();
        setContentView(timelineView);

        client = TwitterApp.getRestClient(this);
        tweetsAdapter = new TweetsAdapter(this, tweets);

        binding.timelineRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.timelineRecycleView.setAdapter(tweetsAdapter);

        populateHomeTimeline();


        binding.swipeRefreshContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_dark);

        binding.swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refresh: Fetching fresh data!");

                //Clears and repopulates
                populateHomeTimeline();

                //Turn off the reload signal
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                Log.d(TAG, statusCode + ", Success: " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        mainMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /**
         * Listener for toolbar options
         */

        switch (item.getItemId()) {

            case R.id.compose_option:

                //Launches fragment
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment editNameDialogFragment = ComposeFragment.newInstance("Some Title", "Nothing");
                editNameDialogFragment.show(fm, "Compose");
                Log.d(TAG, "Compose initiated.");
                return true;
        }
        return true;
    }

    @Override
    public void sendInput(String input) {

        Log.d(TAG, "Acquired tweet: " + input);

    }
}