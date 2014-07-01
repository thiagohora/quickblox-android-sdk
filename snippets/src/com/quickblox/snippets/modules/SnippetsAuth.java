package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBProvider;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.snippets.AsyncSnippet;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.util.List;

/**
 * Created by vfite on 22.01.14.
 */
public class SnippetsAuth extends Snippets{

    private static final String TAG = SnippetsAuth.class.getSimpleName();

    public SnippetsAuth(Context context) {
        super(context);

        snippets.add(createSession);
        snippets.add(createSessionSynchronous);
        //
        snippets.add(createSessionWithUser);
        snippets.add(createSessionWithUserSynchronous);
        //
        snippets.add(createSessionWithSocialProvider);
        snippets.add(createSessionWithSocialProviderSynchronous);
        //
        snippets.add(destroySession);
        snippets.add(destroySessionSynchronous);
    }


    //
    /////////////////////////////////// Create session /////////////////////////////////////////////
    //


    Snippet createSession = new Snippet("create session") {
        @Override
        public void execute() {
            QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {

                @Override
                public void onSuccess(QBSession session, Bundle params) {
                    super.onSuccess(session, params);
                    Log.i(TAG, "session created, token = " + session.getToken());
                }

                @Override
                public void onError(List<String> errors) {

                }
            });
        }
    };

    Snippet createSessionSynchronous = new AsyncSnippet("create session (synchronous)", context) {
        @Override
        public void executeAsync() {
            QBSession session = null;
            try {
                session = QBAuth.createSession();
            } catch (QBResponseException e) {
                setException(e);
            }
            if(session != null){
                Log.i(TAG, "session created, token = " + session.getToken());
            }
        }
    };


    //
    /////////////////////////////// Create session with user ///////////////////////////////////////
    //


    Snippet createSessionWithUser = new Snippet("create session", "with user") {
        @Override
        public void execute() {

            QBAuth.createSession(new QBUser("bobbobbob", "bobbobbob"), new QBEntityCallbackImpl<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle args) {
                    super.onSuccess(session, args);
                    Log.i(TAG, "session created, token = " + session.getToken());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet createSessionWithUserSynchronous = new AsyncSnippet("create session (synchronous)", "with user", context) {
        @Override
        public void executeAsync() {
            QBSession session = null;
            try {
                QBUser user = new QBUser("bobbobbob", "bobbobbob");
                session = QBAuth.createSession(user, (Bundle)null);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(session != null){
                Log.i(TAG, "session created, token = " + session.getToken());
            }
        }
    };


    //
    ////////////////////////// Create session with social provider /////////////////////////////////
    //


    Snippet createSessionWithSocialProvider = new Snippet("create session", "with social provider") {
        @Override
        public void execute() {

            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBAuth.createSessionUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, new QBEntityCallbackImpl<QBSession>() {

                @Override
                public void onSuccess(QBSession session,  Bundle args) {
                    Log.i(TAG, "session created, token = "+session.getToken());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet createSessionWithSocialProviderSynchronous = new AsyncSnippet("create session (synchronous)", "with social provider", context) {
        @Override
        public void executeAsync() {
            // TODO
        }
    };


    //
    ///////////////////////////////////// Destroy session //////////////////////////////////////////
    //


    Snippet destroySession = new Snippet("destroy session") {
        @Override
        public void execute() {
            QBAuth.deleteSession(new QBEmptyCallback(">>> Session Destroy OK"));
        }
    };

    Snippet destroySessionSynchronous = new AsyncSnippet("delete session (synchronous)", context) {
        @Override
        public void executeAsync() {
            try {
                QBAuth.deleteSession();
                Log.i(TAG, "session destroyed ");
            } catch (QBResponseException e) {
                Log.i(TAG, "destroy fail");
                setException(e);
            }
        }
    };
}
