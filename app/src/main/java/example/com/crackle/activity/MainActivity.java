package example.com.crackle.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.model.Movie;
import example.com.crackle.adapter.MovieAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.model.MovieResults;
import example.com.crackle.listener.OnLoadMoreListener;
import example.com.crackle.R;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.DEFAULT_OPTION_CHECKED;
import static example.com.crackle.utils.Constants.LOG_TAG;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.errorLayout)
    ConstraintLayout errorLayout;
    @BindView(R.id.errorImage)
    ImageView errorImage;
    @BindView(R.id.errorText)
    TextView errorText;
    @BindView(R.id.errorButton)
    Button errorButton;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private List<Movie> movies;
    private MovieAdapter movieAdapter;
    private MovieApiClient client;
    private Call<MovieResults> call;

    private int mostPopularMoviesStartPage = 1;
    private int topRatedMoviesStartPage = 1;

    private MenuItem mostPopularMenuItem;
    private MenuItem topRatedMenuItem;
    private boolean defaultOptionChecked = true;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //resolve references to view
        ButterKnife.bind(this);

        movies = new ArrayList<>();

        recyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this, movies, recyclerView);
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.setOnLoadMoreListener(this);

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        refreshLayout.setOnRefreshListener(this);

        client = MovieApiService.getClient().create(MovieApiClient.class);

        errorButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DEFAULT_OPTION_CHECKED)) {
                defaultOptionChecked = savedInstanceState.getBoolean(DEFAULT_OPTION_CHECKED);
            }
        }

        if (defaultOptionChecked) {
            getPopularMovies();
        } else {
            getTopRatedMovies();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        mostPopularMenuItem = menu.findItem(R.id.sort_most_popular);
        topRatedMenuItem = menu.findItem(R.id.sort_top_rated);

        //default option to be checked
        mostPopularMenuItem.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                if (item.isChecked()) {
                    return false;
                } else {
                    item.setChecked(true);
                    Log.d(LOG_TAG, "Clearing popular movies, resetting start page");
                    movieAdapter.setOnLoadMoreListener(null);
                    movies.clear();
                    movieAdapter.notifyDataSetChanged();
                    mostPopularMoviesStartPage = 1;
                    //api call to fetch and display popular movies
                    getPopularMovies();
                }
                break;
            case R.id.sort_top_rated:
                if (item.isChecked()) {
                    return false;
                } else {
                    item.setChecked(true);
                    Log.d(LOG_TAG, "Clearing top rated movies, resetting start page");
                    movieAdapter.setOnLoadMoreListener(null);
                    movies.clear();
                    movieAdapter.notifyDataSetChanged();
                    topRatedMoviesStartPage = 1;
                    //api call to fetch and display top rated movies
                    getTopRatedMovies();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (defaultOptionChecked) {
            mostPopularMenuItem.setChecked(true);
        } else {
            topRatedMenuItem.setChecked(true);
        }
        return true;
    }

    private void getPopularMovies() {

        if (!isInternetConnected()) {
            return;
        }

        defaultOptionChecked = true;

        call = client.getPopularMovies(API_KEY, mostPopularMoviesStartPage);
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(@NonNull Call<MovieResults> call, @NonNull Response<MovieResults> response) {
                progressBar.setVisibility(View.VISIBLE);
                movieAdapter.removeLoader(null);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);

                if (response.body() == null || response.body().getMovies() == null || response.body().getMovies().size() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results);
                    return;
                }

                movies.addAll(response.body().getMovies());
                movieAdapter.notifyDataSetChanged();

                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                movieAdapter.setLoading(false);
                mostPopularMoviesStartPage++;

                movieAdapter.setOnLoadMoreListener(MainActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResults> call, @NonNull Throwable t) {
                refreshLayout.setRefreshing(false);

                errorLayout.setVisibility(View.VISIBLE);
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.error_no_results);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void getTopRatedMovies() {

        if (!isInternetConnected()) {
            return;
        }

        defaultOptionChecked = false;

        call = client.getTopRatedMovies(API_KEY, topRatedMoviesStartPage);
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(@NonNull Call<MovieResults> call, @NonNull Response<MovieResults> response) {
                progressBar.setVisibility(View.VISIBLE);
                movieAdapter.removeLoader(null);

                if (response.body() == null || response.body().getMovies() == null || response.body().getMovies().size() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results);
                    return;
                }

                movies.addAll(response.body().getMovies());
                movieAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);

                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                movieAdapter.setLoading(false);
                topRatedMoviesStartPage++;
                refreshLayout.setEnabled(true);

                movieAdapter.setOnLoadMoreListener(MainActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResults> call, @NonNull Throwable t) {
                refreshLayout.setRefreshing(false);

                errorLayout.setVisibility(View.VISIBLE);
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.error_no_results);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateEmptyStateViews(int errorImage, int errorText, int errorTextDrawable, int errorButtonText) {
        this.errorImage.setImageResource(errorImage);
        this.errorText.setText(errorText);
        this.errorText.setCompoundDrawablesWithIntrinsicBounds(0, errorTextDrawable, 0, 0);
        errorButton.setText(errorButtonText);
    }

    private boolean isInternetConnected() {
        if (Utils.checkInternetConnection(this)) {
            return true;
        } else {
            refreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection, R.drawable.ic_cloud_off, R.string.error_try_again);
            return false;
        }
    }

    @Override
    public void onRefresh() {

        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        movieAdapter.setOnLoadMoreListener(null);
        movieAdapter.setLoading(false);

        mostPopularMoviesStartPage = 1;
        topRatedMoviesStartPage = 1;

        movies.clear();
        movieAdapter.notifyDataSetChanged();

        if (mostPopularMenuItem.isChecked()) {
            getPopularMovies();
        } else {
            getTopRatedMovies();
        }
    }

    @Override
    public void onLoadMore() {
        //handle pagination

        refreshLayout.setEnabled(false);
        movieAdapter.addLoader(null);

        if (mostPopularMenuItem.isChecked()) {
            getPopularMovies();
        } else {
            getTopRatedMovies();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.errorButton:
                if (((Button) view).getText().toString().trim().equalsIgnoreCase(getString(R.string.error_try_again))) {
                    displayToast("Ok! Checking..");
                    progressBar.setVisibility(View.VISIBLE);
                    onRefresh();
                } else {
                    finish();
                }
                break;
        }
    }

    private void displayToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(DEFAULT_OPTION_CHECKED, defaultOptionChecked);
        super.onSaveInstanceState(outState);
    }
}
