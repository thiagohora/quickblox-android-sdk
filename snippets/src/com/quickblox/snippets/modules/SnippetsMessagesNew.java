package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.helper.StringifyArrayList;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.internal.module.content.Consts;
import com.quickblox.module.messages.QBMessages;
import com.quickblox.module.messages.model.*;
import com.quickblox.snippets.AsyncSnippet;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vfite on 10.02.14.
 */
public class SnippetsMessagesNew extends Snippets{
    private static final String TAG = SnippetsMessages.class.getSimpleName();

    public SnippetsMessagesNew(Context context) {
        super(context);

        snippets.add(createPushTokenNewCallback);
        snippets.add(createPushTokenSynchronous);
        snippets.add(deletePushTokenNewCallback);

        snippets.add(createSubscriptionNewCallback);
        snippets.add(getSubscriptionsNewCallback);
        snippets.add(deleteSubscriptionNewCallback);

        snippets.add(createEventNewCallback);
        snippets.add(getEventWithIdNewCallback);
        snippets.add(getEventWithIdSynchronous);
        snippets.add(getEventsNewCallback);
        snippets.add(getEventsSynchronous);
        snippets.add(getPullEventNewCallback);
        snippets.add(updateEventNewCallback);
        snippets.add(deleteEventNewCallback);

        snippets.add(subscribeToPushNotificationsTaskNewCallback);
    }

