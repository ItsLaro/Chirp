package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends DialogFragment{

    private static final String TAG = "ComposeFragment"; //Logging purposes
    private static final int MAX_TWEET_LENGTH = 140;

    //vars
    TwitterClient twitterClient;
    FragmentComposeBinding binding;
    String tweetContent;
    private Tweet replyingToTweet;

    //Interface to be used as callback to get tweet content from fragment
    public interface TweetSubmitListener{
        void sendInput(Tweet submittedTweet);
    }
    public TweetSubmitListener tweetSubmitListener;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment in order to compose and POST a regular tweet
     * @return A new instance of fragment ComposeFragment.
     */
    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This factory method creates a new instance of
     * this fragment in order to reply to another tweet
     * @param inReplyToTweet Tweet to reply to
     * @return A new instance of fragment ComposeFragment.
     */
    public static ComposeFragment newInstance(Tweet inReplyToTweet) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.replyingToTweet = inReplyToTweet;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            twitterClient = TwitterApp.getRestClient(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View binding
        binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);

        //layout of fragment stored in view's root
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show soft keyboard automatically and request focus to field
        binding.composeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Check if this is a reply tweet
        if(replyingToTweet instanceof Tweet){
            Log.d(TAG, "Replying to: " + replyingToTweet);
            //Set views to reflect tweet is being composed as a reply
            binding.composeTitle.setText("Replying to " + replyingToTweet.user.getUsername());
            binding.composeTweet.setHint(R.string.reply_hint);
        }
        else{
            Log.d(TAG, "Tweeting!");
        }

        //Listener on 'Tweet'/Submit button for taps
        binding.tweetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //User's inputted aspiring-tweet
                tweetContent = binding.composeTweet.getText().toString();

                if(tweetContent.isEmpty()){
                    //Displays floating label error
                    binding.composeTitleLayout.setError(getString(R.string.empty_tweet_error));
                    binding.composeTitleLayout.setErrorEnabled(true);
                }
                else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    //Displays floating label error
                    binding.composeTitleLayout.setError(getString(R.string.long_tweet_error));
                    binding.composeTitleLayout.setErrorEnabled(true);
                }
                else {
                    //Check if this is a reply tweet
                    if(replyingToTweet instanceof Tweet){

                        String replyWithMention = replyingToTweet.user.getUsername() + " " + tweetContent;

                        twitterClient.postReply(replyWithMention, replyingToTweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.d(TAG, "Success: POSTED " + json.toString());
                                Toast.makeText(getActivity(), "Tweet is posted!", Toast.LENGTH_SHORT).show();

                                try {
                                    Tweet newTweet = Tweet.fromJSON(json.jsonObject);
                                    Log.d(TAG, "Published Tweet: " + newTweet.getBody());

                                    //interface captures the input & sends back to the activity
                                    tweetSubmitListener.sendInput(newTweet);

                                } catch (JSONException e) {

                                    Log.e(TAG, "Error parsing JSON: " + e);

                                }
                                getDialog().dismiss(); //Dismisses tweet-composing fragment
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                                Log.d(TAG, "Failure: " + throwable.toString());

                                Toast.makeText(getActivity(), "Unable to reach Twitter", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        //It's a regular tweet
                        twitterClient.postTweet(tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.d(TAG, "Success: POSTED " + json.toString());
                                Toast.makeText(getActivity(), "Tweet is posted!", Toast.LENGTH_SHORT).show();

                                try {
                                    Tweet newTweet = Tweet.fromJSON(json.jsonObject);
                                    Log.d(TAG, "Published Tweet: " + newTweet.getBody());

                                    //interface captures the input & sends back to the activity
                                    tweetSubmitListener.sendInput(newTweet);

                                } catch (JSONException e) {

                                    Log.e(TAG, "Error parsing JSON: " + e);

                                }
                                getDialog().dismiss(); //Dismisses tweet-composing fragment
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                                Log.d(TAG, "Failure: " + throwable.toString());

                                Toast.makeText(getActivity(), "Unable to reach Twitter", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        tweetSubmitListener = (TweetSubmitListener) getActivity();
    }

}