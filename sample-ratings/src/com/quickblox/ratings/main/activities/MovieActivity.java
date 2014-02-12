package com.quickblox.ratings.main.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.ratings.QBRatings;
import com.quickblox.module.ratings.model.QBScore;
import com.quickblox.ratings.main.R;
import com.quickblox.ratings.main.core.DataHolder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 27.11.12
 * Time: 16:31
 */
public class MovieActivity extends Activity implements QBEntityCallback<QBScore> {

    private final String POSITION = "position";
    ImageView movieCover;
    TextView movieDescription;
    LinearLayout starsHolder;
    final int STARS_NUMBER = 10;
    double movieRating;
    ProgressDialog progressDialog;
    int position;
    LinearLayout dialogStarsHolder;

    LayoutInflater inflater;
    AlertDialog alert;

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.movie);
        initialize();

    }

    private void initialize() {
        position = getIntent().getIntExtra(POSITION, 0);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        movieCover = (ImageView) findViewById(R.id.movie_cover);
        movieDescription = (TextView) findViewById(R.id.movie_description);
        starsHolder = (LinearLayout) findViewById(R.id.starts_ll);
        movieCover.setImageDrawable(DataHolder.getDataHolder().getMovieCover(position));
        movieDescription.setText(DataHolder.getDataHolder().getMovieDescription(position));
        applyStars(DataHolder.getDataHolder().getMovieRating(position), starsHolder);
    }

    private void applyStars(double movieRating, LinearLayout imageHolder) {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (imageHolder != null) {
            imageHolder.removeAllViews();
        }
        this.movieRating = movieRating;
        for (int i = 0; i < STARS_NUMBER; ) {
            ImageView star = (ImageView) inflater.inflate(R.layout.image_holder, null);
            if (i < movieRating) {
                star.setImageDrawable(getResources().getDrawable(R.drawable.star_full));
            } else {
                star.setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
            }
            star.setTag(++i);
            star.setOnClickListener(getOnStarClickListener());
            if (imageHolder != null) {
                imageHolder.addView(star);
            }
        }
    }

    private View.OnClickListener getOnStarClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyStars((Integer) view.getTag(), dialogStarsHolder);
            }
        };
    }

    private void showCustomAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout customDialog = (LinearLayout) inflater.inflate(R.layout.custom_dialog, null);
        dialogStarsHolder = (LinearLayout) customDialog.findViewById(R.id.stars_holder);
        applyStars(0, dialogStarsHolder);
        builder.setView(customDialog);
        alert = builder.create();
        alert.show();

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_dialog_btn:
                // Create new Score
                createScore();
                this.alert.dismiss();
                break;
            case R.id.rate_main_view_btn:
                showCustomAlert();
                break;
            case R.id.cancel:
                this.alert.dismiss();
                break;

        }

    }

    private void createScore() {
        progressDialog.show();
        // ================= QuickBlox ===== Step 3 =================
        // Rate it!
        QBScore qbScore = new QBScore();
        qbScore.setGameModeId(DataHolder.getDataHolder().getMovieGameModeId(position));
        qbScore.setValue((int)movieRating);
        qbScore.setUserId(DataHolder.getDataHolder().getQbUserId());
        QBRatings.createScore(qbScore, this);
    }

    @Override
    public void onSuccess(QBScore qbScore, Bundle bundle) {
        progressDialog.hide();
        Toast.makeText(this, "Score successfully send!", Toast.LENGTH_SHORT).show();
        DataHolder.getDataHolder().setChosenMoviePosition(position);
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
