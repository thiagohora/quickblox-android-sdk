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
public class SnippetsAuthNew extends Snippets{

    private static final String TAG = SnippetsAuthNew.class.getSimpleName();

    public SnippetsAuthNew(Context context) {
        super(context);

        snippets.add(createSessionNewCallback);
        snippets.add(createSessionSync);
        snippets.add(createSessionWithUserNewCallback);
        snippets.add(createSessionWithSocialProviderNewCallback);
        snippets.add(destroySessionNewCallback);
        snippets.add(destroySessionSync);
    }

    Snippet createSessionNewCallback = new Snippet("create session with new callback") {
        @Override
        public void execute() {
            QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {

                @Override
                public void onSuccess(QBSession result, Bundle args) {
                    super.onSuccess(result, args);
                    Log.i(TAG, "session=" + result.getToken());
                }

                @Override
                public void onError(List<String> eroors) {

                }
            });
        }
    };

    Snippet createSessionSync = new AsyncSnippet("create session syncronize", context) {
        @Override
        public void executeAsync() {
            QBSession session = null;
            try {
                session = QBAuth.createSession();
            } catch (QBResponseException e) {
                e.printStackTrace();
                setException(e);
            }
            Log.i(TAG, "session created"+session.getToken());
        }
    };

    Snippet createSessionWithUserNewCallback = new Snippet("create session", "with user login on new callback") {
        @Override
        public void execute() {

            QBAuth.createSession(new QBUser("AndroidGirl", "AndroidGirl"), new QBEntityCallbackImpl<QBSession>() {
                @Override
                public void onSuccess(QBSession result, Bundle args) {
                    super.onSuccess(result, args);
                    Log.i(TAG, "session="+result.getToken());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet createSessionWithSocialProviderNewCallback = new Snippet("create session with social provider on new callback") {
        @Override
        public void execute() {

            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBAuth.createSessionUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, new QBEntityCallbackImpl<QBSession>() {

                @Override
                public void onSuccess(QBSession session,  Bundle args) {
                    Log.i(TAG, "session created="+session.getToken());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet destroySessionNewCallback = new Snippet("destroy session with new callback") {
        @Override
        public void execute() {
            QBAuth.deleteSession(new QBEntityCallbackImpl() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> Session Destroy OK");
                }

                @Override
                public void onError(List eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet  destroySessionSync = new AsyncSnippet("delete session syncronize", context) {
        @Override
        public void executeAsync() {
            try {
                QBAuth.deleteSession();
            } catch (QBResponseException e) {
                Log.i(TAG, "delete fail");
                setException(e);
            }
        }
    };
}
