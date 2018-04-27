package com.applink.ford.hellosdlandroid.providers;

/**
 * Created by agurz on 4/27/18.
 */

import android.util.Log;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterProvider {

    Twitter api;

    public void init() {
        Configuration conf = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey("8J4m2aaF3OUWyGxtCJW5JFpn1")
                .setOAuthConsumerSecret("ZNCGtt82FhIh5zFdszyGB4MlljpJrSTtRyIpjwyCMvUBjzBvS2")
                .setOAuthAccessToken("184120521-fxReyv6s285HRbh2KppUeDWxMr2qcm6oSfnv2b8J")
                .setOAuthAccessTokenSecret("YzNOvMso9zMSPxIzkwrRpM18Gd8BRseyXl8GVYjR61Wzp")
                .build();

        api = new TwitterFactory(conf).getInstance();
    }

    public void startNotificationsListener() {
        try {
            QueryResult result = api.search(new Query("source:twitter4j baeldung"));
            Log.d("ok", result.toString());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}
