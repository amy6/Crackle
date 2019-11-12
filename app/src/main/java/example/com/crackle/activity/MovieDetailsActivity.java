package example.com.crackle.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieFragmentPagerAdapter;
import example.com.crackle.adapter.MovieImageAdapter;
import example.com.crackle.database.MovieDatabase;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.Certification;
import example.com.crackle.model.Image;
import example.com.crackle.model.Movie;
import example.com.crackle.utils.AppExecutors;
import example.com.crackle.utils.Constants;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.backdrop_image_viewpager)
    ViewPager backdropImageViewPager;
    @BindView(R.id.viewpager_indicator)
    TabLayout viewPagerIndicator;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.genre)
    TextView genre;
    @BindView(R.id.content_rating)
    TextView contentRating;
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
    private List<Image> images;
    private List<Certification> certifications;

    private MovieDetailsActivityViewModel viewModel;
    private Toast toast;
    private String youtubeTrailerLink;
    private boolean isFavorite;

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

        //postpone shared element transition till the image is loaded from the API
        supportPostponeEnterTransition();

        //make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //set up Retrofit client
        MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);

        //get reference to view model
        viewModel = ViewModelProviders.of(this).get(MovieDetailsActivityViewModel.class);

        //initialize data sets
        images = new ArrayList<>();
        certifications = new ArrayList<>();

        //set up click listener for favorites button
        favorites.setOnClickListener(this);

        //display toolbar title only when collapsed
        handleCollapsedToolbarTitle();

        if (getIntent() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                //get movie object from intent
                movie = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
                movieId = movie.getMovieId();
                AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
                    isFavorite = viewModel.isFavorite(movieId);
                    if (isFavorite) {
                        movie = MovieDatabase.getInstance(this).movieDao().getMovie(movieId);
                        runOnUiThread(() -> favorites.setImageResource(R.drawable.ic_favorite));
                    } else {
                        runOnUiThread(() -> favorites.setImageResource(R.drawable.ic_favorite_border));
                    }
                });
            }
        }

        //set up Retrofit call to get movie details
        fetchMovieDetails(client);
    }

    /**
     * invokes TMDB API to get movie details
     *
     * @param client reference to Retrofit client
     */
    private void fetchMovieDetails(MovieApiClient client) {

        if (movieId != 0) {
            Call<Movie> detailResultsCall = client.getMovieDetails(movieId, Constants.API_KEY);

            //get the list of genres for the movie
            fetchMovieGenre();

            detailResultsCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                    if (response.body() == null) {
                        return;
                    }

                    //fetch movie duration from details api call
                    int runtime = response.body().getDuration();
                    //display run time in h:m format
                    duration.setText(Utils.formatDuration(MovieDetailsActivity.this, runtime));

                    //set movie homepage
                    if (response.body().getHomepage() != null && !TextUtils.isEmpty(response.body().getHomepage())) {
                        movie.setHomepage(response.body().getHomepage());
                    }

                    //set movie title
                    if (response.body().getOriginalTitle() != null && !TextUtils.isEmpty(response.body().getOriginalTitle())) {
                        movie.setOriginalTitle(response.body().getOriginalTitle());
                    }

                    //set up viewpager to display movie info, cast and reviews
                    viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(), movie));
                    tabLayout.setupWithViewPager(viewPager);
                }

                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                    if (movie != null) {
                        Log.d(Constants.LOG_TAG, "Movie already set by favorites");
                    } else {
                        displayToastMessage(R.string.error_movie_details);
                    }
                }
            });

            Call<Movie> extraDetailResultsCall = client.getMovieDetails(movieId, Constants.API_KEY, Constants.APPEND_TO_RESPONSE_VALUE);

            extraDetailResultsCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                    if (response.body() == null) {
                        return;
                    }

                    //fetch backdrop images
                    if (response.body().getImageResults() != null && response.body().getImageResults().getBackdrops() != null && response.body().getImageResults().getBackdrops().size() > 0) {
                        //add fetched images to the list
                        if (response.body().getImageResults().getBackdrops().size() > 8) {
                            for (int i = 0; i < 8; i++) {
                                images.add(response.body().getImageResults().getBackdrops().get(i));
                            }
                        } else {
                            images.addAll(response.body().getImageResults().getBackdrops());
                        }
                    }

                    //fetch movie trailers
                    if (response.body().getVideoResults() != null && response.body().getVideoResults().getVideos() != null && response.body().getVideoResults().getVideos().size() > 0) {
                        movie.setVideoResults(response.body().getVideoResults());
                        youtubeTrailerLink = movie.getVideoResults().getVideos().get(0).getTitle();
                    }

                    if (response.body().getCertificationResults() != null && response.body().getCertificationResults().getCertificationList() != null && response.body().getCertificationResults().getCertificationList().size() > 0) {
                        certifications = response.body().getCertificationResults().getCertificationList();
                        for (Certification certification : certifications) {
                            if (certification.getIso().equals("IN")) {
                                if (!TextUtils.isEmpty(certification.getCertification())) {
                                    contentRating.setText(certification.getCertification());
                                }
                            }
                        }
                    }

                    //set up viewpager for backdrop image list
                    viewPagerIndicator.setupWithViewPager(backdropImageViewPager);
                    MovieImageAdapter adapter = new MovieImageAdapter(MovieDetailsActivity.this, images);
                    backdropImageViewPager.setAdapter(adapter);


                }

                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                    if (movie.isFavorite() && movie != null) {
                        Log.d(Constants.LOG_TAG, "Movie already set by favorites");
                        duration.setText(Utils.formatDuration(MovieDetailsActivity.this, movie.getDuration()));
                        movie.setUserRating(movie.getUserRating());
                        movie.setHomepage(movie.getHomepage());
                        movie.setOriginalTitle(movie.getOriginalTitle());

                        //set up viewpager to display movie info, cast and reviews
                        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(), movie));
                        tabLayout.setupWithViewPager(viewPager);

                        ArrayList<Image> imageUrl = new ArrayList<>();
                        Image backdropImage = new Image(movie.getBackdropImageUrl());
                        imageUrl.add(backdropImage);
                        images.addAll(imageUrl);

                        //set up viewpager for backdrop image list
                        viewPagerIndicator.setupWithViewPager(backdropImageViewPager);
                        MovieImageAdapter adapter = new MovieImageAdapter(MovieDetailsActivity.this, images);
                        backdropImageViewPager.setAdapter(adapter);
                    } else {
                        displayToastMessage(R.string.error_movie_details);
                    }
                }
            });
        }
    }

    /**
     * gets the applicable genre category for the movie based on genre code
     */
    private void fetchMovieGenre() {
        //get the list of all genre code and corresponding names from local json file
        SparseArray<String> genreMap = Utils.fetchAllGenres(this);

        //set the fields for the movie
        title.setText(movie.getTitle());
        year.setText(movie.getReleaseDate().substring(0, 4));

        //define default image in case the result is null
        String posterImageUrl = movie.getImageUrl() != null ?
                Constants.IMAGE_URL_SIZE.concat(movie.getImageUrl()) : "";
        Glide.with(this)
                .setDefaultRequestOptions(Utils.setupGlide(Constants.BACKDROP_IMG))
                .load(posterImageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        MovieDetailsActivity.this.supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(posterImage);

        //get genre names based on genre codes
        if (movie.getGenres() != null) {
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

    /**
     * sets the title on the toolbar only if the toolbar is collapsed
     */
    private void handleCollapsedToolbarTitle() {
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
                String intentContent = String.format(getString(R.string.movie_share_intent_text),
                        movie.getTitle(),
                        Constants.TMDB_MOVIE_BASE_URI,
                        String.valueOf(movie.getMovieId()));
                //add youtube trailer link
                if (youtubeTrailerLink != null && !TextUtils.isEmpty(youtubeTrailerLink)) {
                    intentContent = intentContent.concat(String.format(getString(R.string.youtube_link_text), youtubeTrailerLink));
                }
                intentContent = intentContent.concat(getString(R.string.crackle_text));
                intent.putExtra(Intent.EXTRA_TEXT, intentContent);
                //set custom chooser title
                intent = Intent.createChooser(intent,
                        String.format(getString(R.string.movie_share_intent_chooser_text),
                                movie.getTitle()));
                break;

            //intent to open movie in PlayStore "movies" category
            case R.id.action_playstore:
                intent.setAction(Intent.ACTION_VIEW);
                //set PlayStore category to movies
                intent.setData(Uri.parse(Constants.PLAYSTORE_BASE_URI +
                        movie.getTitle()).buildUpon()
                        .appendQueryParameter(Constants.PLAYSTORE_QUERY_PARAMETER_CATEGORY,
                                Constants.PLAYSTORE_QUERY_VALUE_CATEGORY).build());
                break;

            //intent to open movie profile on TMDB
            case R.id.action_tmdb:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.TMDB_MOVIE_BASE_URI + movie.getMovieId()));
                break;

            //close details screen on click of back
            case android.R.id.home:
                finish();
                return true;
        }

        //verify if the intent can be opened with a suitable app on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            displayToastMessage(R.string.error_movie_intent);
        }
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
                //set animation on click for favorite button
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake);
                favorites.startAnimation(anim);

                AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
                    //get saved state of the movie from the database
                    boolean isFavorite = viewModel.isFavorite(movieId);
                    if (isFavorite) {
                        //handle removing the movie from favorites db
                        viewModel.removeMovieFromFavorites(movie);
                        runOnUiThread(() -> {
                            displayToastMessage(R.string.favorites_removed);
                            favorites.setImageResource(R.drawable.ic_favorite_border);
                        });
                    } else {
                        //handle adding the movie to the favorites db
                        viewModel.addMovieToFavorites(movie);
                        runOnUiThread(() -> {
                            displayToastMessage(R.string.favorites_added);
                            favorites.setImageResource(R.drawable.ic_favorite);
                        });
                    }
                    //update saved state
                    viewModel.updateMovieFavorite(movieId, !isFavorite);
                });
                break;
        }
    }

    /**
     * display message in toast
     *
     * @param messageId string resource id for the message to be displayed
     */
    private void displayToastMessage(int messageId) {
        cancelToast();

        toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        //dismiss any outstanding toast messages
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelToast();
    }
}
