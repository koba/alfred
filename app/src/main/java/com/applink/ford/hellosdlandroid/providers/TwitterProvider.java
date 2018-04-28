package com.applink.ford.hellosdlandroid.providers;

/**
 * Created by agurz on 4/27/18.
 */

import com.applink.ford.hellosdlandroid.Alfred;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Consumer;
import twitter4j.DirectMessage;
import twitter4j.MediaEntity;
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

    private final String NAME = "Fernando";

    private Configuration conf;
    private Twitter twitter;

    private long lastTweetId = 0;

    // events consumers
    private Consumer<String> notificationReceivedConsumer;
    private Consumer<String> mediaReceivedConsumer;

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
                triggerNotificationReceivedConsumer(makeTweeterUsernamePronounceable(source.getScreenName()) + " te está siguiendo");
            }

            @Override
            public void onUnfollow(User source, User unfollowedUser) {
                triggerNotificationReceivedConsumer(makeTweeterUsernamePronounceable(source.getScreenName()) + " ha dejado de seguirte");
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
                lastTweetId = status.getId();
                triggerNotificationReceivedConsumer(makeTweeterUsernamePronounceable(status.getUser().getScreenName()) + " ha twitteado: " + makeTweetPronounceable(status.getText()));

                MediaEntity[] media = status.getMediaEntities();
                if (media.length > 0) {
                    for (MediaEntity mediaEntity : media) {
                        String mediaUrl = mediaEntity.getMediaURL();
                        triggerMediaReceivedConsumer(mediaUrl);
                    }
                }
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

    public void injectCommands(final Alfred alfred) {
        alfred.registerCommand("retweet", new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (lastTweetId > 0) {
                    twitter.retweetStatus(lastTweetId);
                    alfred.speak("Listo");
                }
                else {
                    alfred.speak("Lo siento " + NAME + ", no has recibido ningún tweet ultimamente");
                }
            }
        });
    }

    private String makeTweetPronounceable(String message) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(message);
        int i = 0;
        while (m.find()) {
            message = message.replaceAll(m.group(i),"").trim();
            i++;
        }
        return message;
    }

    private String makeTweeterUsernamePronounceable(String username) {
        return username.replace(".", " punto ");
    }

    private void triggerNotificationReceivedConsumer(String message) {
        try {
            if (notificationReceivedConsumer != null) {
                notificationReceivedConsumer.accept(message);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void triggerMediaReceivedConsumer(String mediaUrl) {
        try {
            if (mediaReceivedConsumer != null) {
                mediaReceivedConsumer.accept(mediaUrl);
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

    public void onMediaReceived(Consumer<String> consumer) {
        mediaReceivedConsumer = consumer;
    }
}
