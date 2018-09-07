package example.com.crackle.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.utils.Utils;
import example.com.crackle.adapter.MovieFragmentPagerAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.DetailResults;
import example.com.crackle.model.Movie;
import example.com.crackle.utils.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.IMAGE_URL_SIZE;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.backdrop_image)
    ImageView backdropImage;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.genre)
    TextView genre;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingtoolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    private Movie movie;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //resolve references to views
        ButterKnife.bind(this);

        //setup toolbar
        setSupportActionBar(toolbar);

        //add back navigation option on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //set title on on collapsed toolbar
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //verify if the toolbar is completely collapsed and set the movie name as the title
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(movie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    //display an empty string when toolbar is expanded
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }

            }
        });

        //get the list of all genre code and corresponding names from local json file
        SparseArray<String> genreMap = Utils.fetchAllGenres(this);

        if (getIntent() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                //get movie object from intent
                movie = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

                //fetch movie id
                movieId = movie.getMovieId();

                //set the fields for the movie
                title.setText(movie.getTitle());
                year.setText(movie.getReleaseDate().substring(0, 4));

                //define default image in case the result is null
                String posterImageUrl = movie.getImageUrl() != null ?
                        IMAGE_URL_SIZE.concat(movie.getImageUrl()) : "";
                Glide.with(this)
                        .load(posterImageUrl)
                        .into(posterImage);
                //define default image in case the result is null
                String backdropImageUrl = movie.getBackdropImageUrl() != null ?
                        IMAGE_URL_SIZE.concat(movie.getBackdropImageUrl()) : "";
                Glide.with(this)
                        .load(backdropImageUrl)
                        .into(backdropImage);

                //get genre names based on genre codes
                List<Integer> genreId = new ArrayList<>(movie.getGenres());
                int count = 0;
                for (int id : genreId) {
                    genre.append(genreMap.get(id));
                    count++;
                    if (count < genreId.size()) {
                        genre.append(", ");
                    }
                }

            }
        }

        if (movieId != 0) {
            MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);
            Call<DetailResults> call = client.getMovieDetails(movieId, API_KEY);

            call.enqueue(new Callback<DetailResults>() {
                @Override
                public void onResponse(@NonNull Call<DetailResults> call, @NonNull Response<DetailResults> response) {
                    if (response.body() == null) {
                        return;
                    }
                    //fetch movie duration from details api call
                    int runtime = response.body().getDuration();
                    //display run time in h:m format
                    duration.setText(Utils.formatDuration(MovieDetailsActivity.this, runtime));
                }

                @Override
                public void onFailure(@NonNull Call<DetailResults> call, @NonNull Throwable t) {
                    Toast.makeText(MovieDetailsActivity.this, R.string.error_movie_duration, Toast.LENGTH_SHORT).show();
                }
            });
        }

        //set up viewpager to display movie info, cast and reviews
        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(), movie));
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * handles back navigation on toolbar
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        //close the current activity
        finish();
        return true;
    }
}