    //
    ///////////////////////////////////////////// Push token /////////////////////////////////////////////
    //
    Snippet createPushTokenNewCallback = new Snippet("create push token") {
        @Override
        public void execute() {
            String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if(deviceId == null){
                deviceId = "UniversalDevice";
            }

            QBPushToken qbPushToken = new QBPushToken();
            qbPushToken.setEnvironment(QBEnvironment.DEVELOPMENT);
            qbPushToken.setDeviceUdid(deviceId);
            qbPushToken.setCis("2342hiyf2352959fg9af03fgfg0fahoo018273af");


            QBMessages.createPushToken(qbPushToken, new QBEntityCallbackImpl<QBPushToken>() {

                @Override
                public void onSuccess(QBPushToken pushToken, Bundle args) {
                    Log.i(TAG, ">>> PushToken: " + pushToken.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet createPushTokenSynchronous = new AsyncSnippet("create push token synchronous", context) {
        @Override
        public void executeAsync() {
            String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if(deviceId == null){
                deviceId = "UniversalDevice";
            }

            QBPushToken qbPushToken = new QBPushToken();
            qbPushToken.setEnvironment(QBEnvironment.DEVELOPMENT);
            qbPushToken.setDeviceUdid(deviceId);
            qbPushToken.setCis("2342hiyf2352959fg9af03fgfg0fahoo018273af");

            QBPushToken pushToken = null;
            try {
                pushToken = QBMessages.createPushToken(qbPushToken);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(pushToken != null){
                Log.i(TAG, ">>> PushToken: " + pushToken.toString());
            }
        }
    };



    Snippet deletePushTokenNewCallback = new Snippet("delete push token") {
        @Override
        public void execute() {
            QBMessages.deletePushToken(13998, new QBEntityCallbackImpl<Void>() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> push token successfully deleted");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };


    //
    ///////////////////////////////////////////// Subscription /////////////////////////////////////////////
    //
    Snippet createSubscriptionNewCallback = new Snippet("subscription fot events", "(push listener)") {
        @Override
        public void execute() {
            QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
            QBMessages.createSubscription(subscription, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                    Log.i(TAG, ">>> subscription created" + subscriptions.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getSubscriptionsNewCallback = new Snippet("get subscriptions") {
        @Override
        public void execute() {
            QBMessages.getSubscriptions(new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                    Log.i(TAG, ">>> subscription list" + subscriptions.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet deleteSubscriptionNewCallback = new Snippet("delete subscription") {
        @Override
        public void execute() {
            QBMessages.deleteSubscription(14824, new QBEntityCallbackImpl<Void>() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> subscription successfully deleted");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };


    //
    ///////////////////////////////////////////// Event /////////////////////////////////////////////
    //
    Snippet createEventNewCallback = new Snippet("create event (send push)") {
        @Override
        public void execute() {

            // recipient
            StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
            userIds.add(3055);
//            userIds.add(960);

            QBEvent event = new QBEvent();
            event.setUserIds(userIds);
            event.setName("Magic Push");
            event.setEnvironment(QBEnvironment.DEVELOPMENT);
            event.setNotificationType(QBNotificationType.PUSH);

            // generic push - will be delivered to all platforms (Android, iOS, WP, Blackberry..)
            event.setMessage("Gonna send Push Notification!");

            // Android based push
//            event.setPushType(QBPushType.GCM);
//            HashMap<String, String> data = new HashMap<String, String>();
//            data.put("data.message", "Hello");
//            data.put("data.type", "welcome message");
//            event.setMessage(data);

            QBMessages.createEvent(event, new QBEntityCallbackImpl<QBEvent>() {
                @Override
                public void onSuccess(QBEvent qbEvent, Bundle args) {
                    Log.i(TAG, ">>> new event: " + qbEvent.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getEventWithIdNewCallback = new Snippet("get event with id") {
        @Override
        public void execute() {
            QBMessages.getEvent(25245, new QBEntityCallbackImpl<QBEvent>() {

                @Override
                public void onSuccess(QBEvent qbEvent, Bundle args) {
                    Log.i(TAG, ">>> event: " + qbEvent.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet getEventWithIdSynchronous = new AsyncSnippet("get event with id", context) {
        public QBEvent event;

        @Override
        public void executeAsync() {
            try {
                event = QBMessages.getEvent(25245);
            } catch (QBResponseException e) {
                setException(e);
            }
        }

        @Override
        protected void postExecute() {
            super.postExecute();
            if(event != null){
                Log.i(TAG, ">>> event: " + event.toString());
            }
        }
    };


    Snippet getEventsNewCallback = new Snippet("get Events") {
        @Override
        public void execute() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(20, 1);
            QBMessages.getEvents(requestBuilder, new QBEntityCallbackImpl<ArrayList<QBEvent>>() {

                @Override
                public void onSuccess(ArrayList<QBEvent> events, Bundle args) {
                    Log.i(TAG, ">>> Events: " + events.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });

        }
    };

    Snippet getEventsSynchronous = new AsyncSnippet("get Events synchronous", context) {
        @Override
        public void executeAsync() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(20, 1);
            Bundle params = new Bundle();
            ArrayList<QBEvent> events = null;
            try {
                events = QBMessages.getEvents(requestBuilder, params);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(events != null){
                Log.i(TAG, ">>> Events: " + events.toString());
                Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
            }
        }

    };

    Snippet getPullEventNewCallback = new Snippet("get pull events") {
        @Override
        public void execute() {

            QBMessages.getPullEvents(new QBEntityCallbackImpl<ArrayList<QBEvent>>() {

                @Override
                public void onSuccess(ArrayList<QBEvent> events, Bundle args) {
                    Log.i(TAG, ">>>Pull Events: " + events.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet updateEventNewCallback = new Snippet("update event") {
        @Override
        public void execute() {
            QBEvent event = new QBEvent();
            event.setId(25245);
            event.setMessage("Gonna send Push Notification again!");
            event.setEnvironment(QBEnvironment.DEVELOPMENT);
            event.setPushType(QBPushType.GCM);
            event.setNotificationType(QBNotificationType.PUSH);

            QBMessages.updateEvent(event, new QBEntityCallbackImpl<QBEvent>() {

                @Override
                public void onSuccess(QBEvent qbEvent, Bundle args) {
                    Log.i(TAG, ">>> event: " + qbEvent.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet deleteEventNewCallback = new Snippet("delete event") {
        @Override
        public void execute() {
            QBMessages.deleteEvent(25245, new QBEntityCallbackImpl<Void>() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> event successfully deleted");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };


    //
    ///////////////////////////////////////////// Tasks /////////////////////////////////////////////
    //
    Snippet subscribeToPushNotificationsTaskNewCallback = new Snippet("TASK: Subscribe to push notifications") {
        @Override
        public void execute() {
            String registrationID = "2342hiyf2352959fg9af03fgfg0fahoo018273af";
            String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if(deviceId == null){
                deviceId = "UniversalDevice";
            }

            QBMessages.subscribeToPushNotificationsTask(registrationID, deviceId, QBEnvironment.PRODUCTION, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                    Log.i(TAG, ">>> subscription created" + subscriptions.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });

        }
    };

}
