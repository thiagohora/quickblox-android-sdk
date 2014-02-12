package com.quickblox.ratings.main.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.ratings.QBRatings;
import com.quickblox.module.ratings.model.QBAverage;
import com.quickblox.module.ratings.model.QBGameMode;
import com.quickblox.ratings.main.R;
import com.quickblox.ratings.main.adapter.MoviesListAdapter;
import com.quickblox.ratings.main.core.DataHolder;

import java.util.List;

public class MoviesListActivity extends Activity implements AdapterView.OnItemClickListener, QBEntityCallback<QBAverage> {

    private final int APP_ID = 99;
    ListView moviesLV;
    MoviesListAdapter moviesListAdapter;
    private final String POSITION = "position";
    private final int NONE_SCORE_CHANGE = -1;
    int i;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list);
        initialize();
    }

    private void initialize() {
        moviesLV = (ListView) findViewById(R.id.movie_list);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        moviesListAdapter = new MoviesListAdapter(this);
        moviesLV.setAdapter(moviesListAdapter);
        moviesLV.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        int index = DataHolder.getDataHolder().getChosenMoviePosition();
        if (index != NONE_SCORE_CHANGE) {
            getAvarageRatingForMovie(index);
        }
    }

    // Get avarage by all score for game mode
    private void getAvarageRatingForMovie(int index) {
        QBGameMode qbGameMode = new QBGameMode();
        qbGameMode.setId(DataHolder.getDataHolder().getMovieGameModeId(index));
        QBRatings.getAverageByGameMode(qbGameMode, this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, MovieActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onSuccess(QBAverage average, Bundle bundle) {
        if (average.getValue() != null) {
            DataHolder.getDataHolder().setMovieRating(DataHolder.getDataHolder().getChosenMoviePosition(), average.getValue());
            DataHolder.getDataHolder().setChosenMoviePosition(NONE_SCORE_CHANGE);
            progressDialog.hide();
            moviesListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> errors) {
        Toast.makeText(this, errors.toString(), Toast.LENGTH_SHORT).show();
        progressDialog.hide();
    }
}