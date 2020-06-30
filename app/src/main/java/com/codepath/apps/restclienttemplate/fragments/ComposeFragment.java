package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

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
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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


        //Listener on 'Tweet'/Submit button for taps
        binding.tweetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                tweetContent = binding.composeTweet.getText().toString();

                if(tweetContent.isEmpty()){
                    //Displays floating label error
                    binding.composeTitleLayout.setError(getString(R.string.empty_tweet_error));
                    binding.composeTitleLayout.setErrorEnabled(true);
                }
                else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    //Displays floating label error
                    binding.composeTitleLayout.setError(getString(R.string.long_tweet_error));
                    binding.composeTitleLayout.setErrorEnabled(true);                }
                else {
                    Toast.makeText(getActivity(), "Tweet is good!", Toast.LENGTH_SHORT).show();
                    twitterClient.postTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {

                            Log.d(TAG, "Success: POSTED " + json.toString());

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
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        tweetSubmitListener = (TweetSubmitListener) getActivity();
    }

}