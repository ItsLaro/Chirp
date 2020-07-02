package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    private String name;
    private String username;
    private String profileImageUrl;
    private String profileBanner;
    private String bio;
    private int followingCount;
    private int followersCount;

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

}
