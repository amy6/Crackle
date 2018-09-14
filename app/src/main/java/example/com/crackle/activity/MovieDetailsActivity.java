package example.com.crackle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieImageAdapter;
import example.com.crackle.model.Image;
import example.com.crackle.model.ImageResults;
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
import static example.com.crackle.utils.Constants.PLAYSTORE_BASE_URI;
import static example.com.crackle.utils.Constants.PLAYSTORE_QUERY_PARAMETER_CATEGORY;
import static example.com.crackle.utils.Constants.PLAYSTORE_QUERY_VALUE_CATEGORY;
import static example.com.crackle.utils.Constants.TMDB_MOVIE_BASE_URI;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.backdrop_image_viewpager)
    ViewPager backdropImageViewPager;
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
    @BindView(R.id.favorites)
    FloatingActionButton favorites;

    private Movie movie;
    private int movieId;
    private MovieApiClient client;
    private List<Image> images;

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

        //set up Retrofit client
        client = MovieApiService.getClient().create(MovieApiClient.class);

        //initialize image array list
        images = new ArrayList<>();

        //set up click listener for favorites button
        favorites.setOnClickListener(this);

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
                /*Glide.with(this)
                        .load(backdropImageUrl)
                        .into(backdropImageViewPager);*/

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
            Call<DetailResults> detailResultsCall = client.getMovieDetails(movieId, API_KEY);

            detailResultsCall.enqueue(new Callback<DetailResults>() {
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

            Call<ImageResults> imageResultsCall = client.getMovieImages(movieId, API_KEY);

            imageResultsCall.enqueue(new Callback<ImageResults>() {
                @Override
                public void onResponse(@NonNull Call<ImageResults> call, @NonNull Response<ImageResults> response) {
                    if (response.body() == null || response.body().getBackdrops() == null || response.body().getBackdrops().size() == 0) {
                        return;
                    }

                    //add fetched images to the list
                    images.addAll(response.body().getBackdrops());
                    backdropImageViewPager.setAdapter(new MovieImageAdapter(MovieDetailsActivity.this, images));
                }


                @Override
                public void onFailure(@NonNull Call<ImageResults> call, @NonNull Throwable t) {
                    Toast.makeText(MovieDetailsActivity.this, R.string.error_movie_images, Toast.LENGTH_SHORT).show();
                }
            });
        }

        //set up viewpager to display movie info, cast and reviews
        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(), movie));
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * inflate menu options
     *
     * @param menu reference to menu object
     * @return boolean flag indicating whether the menu create action was handled successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    /**
     * handle selection of menu options
     *
     * @param item reference to the menu item clicked
     * @return boolean flag indicating whether the menu click action was handled successfully
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {

            //start an intent to share movie TMDB profile url
            case R.id.action_share:
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                //set text content
                intent.putExtra(Intent.EXTRA_TEXT,
                        String.format(getString(R.string.movie_share_intent_text),
                                movie.getTitle(),
                                TMDB_MOVIE_BASE_URI,
                                String.valueOf(movie.getMovieId())));
                //set custom chooser title
                intent = Intent.createChooser(intent,
                        String.format(getString(R.string.movie_share_intent_chooser_text),
                                movie.getTitle()));
                break;

            //intent to open movie in PlayStore "movies" category
            case R.id.action_playstore:
                intent.setAction(Intent.ACTION_VIEW);
                //set PlayStore category to movies
                intent.setData(Uri.parse(PLAYSTORE_BASE_URI +
                        movie.getTitle()).buildUpon()
                        .appendQueryParameter(PLAYSTORE_QUERY_PARAMETER_CATEGORY,
                                PLAYSTORE_QUERY_VALUE_CATEGORY).build());
                break;

            //intent to open movie profile on TMDB
            case R.id.action_tmdb:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(TMDB_MOVIE_BASE_URI + movie.getMovieId()));
                break;
        }

        //verify if the intent can be opened with a suitable app on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.error_movie_intent, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * handles back navigation on toolbar
     *
     * @return boolean flag indicating whether the call was successfully handled
     */
    @Override
    public boolean onSupportNavigateUp() {
        //close the current activity
        finish();
        return true;
    }

    /**
     * handle click events on views
     *
     * @param view reference to the view that receives the click event
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorites:
                //TODO: Implement favorites functionality
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
