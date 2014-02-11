package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.internal.module.content.Consts;
import com.quickblox.module.ratings.QBRatings;
import com.quickblox.module.ratings.model.QBAverage;
import com.quickblox.module.ratings.model.QBGameMode;
import com.quickblox.module.ratings.model.QBScore;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.snippets.AsyncSnippet;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vfite on 11.02.14.
 */
public class SnippetsRatingNew extends Snippets {
    private static final String TAG = SnippetsRatings.class.getSimpleName();

    public SnippetsRatingNew(Context context) {
        super(context);

        snippets.add(createGameMode);
        snippets.add(getGameModeWithId);
        snippets.add(updateGameMode);
        snippets.add(getGameModes);
        snippets.add(getGameModesSync);
        snippets.add(deleteGameModeWithId);

        snippets.add(createScore);
        snippets.add(getScoreWithId);
        snippets.add(updateScore);
        snippets.add(deleteScoreWithId);
        snippets.add(getTopNScores);
        snippets.add(getScoresWithUserId);
        snippets.add(getScoresWithUserIdSync);

        snippets.add(getAverageByGameModeId);
        snippets.add(getAverageByGameModeIdSync);
        snippets.add(getAverageForApp);
    }

    //
    ///////////////////////////////////////////// Game mode /////////////////////////////////////////////
    //
    Snippet createGameMode = new Snippet("create game mode") {
        @Override
        public void execute() {
            QBGameMode gameMode = new QBGameMode("Guitar hero mode");

            QBRatings.createGameMode(gameMode, gameModeCallback);
        }
    };

    QBEntityCallback<QBGameMode> gameModeCallback = new QBEntityCallbackImpl<QBGameMode>() {

        @Override
        public void onSuccess(QBGameMode qbGameMode, Bundle args) {
            Log.i(TAG, ">>> game mode is:" + qbGameMode.toString());
        }

        @Override
        public void onError(List<String> errors) {
            handleErrors(errors);
        }
    };


    Snippet getGameModeWithId = new Snippet("get game mode") {
        @Override
        public void execute() {
            QBGameMode gameMode = new QBGameMode(311);
            QBRatings.getGameMode(gameMode, gameModeCallback);
        }
    };

    Snippet updateGameMode = new Snippet("update game mode") {
        @Override
        public void execute() {
            QBGameMode qbGameMode = new QBGameMode();
            qbGameMode.setId(310);
            qbGameMode.setTitle("new title for game mode yeahhh");

            QBRatings.updateGameMode(qbGameMode, gameModeCallback);
        }
    };

    Snippet deleteGameModeWithId = new Snippet("delete game mode") {
        @Override
        public void execute() {
            QBGameMode gameMode = new QBGameMode(3190);
            QBRatings.deleteGameMode(gameMode, new QBEmptyCallback(">>>game mode successfully deleted:"));
        }
    };

