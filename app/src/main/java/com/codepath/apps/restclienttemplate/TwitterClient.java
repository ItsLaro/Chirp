package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/**
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	// DEFINE METHODS for different API endpoints here

	//GET
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("since_id", "1");
		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}

	public void getProfileTimeline(String username, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", "200");
		params.put("since_id", "1");
		params.put("screen_name", username);
		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}

	public void getNextPageOfTweets(JsonHttpResponseHandler handler, long maxId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", "100");
		params.put("max_id", maxId - 1); //Subtracting an arbitrary number since we want to exclude the same id
		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}

	public void getFollowing(String username, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");

		RequestParams params = new RequestParams();
		params.put("count", "200");
		params.put("screen_name", username);
		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}

	public void getFollowers(String username, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");

		RequestParams params = new RequestParams();
		params.put("count", "200");
		params.put("screen_name", username);
		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}

	//POST
	public void postTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", tweetContent);

		client.post(apiUrl, params, "", handler);
	}

	public void postReply(String tweetContent, long tweetID, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		//'status' content must include @username of the author of the tweet in 'in_reply_to_status_id'
		params.put("status", tweetContent);
		params.put("in_reply_to_status_id", tweetID);

		client.post(apiUrl, params, "", handler);
	}

	public void postLike(long tweetID, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");

		RequestParams params = new RequestParams();
		params.put("id", tweetID);

		client.post(apiUrl, params, "", handler);
	}

	public void postUnlike(long tweetID, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");

		RequestParams params = new RequestParams();
		params.put("id", tweetID);

		client.post(apiUrl, params, "", handler);
	}

	public void postRetweet(long tweetID, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", tweetID));

		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();

		client.post(apiUrl, params, "", handler);
	}

	public void postUnretweet(Long tweetID, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl(String.format("statuses/unretweet/%d.json", tweetID));

		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();

		client.post(apiUrl, params, "", handler);
	}

}
