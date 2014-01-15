package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import android.widget.Toast;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.model.QBEntity;
import com.quickblox.core.result.Result;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBProvider;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.util.List;

/**
 * User: Oleg Soroka
 * Date: 01.10.12
 * Time: 19:28
 */
public class SnippetsAuth extends Snippets {

    private static final String TAG = SnippetsAuth.class.getSimpleName();

    public SnippetsAuth(Context context) {
        super(context);

        snippets.add(createSession);
        snippets.add(createSessionNewCallback);
        snippets.add(createSessionSync);
        snippets.add(createSessionWithUser);
        snippets.add(createSessionWithUserNewCallback);
        snippets.add(createSessionWithUserEmail);
        snippets.add(createSessionWithSocialProvider);
        snippets.add(createSessionWithSocialProviderNewCallback);
        snippets.add(destroySession);
        snippets.add(destroySessionNewCallback);
        snippets.add(destroySessionSync);
    }

    Snippet createSession = new Snippet("create session") {
        @Override
        public void execute() {
            QBAuth.createSession(new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSuccess()) {
                        QBSessionResult sessionResult = (QBSessionResult) result;
                        Log.i(TAG, ">>> Session = " + sessionResult.getSession());
                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet createSessionNewCallback = new Snippet("create session with new callback") {
        @Override
        public void execute() {
            QBAuth.createSession( new QBEntityCallback<QBSession>() {

                @Override
                public void onSuccess(QBSession result) {
                    super.onSuccess(result);
                    Log.i(TAG, "session="+result.getToken());
                }

                @Override
                public void onSuccess() {
                       Log.i(TAG, "session=");
                }

                @Override
                public void onError(List<String> eroors) {

                }
            });
        }
    };

    Snippet createSessionSync = new Snippet("create session syncronize") {
        @Override
        public void execute() {

            (new AsyncTask<Void, Void, Void>(){
                public QBSession session;

                @Override
                protected Void doInBackground(Void... objects) {
                    Log.i(TAG, "doInBackground");
                    try {
                        session = QBAuth.createSession();
                    } catch (QBResponseException e) {
                        e.printStackTrace();
                        Log.i(TAG, "session fail"+e.getErrors());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if(session!=null){
                        Log.i(TAG, "session created"+session.getToken());
                        Toast.makeText(context, "session created"+session.getToken(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();

        }
    };

    Snippet createSessionWithUser = new Snippet("create session", "with user login") {
        @Override
        public void execute() {

            QBAuth.createSession("AndroidGirl", "AndroidGirl", new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSuccess()) {
                        QBSessionResult sessionResult = (QBSessionResult) result;
                        Log.i(TAG, ">>> Session = " + sessionResult.getSession());
                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet createSessionWithUserNewCallback = new Snippet("create session", "with user login on new callback") {
        @Override
        public void execute() {

            QBAuth.createSession(new QBUser("AndroidGirl", "AndroidGirl"), new QBEntityCallback<QBSession>() {
                @Override
                public void onSuccess(QBSession result) {
                    super.onSuccess(result);
                    Log.i(TAG, "session="+result.getToken());
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "session=");
                }

                @Override
                public void onError(List<String> eroors) {
                                  handleErrors(eroors, 0);
                }
            });
        }
    };

    Snippet createSessionWithUserEmail = new Snippet("create session", "with user email") {
        @Override
        public void execute() {

            QBAuth.createSessionByEmail("test123@test.com", "testpassword", new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSuccess()) {
                        QBSessionResult sessionResult = (QBSessionResult) result;
                        Log.i(TAG, ">>> Session = " + sessionResult.getSession());

                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet createSessionWithSocialProvider = new Snippet("create session with social provider") {
        @Override
        public void execute() {

            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBAuth.createSessionUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSuccess()) {
                        QBSessionResult sessionResult = (QBSessionResult) result;
                        Log.i(TAG, ">>> Session = " + sessionResult.getSession());

                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet createSessionWithSocialProviderNewCallback = new Snippet("create session with social provider on new callback") {
        @Override
        public void execute() {

            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBAuth.createSessionUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, new QBEntityCallback<QBSession>() {

                @Override
                public void onSuccess(QBSession session) {
                    Log.i(TAG, "session created="+session.getToken());
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors, 0);
                }
            });
        }
    };

    Snippet destroySession = new Snippet("destroy session") {
        @Override
        public void execute() {
            QBAuth.deleteSession(new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSuccess()) {
                        Log.i(TAG, ">>> Session Destroy OK");
                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet destroySessionNewCallback = new Snippet("destroy session with new callback") {
        @Override
        public void execute() {
            QBAuth.deleteSession(new QBEntityCallback() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> Session Destroy OK");
                }

                @Override
                public void onError(List eroors) {
                             handleErrors(eroors, 0);
                }
            });
        }
    };

    Snippet  destroySessionSync = new Snippet("delete session syncronize") {
        @Override
        public void execute() {

            (new AsyncTask<Void, Void, Void>(){
                public QBSession session;
                List<String> erors;

                @Override
                protected Void doInBackground(Void... objects) {
                    Log.i(TAG, "doInBackground");
                    try {
                        QBAuth.deleteSession();
                    } catch (QBResponseException e) {
                        Log.i(TAG, "delete fail"+e.getErrors());
                        erors = e.getErrors();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if(erors == null){
                        Log.i(TAG, ">>> Session Destroy OK");
                        Toast.makeText(context, " Session Destroy OK", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();

        }
    };
}