    Snippet getGameModes = new Snippet("get game modes") {
        @Override
        public void execute() {
            QBRatings.getGameModes(new QBEntityCallbackImpl<ArrayList<QBGameMode>>() {

                @Override
                public void onSuccess(ArrayList<QBGameMode> gameModes, Bundle args) {
                    Log.i(TAG, "GameMode list - " + gameModes.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getGameModesSync = new AsyncSnippet("get game modes synchronous", context) {
        @Override
        public void executeAsync() {
            ArrayList<QBGameMode> gameModes = null;
            try {
                gameModes = QBRatings.getGameModes();
            } catch (QBResponseException e) {
                setException(e);
            }
            if(gameModes != null){
                Log.i(TAG, "GameMode list - " + gameModes.toString());
            }
        }
    };

    //
    ///////////////////////////////////////////// Scores /////////////////////////////////////////////
    //
    Snippet createScore = new Snippet("create score") {
        @Override
        public void execute() {
            QBScore score = new QBScore();
            score.setGameModeId(311);
            score.setValue(4);

            QBRatings.createScore(score, scoreCallback);
        }
    };

    QBEntityCallback<QBScore> scoreCallback = new QBEntityCallbackImpl<QBScore>() {

        @Override
        public void onSuccess(QBScore qbScore, Bundle args) {
            Log.i(TAG, ">>> Score is:" + qbScore.toString());
        }

        @Override
        public void onError(List<String> errors) {
            handleErrors(errors);
        }
    };

    Snippet getScoreWithId = new Snippet("get score") {
        @Override
        public void execute() {
            QBScore score = new QBScore(1945);
            Date date = new Date(System.currentTimeMillis());
            score.setCreatedAt(date);

            QBRatings.getScore(score, scoreCallback);
        }
    };

    Snippet deleteScoreWithId = new Snippet("delete score") {
        @Override
        public void execute() {
            QBScore score = new QBScore(1945);

            QBRatings.deleteScore(score, new QBEmptyCallback("Score deleted"));
        }
    };

    Snippet updateScore = new Snippet("update score") {
        @Override
        public void execute() {
            QBScore qbScore = new QBScore();
            qbScore.setId(1945);
            qbScore.setValue(1945);

            QBRatings.updateScore(qbScore, scoreCallback);
        }
    };

    Snippet getTopNScores = new Snippet("get top n scores") {
        @Override
        public void execute() {
            QBGameMode qbGameMode = new QBGameMode();
            qbGameMode.setId(311);

            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
            requestBuilder.setPage(1);
            requestBuilder.setPerPage(20);

            QBRatings.getTopScores(qbGameMode, 10, requestBuilder, scoresCallback);
        }
    };

    QBEntityCallback<ArrayList<QBScore>> scoresCallback =  new QBEntityCallbackImpl<ArrayList<QBScore>>() {
        @Override
        public void onSuccess(ArrayList<QBScore> scores, Bundle args) {
            Log.i(TAG, "Score list " + scores.toString());
            Log.i(TAG, "currentPage: " + args.getInt(Consts.CURR_PAGE));
            Log.i(TAG, "perPage: " + args.getInt(Consts.PER_PAGE));
            Log.i(TAG, "totalPages: " + args.getInt(Consts.TOTAL_ENTRIES));
        }

        @Override
        public void onError(List<String> errors) {
            handleErrors(errors);
        }
    };

    Snippet getScoresWithUserId = new Snippet("get scores with user id") {
        @Override
        public void execute() {
            QBUser qbUser = new QBUser();
            qbUser.setId(53779);

            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
            requestBuilder.setPage(1);
            requestBuilder.setPerPage(20);

            QBRatings.getScoresByUser(qbUser, requestBuilder, scoresCallback);
        }
    };

    Snippet getScoresWithUserIdSync = new AsyncSnippet("get scores with user id synchronous", context) {
        @Override
        public void executeAsync()  {
            QBUser qbUser = new QBUser();
            qbUser.setId(53779);

            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
            requestBuilder.setPage(1);
            requestBuilder.setPerPage(20);
            Bundle args = new Bundle();
            ArrayList<QBScore> scoresByUser = null;
            try {
                scoresByUser = QBRatings.getScoresByUser(qbUser, requestBuilder, args);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(scoresByUser != null){
                Log.i(TAG, "Score list " + scoresByUser.toString());
                Log.i(TAG, "currentPage: " + args.getInt(Consts.CURR_PAGE));
                Log.i(TAG, "perPage: " + args.getInt(Consts.PER_PAGE));
                Log.i(TAG, "totalPages: " + args.getInt(Consts.TOTAL_ENTRIES));
            }
        }
    };


    //
    ///////////////////////////////////////////// Average /////////////////////////////////////////////
    //

    Snippet getAverageForApp = new Snippet("get average for application") {
        @Override
        public void execute() {
            QBRatings.getAveragesByApp(new QBEntityCallbackImpl<ArrayList<QBAverage>>() {

                @Override
                public void onSuccess(ArrayList<QBAverage> result, Bundle params) {
                    Log.i(TAG, "AverageList- " + result.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getAverageByGameModeId = new Snippet("get average by game mode id") {
        @Override
        public void execute() {
            QBGameMode qbGameMode = new QBGameMode();
            qbGameMode.setId(311);
            QBRatings.getAverageByGameMode(qbGameMode, new QBEntityCallbackImpl<QBAverage>() {

                @Override
                public void onSuccess(QBAverage average, Bundle args) {
                    Log.i(TAG, "Average - " + average.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getAverageByGameModeIdSync = new AsyncSnippet("get average by game mode id synchronous", context) {
        @Override
        public void executeAsync() {
            QBGameMode qbGameMode = new QBGameMode();
            qbGameMode.setId(311);
            QBAverage averageByGameMode = null;
            try {
                averageByGameMode = QBRatings.getAverageByGameMode(qbGameMode);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(averageByGameMode != null){
                Log.i(TAG, "Average - " + averageByGameMode.toString());
            }
        }
    };
}
