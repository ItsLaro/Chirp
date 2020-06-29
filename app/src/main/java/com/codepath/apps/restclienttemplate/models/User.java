package com.codepath.apps.restclienttemplate.models;

        import org.json.JSONException;
        import org.json.JSONObject;

public class User {

    private String name;
    private String screenName;
    private String profileImageUrl;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");

        return user;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
