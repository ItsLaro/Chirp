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
import android.os.Parcelable;
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
import com.codepath.apps.restclienttemplate.utilities.EndlessRecyclerViewScrollListener;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.TweetSubmitListener {

    private static final String TAG = "TimelineActivity";

    private Menu mainMenu;
    private ActivityTimelineBinding binding;

    private TwitterClient client;
    private TweetsAdapter tweetsAdapter;
    private List<Tweet> tweets = new ArrayList<>();

    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View timelineView = binding.getRoot();
        setContentView(timelineView);

        //Client
        client = TwitterApp.getRestClient(this);

        //Adapter
        TweetsAdapter.OnClickListener onClickListener = new TweetsAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent tweetDetailsIntent = new Intent(TimelineActivity.this, TweetDetailsActivity.class);

                //Passing data to the intent
                tweetDetailsIntent.putExtra("tweet_position", position);
                tweetDetailsIntent.putExtra("tweet_object", Parcels.wrap(tweets.get(position)));

                startActivity(tweetDetailsIntent);
            }
        };

        tweetsAdapter = new TweetsAdapter(this, tweets, onClickListener);
        layoutManager = new LinearLayoutManager(this);
        binding.timelineRecycleView.setLayoutManager(layoutManager);
        binding.timelineRecycleView.setAdapter(tweetsAdapter);

        populateHomeTimeline();

        //Swipe Refresh Listener
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

        //Infinite scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "User tried to load more from page: " + page);
                loadMore();
            }
        };

        //ScrollListener attached to RecyclerView
        binding.timelineRecycleView.addOnScrollListener(scrollListener);

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

    private void loadMore() {

        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Last ID: " + tweets.get(tweets.size()-1).getId());
                Log.d(TAG, statusCode + ", Success: " + json.toString());
                JSONArray jsonArray = json.jsonArray;

                try {
                    tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }

        }, tweets.get(tweets.size()-1).getId());

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

                //param is 'false' for a non-reply tweet
                ComposeFragment editNameDialogFragment = ComposeFragment.newInstance();
                editNameDialogFragment.show(fm, "Compose");

                Log.d(TAG, "Compose initiated.");
                return true;
        }
        return true;
    }

    @Override
    public void sendInput(Tweet postedTweet) {

        Log.d(TAG, "Acquired tweet: " + postedTweet.getBody());

        tweets.add(0, postedTweet);
        tweetsAdapter.notifyItemInserted(0);
        binding.timelineRecycleView.smoothScrollToPosition(0);

    }
}