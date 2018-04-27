package com.applink.ford.hellosdlandroid.providers;

/**
 * Created by agurz on 4/27/18.
 */

import com.applink.ford.hellosdlandroid.Alfred;

import io.reactivex.functions.Consumer;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterProvider {

    Configuration conf;
    Twitter twitter;

    // events consumers
    Consumer<String> notificationReceivedConsumer;

    public TwitterProvider() {
        conf = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey("w3fhedEf5kiJXTCi6w6pEtIwI")
                .setOAuthConsumerSecret("8W4eQBecIcrN5mwMdhsg2DZTQ1Rbrpgr8VHsj6g8II74CLKabL")
                .setOAuthAccessToken("1317765637-KKWGbvxHProtW82AxCgj60KWVl7YDbTBlaLAweJ")
                .setOAuthAccessTokenSecret("MoejDkxhUZFb4psZtg5azDF6Ae0Y5iNQNvQlHBzH87tRS")
                .build();

        twitter = new TwitterFactory(conf).getInstance();

        initUserStreamListeners();
    }

    private void initUserStreamListeners() {
        TwitterStream stream = new TwitterStreamFactory(conf).getInstance();

        stream.addListener(new UserStreamListener() {
            @Override
            public void onDeletionNotice(long directMessageId, long userId) {

            }

            @Override
            public void onFriendList(long[] friendIds) {

            }

            @Override
            public void onFavorite(User source, User target, Status favoritedStatus) {

            }

            @Override
            public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

            }

            @Override
            public void onFollow(User source, User followedUser) {
                triggerNotificationConsumer(makeTweeterUsernamePronounceable(source.getScreenName()) + " te está siguiendo");
            }

            @Override
            public void onUnfollow(User source, User unfollowedUser) {
                triggerNotificationConsumer(makeTweeterUsernamePronounceable(source.getScreenName()) + " ha dejado de seguirte");
            }

            @Override
            public void onDirectMessage(DirectMessage directMessage) {

            }

            @Override
            public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

            }

            @Override
            public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

            }

            @Override
            public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

            }

            @Override
            public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

            }

            @Override
            public void onUserListCreation(User listOwner, UserList list) {

            }

            @Override
            public void onUserListUpdate(User listOwner, UserList list) {

            }

            @Override
            public void onUserListDeletion(User listOwner, UserList list) {

            }

            @Override
            public void onUserProfileUpdate(User updatedUser) {

            }

            @Override
            public void onBlock(User source, User blockedUser) {

            }

            @Override
            public void onUnblock(User source, User unblockedUser) {

            }

            @Override
            public void onStatus(Status status) {
                triggerNotificationConsumer(makeTweeterUsernamePronounceable(status.getUser().getScreenName()) + " ha twitteado: " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception ex) {

            }
        });

        stream.user();
    }

    public void injectCommands(Alfred alfred) {
        alfred.registerCommand("tweet", new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

            }
        });
    }

    private String makeTweeterUsernamePronounceable(String username) {
        return username.replace(".", " punto ");
    }

    private void triggerNotificationConsumer(String message) {
        try {
            if (notificationReceivedConsumer != null) {
                notificationReceivedConsumer.accept(message);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // event consumers

    public void onNotificationReceived(Consumer<String> consumer) {
        notificationReceivedConsumer = consumer;
    }
}
