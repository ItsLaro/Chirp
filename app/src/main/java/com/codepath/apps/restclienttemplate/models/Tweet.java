package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    //package private or public fields (required by Parceler)
    @ColumnInfo @PrimaryKey
    long id;
    @ColumnInfo
    String body;
    @ColumnInfo
    String createdAt;

    @Ignore
    public User user;
    @ColumnInfo
    long userId;


    @ColumnInfo
    int retweetCount;
    @ColumnInfo
    int favoriteCount;
    @ColumnInfo
    boolean isFavorited;
    @ColumnInfo
    boolean isRetweet;


    @Transient @Ignore
    JSONObject entities;

    @Ignore
    List<String> mediaUrls = new ArrayList<>();

    public Tweet(){
        //empty constructor needed by the Parceler library
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void toggleFavorited(){
        isFavorited = isFavorited ? false : true;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public void toggleRetweeted(){
        isRetweet = isRetweet ? false : true;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public String getMediaUrl(int index) {
        String mediaURL = mediaUrls.get(index);
        return mediaURL;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public void setRetweet(boolean retweet) {
        isRetweet = retweet;
    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.id = jsonObject.getLong("id");

        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");

        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.userId = tweet.user.getId();

        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweet = jsonObject.getBoolean("retweeted");

        tweet.entities = jsonObject.getJSONObject("entities");
        if(tweet.entities.has("media")){

            JSONArray mediaEntities = tweet.entities.getJSONArray("media");

            /*Only the first photo is listed in the entities section.
            TODO: How to acquire the rest? Bonus Feature: show all embedded media*/
            for(int i = 0; i < mediaEntities.length(); i++){
                tweet.mediaUrls.add(mediaEntities.getJSONObject(i).getString("media_url_https"));
            }

            Log.d("TweetMedia", "Media! Found: " + tweet.mediaUrls.toString());

        }else{

            Log.d("TweetMedia", "No media on tweet");

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
}

