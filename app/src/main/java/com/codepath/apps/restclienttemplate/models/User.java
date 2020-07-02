package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    private String name;
    private String username;
    private String profileImageUrl;
    private String profileSmallImageUrl;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.username = "@" + jsonObject.getString("screen_name");

        user.profileSmallImageUrl = jsonObject.getString("profile_image_url_https");
            //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png'

        //Replaces 'normal' with 'bigger' (In order to get higher definition images).

        String root = user.profileSmallImageUrl.substring(0, user.profileSmallImageUrl.length()-10);
            //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_'

        String extension = user.profileSmallImageUrl.substring(user.profileSmallImageUrl.length()-4);
            //Ex: '.png'

        user.profileImageUrl = root + "bigger" + extension;
            //Ex: 'http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_bigger.png'

        return user;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileSmallImageUrl() {
        return profileSmallImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
