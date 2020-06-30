package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.facebook.stetho.inspector.jsonrpc.JsonRpcException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    private String body;
    private String createdAt;
    public User user;

    private JSONObject entities;
    private JSONArray mediaEntities;
    private List<String> mediaUrls = new ArrayList<>();

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        tweet.entities = jsonObject.getJSONObject("entities");
        if(tweet.entities.has("media")){

            tweet.mediaEntities = tweet.entities.getJSONArray("media");

            //Only the first photo is listed in the entities section. TODO: How to acquire the rest?
            for(int i = 0; i < tweet.mediaEntities.length(); i++){
                tweet.mediaUrls.add(tweet.mediaEntities.getJSONObject(i).getString("media_url_https"));
            }

            Log.i("TweetMedia", "Media! Found: " + tweet.mediaUrls.toString());

        }else{
            Log.i("TweetMedia", "No media on tweet");

        }

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) throws JSONException{
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJSON(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public String getMediaUrl(int index) {
        String mediaURL = mediaUrls.get(index);
        return mediaURL;
    }
}

