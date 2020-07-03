package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel @Entity
public class User {

    @ColumnInfo @PrimaryKey
    long id;
    @ColumnInfo
    String name;
    @ColumnInfo
    String username;
    @ColumnInfo
    String profileImageUrl;
    @ColumnInfo
    String profileBanner;
    @ColumnInfo
    String bio;
    @ColumnInfo
    int followingCount;
    @ColumnInfo
    int followersCount;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBanner() {
        return profileBanner;
    }

    public String getBio() {
        return bio;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.username = "@" + jsonObject.getString("screen_name");

        Log.d("USER", user.username);

        String profileThumbnailUrl = jsonObject.getString("profile_image_url_https");
        //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png'
        //Replaces 'normal' with 'bigger' (In order to get higher definition images).
        String root = profileThumbnailUrl.substring(0, profileThumbnailUrl.length()-11);
        //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3'
        String extension = profileThumbnailUrl.substring(profileThumbnailUrl.length()-4);
        //Ex: '.png'
        user.profileImageUrl = root + extension; // Full-size
        //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3.png'

        try {
            user.profileBanner = jsonObject.getString("profile_banner_url");
        }
        catch (Exception e){
            user.profileBanner = "";
        }

        user.bio = jsonObject.getString("description");
        user.followingCount = jsonObject.getInt("friends_count");
        user.followersCount = jsonObject.getInt("followers_count");

        return user;
    }

    public static List<User> fromJSONArray(JSONArray jsonArray) throws JSONException{
        List<User> users = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            users.add(fromJSON(jsonArray.getJSONObject(i)));
        }
        return users;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++){
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }

}
