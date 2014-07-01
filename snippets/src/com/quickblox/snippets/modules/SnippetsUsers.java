package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBRequestCanceler;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.helper.StringifyArrayList;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.internal.module.content.Consts;
import com.quickblox.module.auth.model.QBProvider;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.snippets.AsyncSnippet;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vfite on 04.02.14.
 */

public class SnippetsUsers extends Snippets{
    private static final String TAG = SnippetsUsers.class.getSimpleName();

    public SnippetsUsers(Context context) {
        super(context);

        snippets.add(signInUserWithLogin);
        snippets.add(signInUserWithLoginSynchronous);
        //
        snippets.add(signInUserWithEmail);
        snippets.add(signInUserWithEmailSynchronous);
        //
        snippets.add(signInUsingSocialProvider);
        snippets.add(signInUsingSocialProviderSynchronous);
        //
        snippets.add(signOut);
        snippets.add(signOutSynchronous);
        //
        snippets.add(signUpUserNewCallback);
        snippets.add(signUpUserSynchronous);
        //
        snippets.add(signUpSignInUser);
        snippets.add(signUpSignInUserSynchronous);
        //
        //
        snippets.add(updateUser);
        snippets.add(updateUserSynchronous);
        //
        //
        snippets.add(deleteUserById);
        snippets.add(deleteUserByIdSynchronous);
        //
        snippets.add(deleteUserByExternalId);
        snippets.add(deleteUserByExternalIdSynchronous);
        //
        //
        snippets.add(resetPassword);
        snippets.add(resetPasswordSynchronous);
        //
        //
        snippets.add(getAllUsers);
        snippets.add(getAllUsersSynchronous);
        //
        snippets.add(getUsersByIds);
        snippets.add(getUsersByIdsSynchronous);
        //
        snippets.add(getUsersByLoginsNewCallback);
        snippets.add(getUsersByLoginsSynchronous);
        //
        snippets.add(getUsersByEmailsNewCallback);

        //
        snippets.add(getUsersWithTagsNewCallback);

        //
        snippets.add(getUsersWithFullNameNewCallback);
        //
        snippets.add(getUserByIdNewCallback);
        snippets.add(getUserWithLoginNewCallback);
        snippets.add(getUserWithTwitterIdNewCallback);
        snippets.add(getUserWithFacebookIdNewCallback);
        snippets.add(getUserWithEmailNewCallback);
        snippets.add(getUserWithEmailSynchronous);
        snippets.add(getUserWithExternalIdNewCallback);
    }


    //
    ///////////////////////////////// Sign In with login ///////////////////////////////////////////
    //


    Snippet signInUserWithLogin = new Snippet("sign in user", "with login") {
        @Override
        public void execute() {

            final QBUser user = new QBUser("testuser", "testpassword");

            final QBRequestCanceler canceler = QBUsers.signIn(user, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle params) {
                    Log.i(TAG, ">>> User was successfully signed in:  " + user.toString());
                }

                @Override
                public void onError(List<String> errors) {
                       handleErrors(errors);
                }
            });
        }
    };

    Snippet signInUserWithLoginSynchronous = new AsyncSnippet("sign in user (synchronous)", "with login" , context) {
        @Override
        public void executeAsync() {
            QBUser user = new QBUser();
            user.setLogin("testuser");
            user.setPassword("testpassword");
            QBUser userResult = null;
            try {
                userResult =  QBUsers.signIn(user);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(userResult != null){
                Log.i(TAG, "User was successfully signed in: "+userResult);
            }
        }
    };


    //
    ///////////////////////////////// Sign In with email ///////////////////////////////////////////
    //


    Snippet signInUserWithEmail = new Snippet("sign in user", "with email") {
        @Override
        public void execute() {

            final QBUser user = new QBUser();
            user.setEmail("test987@test.com");
            user.setPassword("testpassword");

            QBUsers.signIn(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User was successfully signed in, " + user);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet signInUserWithEmailSynchronous = new AsyncSnippet("sign in user (synchronous)", "with email", context) {
        @Override
        public void executeAsync() {
            QBUser user = new QBUser();
            user.setEmail("test987@test.com");
            user.setPassword("testpassword");
            QBUser userResult = null;
            try {
                userResult =  QBUsers.signIn(user);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(userResult != null){
                Log.i(TAG, "User was successfully signed in,"+userResult);
            }
        }
    };


    //
    ///////////////////////////////// Sign In with social provider /////////////////////////////////
    //


    Snippet signInUsingSocialProvider = new Snippet("sign in user", "with social provider") {
        @Override
        public void execute() {
            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User was successfully signed in, " + user);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet signInUsingSocialProviderSynchronous = new AsyncSnippet("sign in user (synchronous)", "with social provider", context) {
        @Override
        public void executeAsync() {

            String facebookAccessToken = "AAAEra8jNdnkBABYf3ZBSAz9dgLfyK7tQNttIoaZA1cC40niR6HVS0nYuufZB0ZCn66VJcISM8DO2bcbhEahm2nW01ZAZC1YwpZB7rds37xW0wZDZD";

            QBUser userResult = null;
            try {
                userResult = QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(userResult != null){
                Log.i(TAG, "User was successfully signed in,"+userResult);
            }
        }
    };


    //
    ///////////////////////////////////////// Sign Out /////////////////////////////////////////////
    //


    Snippet signOut = new Snippet("sign out") {
        @Override
        public void execute() {
            QBUsers.signOut(new QBEntityCallbackImpl(){

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> User was successfully signed out");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet signOutSynchronous = new AsyncSnippet("sign out (synchronous)", context) {
        @Override
        public void executeAsync() {
            try {
                QBUsers.signOut();
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };


    //
    ///////////////////////////////////////// Sign Up //////////////////////////////////////////////
    //


    Snippet signUpUserNewCallback = new Snippet("sign up user") {
        @Override
        public void execute() {

            final QBUser user = new QBUser("testuser12344443", "testpassword");
            user.setEmail("test123456789w0@test.com");
            user.setExternalId("02345777");
            user.setFacebookId("1233453457767");
            user.setTwitterId("12334635457");
            user.setFullName("fullName5");
            user.setPhone("+18904567812");
            StringifyArrayList<String> tags = new StringifyArrayList<String>();
            tags.add("firstTag");
            tags.add("secondTag");
            tags.add("thirdTag");
            tags.add("fourthTag");
            user.setTags(tags);
            user.setWebsite("website.com");

            QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User was successfully signed up, " + user);
                }

                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet signUpUserSynchronous = new AsyncSnippet("sign up user (synchronous)", context) {
        @Override
        public void executeAsync() {

            final QBUser user = new QBUser("testuser12344443", "testpassword");
            user.setEmail("test1234567589w0@test.com");
            user.setExternalId("02345777");
            user.setFacebookId("1233453457767");
            user.setTwitterId("12334635457");
            user.setFullName("fullName5");
            user.setPhone("+18904567812");
            StringifyArrayList<String> tags = new StringifyArrayList<String>();
            tags.add("firstTag");
            tags.add("secondTag");
            tags.add("thirdTag");
            tags.add("fourthTag");
            user.setTags(tags);
            user.setWebsite("website.com");
            QBUser qbUserResult = null;
            try {
                qbUserResult = QBUsers.signUp(user);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(qbUserResult != null){
                Log.i(TAG, ">>> User was successfully signed up, " + qbUserResult);
            }
        }
    };


    //
    ///////////////////////////////// Sign Up and Sign In task /////////////////////////////////////
    //


    Snippet signUpSignInUser = new Snippet("sign up and sign in user") {
        @Override
        public void execute() {

            final QBUser user = new QBUser("testuser12344443", "testpassword");
            user.setEmail("test123456789w0@test.com");
            user.setExternalId("02345777");
            user.setFacebookId("1233453457767");
            user.setTwitterId("12334635457");
            user.setFullName("fullName5");
            user.setPhone("+18904567812");
            StringifyArrayList<String> tags = new StringifyArrayList<String>();
            tags.add("firstTag");
            tags.add("secondTag");
            tags.add("thirdTag");
            tags.add("fourthTag");
            user.setTags(tags);
            user.setWebsite("website.com");

            QBUsers.signUpSignInTask(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User was successfully signed up and signed in, " + user);
                }

                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });


        }
    };

    Snippet signUpSignInUserSynchronous = new AsyncSnippet("sign up and sign in user (synchronous)", context) {
        @Override
        public void executeAsync() {
            final QBUser user = new QBUser("testuser12344443", "testpassword");
            user.setEmail("test123456789w0@test.com");
            user.setExternalId("02345777");
            user.setFacebookId("1233453457767");
            user.setTwitterId("12334635457");
            user.setFullName("fullName5");
            user.setPhone("+18904567812");
            StringifyArrayList<String> tags = new StringifyArrayList<String>();
            tags.add("firstTag");
            tags.add("secondTag");
            tags.add("thirdTag");
            tags.add("fourthTag");
            user.setTags(tags);
            user.setWebsite("website.com");


            QBUser userResult = null;
            try {
                userResult = QBUsers.signUpSignInTask(user);
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };



    //
    ///////////////////////////////// Update user ///////////////////////////////////////////
    //

    Snippet updateUser = new Snippet("update user") {
        @Override
        public void execute() {
            final QBUser user = new QBUser();
            user.setId(53779);
            user.setFullName("Monro");
            user.setEmail("test987@test.com");
            user.setExternalId("987");
            user.setFacebookId("987");
            user.setTwitterId("987");
            user.setFullName("galog");
            user.setPhone("+123123123");
            StringifyArrayList<String> tags = new StringifyArrayList<String>();
            tags.add("man");
            user.setTags(tags);
            user.setWebsite("google.com");

            QBUsers.updateUser(user, new QBEntityCallbackImpl<QBUser>(){
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet updateUserSynchronous = new AsyncSnippet("update user (synchronous)", context) {
        @Override
        public void executeAsync() {
            final QBUser user = new QBUser();
            user.setId(53779);
            user.setFullName("galog");
            user.setWebsite("google.com");

            QBUser userResult = null;
            try {
                userResult = QBUsers.updateUser(user);
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };


    //
    ///////////////////////////////// Delete user ///////////////////////////////////////////
    //


    Snippet deleteUserById = new Snippet("delete user", "by id") {
        @Override
        public void execute() {

            int userId = 562;
            QBUsers.deleteUser(new QBUser(userId), new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> User was successfully deleted");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet deleteUserByIdSynchronous = new AsyncSnippet("delete user (synchronous)", "by id", context) {
        @Override
        public void executeAsync() {

            int userId = 562;

            try {
                QBUsers.deleteUser(new QBUser(userId));
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };


    //
    ///////////////////////////////// Delete user ///////////////////////////////////////////
    //


    Snippet deleteUserByExternalId = new Snippet("delete user", "by external id") {
        @Override
        public void execute() {
            QBUsers.deleteByExternalId("568965444", new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> User was successfully deleted");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet deleteUserByExternalIdSynchronous = new AsyncSnippet("delete user (synchronous)", "by external id", context) {
        @Override
        public void executeAsync() {

            try {
                QBUsers.deleteByExternalId("568965444");
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };


    //
    //////////////////////////////////////// Resey password ////////////////////////////////////////
    //


    Snippet resetPassword = new Snippet("reset password") {
        @Override
        public void execute() {
            QBUsers.resetPassword("test987@test.com", new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> Email was sent");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet resetPasswordSynchronous = new AsyncSnippet("reset password(synchronous)", context) {
        @Override
        public void executeAsync() {

            try {
                QBUsers.resetPassword("test987@test.com");
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };


    //
    //////////////////////////////////////// Get users /////////////////////////////////////////////
    //


    Snippet getAllUsers = new Snippet("get all users") {
        @Override
        public void execute() {

            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(2);
            pagedRequestBuilder.setPerPage(5);

            QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                   handleErrors(errors);
                }
            });

        }
    };

    Snippet getAllUsersSynchronous = new AsyncSnippet("get all users (synchronous)", context) {
        @Override
        public void executeAsync() {
            // TODO
        }
    };


    //
    //////////////////////////////////////// Get users by IDs //////////////////////////////////////
    //


    Snippet getUsersByIds = new Snippet("get users by ids") {
        @Override
        public void execute() {
            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(1);
            pagedRequestBuilder.setPerPage(10);

            List<Integer> usersIds = new ArrayList<Integer>();
            usersIds.add(378);
            usersIds.add(379);
            usersIds.add(380);

            QBUsers.getUsersByIDs(usersIds, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };


    Snippet getUsersByIdsSynchronous = new AsyncSnippet("get users by ids (synchronous)", context) {
        @Override
        public void executeAsync() {
            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(1);
            pagedRequestBuilder.setPerPage(10);

            List<Integer> usersIds = new ArrayList<Integer>();
            usersIds.add(378);
            usersIds.add(379);
            usersIds.add(380);

            try {
                QBUsers.getUsersByIDs(usersIds, pagedRequestBuilder, (Bundle)null);
            } catch (QBResponseException e) {
                setException(e);
            }
        }
    };




    Snippet getUsersByLoginsNewCallback = new Snippet("get users by logins") {
        @Override
        public void execute() {
            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(1);
            pagedRequestBuilder.setPerPage(10);

            ArrayList<String> usersLogins = new ArrayList<String>();
            usersLogins.add("bob");
            usersLogins.add("john");

            QBUsers.getUsersByLogins(usersLogins, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet getUsersByLoginsSynchronous = new AsyncSnippet("get users by logins synchornous", context) {
        @Override
        public void executeAsync() {
            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(1);
            pagedRequestBuilder.setPerPage(10);

            ArrayList<String> usersLogins = new ArrayList<String>();
            usersLogins.add("bob");
            usersLogins.add("john");
            Bundle params = new Bundle();
            ArrayList<QBUser> usersByLogins = null;
            try {
                usersByLogins = QBUsers.getUsersByLogins(usersLogins, pagedRequestBuilder, params);
            } catch (QBResponseException e) {
               setException(e);
            }

            if(usersByLogins != null){
                Log.i(TAG, ">>> Users: " + usersByLogins.toString());
                Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
            }
        }
    };


    Snippet getUsersByEmailsNewCallback = new Snippet("get users by emails") {
        @Override
        public void execute() {
            QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
            pagedRequestBuilder.setPage(1);
            pagedRequestBuilder.setPerPage(10);

            ArrayList<String> usersEmails = new ArrayList<String>();
            usersEmails.add("asd@ffg.fgg");
            usersEmails.add("ghh@ggh.vbb");

            QBUsers.getUsersByEmails(usersEmails, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet getUsersWithFullNameNewCallback = new Snippet("get user with full name") {
        @Override
        public void execute() {
            String fullName = "fullName";
            QBUsers.getUsersByFullName(fullName, null,  new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getUsersWithTagsNewCallback = new Snippet("get users with tags") {
        @Override
        public void execute() {
            ArrayList<String> userTags = new ArrayList<String>();
            userTags.add("man");
            userTags.add("car");

            QBUsers.getUsersByTags(userTags, null, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                    Log.i(TAG, ">>> Users: " + users.toString());
                    Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };


    Snippet getUserByIdNewCallback = new Snippet("get user by id") {
        @Override
        public void execute() {
            QBUsers.getUser(53779, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet getUserWithLoginNewCallback = new Snippet("get user with login") {
        @Override
        public void execute() {
            String login = "testuser";
            QBUser qbUser = new QBUser();
            qbUser.setLogin(login);
            QBUsers.getUserByLogin(qbUser, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet getUserWithTwitterIdNewCallback = new Snippet("get user with twitter id") {
        @Override
        public void execute() {
            String twitterId = "56802037340";
            QBUsers.getUserByTwitterId(twitterId, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet getUserWithFacebookIdNewCallback = new Snippet("get user with facebook id") {
        @Override
        public void execute() {
            String facebookId = "100003123141430";
            QBUsers.getUserByFacebookId(facebookId, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }
                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet getUserWithEmailNewCallback = new Snippet("get user with email") {
        @Override
        public void execute() {
            String email = "test123@test.com";
            QBUsers.getUserByEmail(email, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }
                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };

    Snippet getUserWithEmailSynchronous = new AsyncSnippet("get user with email synchornous", context) {

        QBUser userByEmail;
        @Override
        public void executeAsync() {
            String email = "test123@test.com";
            try {
                userByEmail = QBUsers.getUserByEmail(email);
            } catch (QBResponseException e) {
                setException(e);
            }
        }

        @Override
        protected void postExecute() {
            super.postExecute();
            if( userByEmail != null){
                Log.i(TAG, ">>> User: " + userByEmail.toString());
            }
        }
    };

    Snippet getUserWithExternalIdNewCallback = new Snippet("get user with external id") {
        @Override
        public void execute() {
            String externalId = "123145235";
            QBUsers.getUserByExternalId(externalId, new QBEntityCallbackImpl<QBUser>() {

                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.i(TAG, ">>> User: " + user.toString());
                }
                @Override
                public void onError(List<String> errors) {
                    super.onError(errors);
                }
            });
        }
    };
}
