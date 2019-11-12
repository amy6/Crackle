package example.com.crackle.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.listener.OnLoadMoreListener;
import example.com.crackle.model.Movie;
import example.com.crackle.model.MovieResults;
import example.com.crackle.utils.Constants;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnLoadMoreListener, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.errorLayout)
    ScrollView errorLayout;
    @BindView(R.id.errorImage)
    ImageView errorImage;
    @BindView(R.id.errorText)
    TextView errorText;
    @BindView(R.id.errorButton)
    Button errorButton;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;
    private MainActivityViewModel viewModel;

    private MovieApiClient client;
    private Call<MovieResults> call;

    private int mostPopularMoviesStartPage = 1;
    private int topRatedMoviesStartPage = 1;
    private MenuItem mostPopularMenuItem;
    private MenuItem topRatedMenuItem;
    private MenuItem favoritesMenuItem;
    private boolean mostPopularOptionChecked = true;
    private boolean topRatedOptionChecked = false;

    private Toast toast;
    private boolean fromErrorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //resolve references to views
        ButterKnife.bind(this);

        //initialize movies array
        movies = new ArrayList<>();

        //set up RecyclerView - define caching properties and default animator
        Utils.INSTANCE.setupRecyclerView(this, recyclerView, Constants.GRID_LAYOUT);

        //set up adapter
        movieAdapter = new MovieAdapter(this, movies, recyclerView);
        recyclerView.setAdapter(movieAdapter);

        //initialize view model
        viewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
        //set up observer to get notified when movies are added/removed from favorites database
        viewModel.getFavoriteMovies().observe(this, favorites -> {
            if (favorites != null && !mostPopularOptionChecked && !topRatedOptionChecked) {
                if (movies == null) {
                    movies = new ArrayList<>();
                } else {
                    movieAdapter.clear();
                }
                movieAdapter.addAll(favorites);

                if (movies.size() == 0) {
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                            R.drawable.ic_error_outline, R.string.browse_movies);
                }
            }
        });

        //register refresh layout listener
        refreshLayout.setOnRefreshListener(this);
        //customize refresh layout color scheme
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));

        //get reference to TMDB API client
        client = MovieApiService.getClient().create(MovieApiClient.class);

        //set up button click listener for error/empty state views
        errorButton.setOnClickListener(this);

        //restore any previously saved data on activity state changed
        if (savedInstanceState != null) {
            //restore movies list data
            if (savedInstanceState.containsKey(Constants.MOVIES_LIST)) {
                progressBar.setVisibility(View.GONE);
                movies = savedInstanceState.getParcelableArrayList(Constants.MOVIES_LIST);
                movieAdapter.setData(movies);
            }
            //restore page numbers for paginated data
            if (savedInstanceState.containsKey(Constants.MOST_POPULAR_START_PAGE)) {
                mostPopularMoviesStartPage = savedInstanceState.getInt(Constants.MOST_POPULAR_START_PAGE);
            }
            if (savedInstanceState.containsKey(Constants.TOP_RATED_START_PAGE)) {
                topRatedMoviesStartPage = savedInstanceState.getInt(Constants.TOP_RATED_START_PAGE);
            }
            //get the currently selected menu item
            if (savedInstanceState.containsKey(Constants.MOST_POPULAR_OPTION_CHECKED)) {
                mostPopularOptionChecked = savedInstanceState.getBoolean(Constants.MOST_POPULAR_OPTION_CHECKED);
            }
            if (savedInstanceState.containsKey(Constants.TOP_RATED_OPTION_CHECKED)) {
                topRatedOptionChecked = savedInstanceState.getBoolean(Constants.TOP_RATED_OPTION_CHECKED);
            }
            //get recycler view layout manager state to restore scroll position
            if (savedInstanceState.containsKey(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE)) {
                recyclerView.getLayoutManager().onRestoreInstanceState(
                        savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE));
            }
        }

        //enable pagination only if the list data is fetched from network (most popular/top rated)
        //rather than favorites which is fetched from DB
        if (mostPopularOptionChecked || topRatedOptionChecked) {
            //set up pagination listener
            movieAdapter.setOnLoadMoreListener(this);
        }

        //call API based on the selected sort order - popular movies being default
        //do not explicitly invoke the API on orientation change, wait for the list to be scrolled to the bottom
        if (savedInstanceState == null) {
            if (mostPopularOptionChecked) {
                getPopularMovies();
            } else if (topRatedOptionChecked) {
                getTopRatedMovies();
            }
        }

    }

    /**
     * inflates the menu on the action bar
     *
     * @param menu reference to the menu in which to inflate the options
     * @return boolean flag indicating whether inflating was successfully handled
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //get references to the movie sort order menu items
        mostPopularMenuItem = menu.findItem(R.id.sort_most_popular);
        topRatedMenuItem = menu.findItem(R.id.sort_top_rated);
        favoritesMenuItem = menu.findItem(R.id.sort_favorites);

        return true;
    }

    /**
     * handle selection of menu items
     *
     * @param item reference to the selected menu item
     * @return boolean flag indicating whether menu item selection action was successfully handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cancelToast();
        //verify the selected menu option
        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                //if the item is already selected, exit early
                if (item.isChecked()) {
                    return false;
                } else {

                    errorLayout.setVisibility(View.GONE);
                    //display progress indicator
                    progressBar.setVisibility(View.VISIBLE);
                    //set the item as selected
                    item.setChecked(true);
                    //unregister pagination listener before invoking the API to avoid unexpected behaviours
                    movieAdapter.setOnLoadMoreListener(null);
                    //clear the list, notify the adapter
                    movieAdapter.clear();

                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = true;
                    topRatedOptionChecked = false;

                    //reset the page number to load from the beginning
                    mostPopularMoviesStartPage = 1;
                    //display popular movies
                    getPopularMovies();
                }
                break;
            case R.id.sort_top_rated:
                //if the item is already selected, exit early
                if (item.isChecked()) {
                    return false;
                } else {

                    errorLayout.setVisibility(View.GONE);
                    //display progress indicator
                    progressBar.setVisibility(View.VISIBLE);
                    //set the item as selected
                    item.setChecked(true);
                    //unregister pagination listener before invoking the API to avoid unexpected behaviours
                    movieAdapter.setOnLoadMoreListener(null);
                    //clear the list, notify the adapter
                    movieAdapter.clear();

                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = false;
                    topRatedOptionChecked = true;

                    //reset the page number to load from the beginning
                    topRatedMoviesStartPage = 1;
                    //display top rated movies
                    getTopRatedMovies();
                }
                break;
            case R.id.sort_favorites:
                //if the item is already selected, exit early
                if (item.isChecked()) {
                    return false;
                } else {
                    //ignore internet connectivity check for displaying favorites
                    if (Utils.INSTANCE.checkInternetConnection(this)) {
                        errorLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    //set the item as selected
                    item.setChecked(true);
                    movieAdapter.setOnLoadMoreListener(null);
                    //clear the list, notify the adapter
                    movieAdapter.clear();
                    //disable refresh layout
                    refreshLayout.setEnabled(false);

                    //reset scroll position to 0
                    recyclerView.scrollToPosition(0);

                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = false;
                    topRatedOptionChecked = false;

                    //hide loading indicator
                    progressBar.setVisibility(View.GONE);
                    //get the list of favorite movies from the database
                    List<Movie> favoriteMovies = viewModel.getFavoriteMovies().getValue();
                    if (favoriteMovies != null && favoriteMovies.size() > 0) {
                        //update the list, notify the adapter
                        movieAdapter.addAll(favoriteMovies);
                    } else {
                        //display empty state views if there are no favorites saved
                        updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                                R.drawable.ic_error_outline, R.string.browse_movies);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * called before displaying the menu to the user
     *
     * @param menu reference to the menu item that can be modified
     * @return boolean flag indicating whether any required modifications were successfully handled
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //set the checked item based on the boolean flag saved along activity lifecycle
        if (mostPopularOptionChecked) {
            mostPopularMenuItem.setChecked(true);
        } else if (topRatedOptionChecked) {
            topRatedMenuItem.setChecked(true);
        } else {
            favoritesMenuItem.setChecked(true);
        }
        return true;
    }

    /**
     * invoke TMDB API To fetch movies sorted by popularity
     */
    private void getPopularMovies() {

        //exit early if internet is not connected
        if (Utils.INSTANCE.checkInternetConnection(this)) {
            updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection,
                    R.drawable.ic_cloud_off, R.string.error_try_again);
            return;
        }

        //define the call object that wraps the API response
        call = client.getPopularMovies(Constants.API_KEY, mostPopularMoviesStartPage);
        //invoke the call asynchronously
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(@NonNull Call<MovieResults> call, @NonNull Response<MovieResults> response) {

                //remove pagination loading indicator
                movieAdapter.removeLoader(null);
                //hide refresh layout progress indicator
                refreshLayout.setRefreshing(false);
                //enable refresh action
                refreshLayout.setEnabled(true);

                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body().getMovies() == null || response.body().getMovies().size() == 0) {
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results);
                    return;
                }

                //update data set, notify the adapter
                movieAdapter.addAll(response.body().getMovies());

                //hide progress indicator and empty state views
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                //display recycler view
                recyclerView.setVisibility(View.VISIBLE);

                //notify the adapter that a new page data load is complete
                movieAdapter.setLoading(false);
                //increment the page number
                mostPopularMoviesStartPage++;

                //enable pagination in case user wants to load the next page
                movieAdapter.setOnLoadMoreListener(MainActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResults> call, @NonNull Throwable t) {
                //display error messages on failure
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.browse_movies);
            }
        });
    }


    /**
     * invoke TMDB API To fetch movies sorted by ratings
     */
    private void getTopRatedMovies() {

        //exit early if internet is not connected
        if (Utils.INSTANCE.checkInternetConnection(this)) {
            updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection, R.drawable.ic_cloud_off, R.string.error_try_again);
            return;
        }

        //define the call object that wraps the API response
        call = client.getTopRatedMovies(Constants.API_KEY, topRatedMoviesStartPage);
        //invoke the call asynchronously
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(@NonNull Call<MovieResults> call, @NonNull Response<MovieResults> response) {

                //remove pagination loading indicator
                movieAdapter.removeLoader(null);
                //hide refresh layout progress indicator
                refreshLayout.setRefreshing(false);
                //enable refresh action
                refreshLayout.setEnabled(true);

                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body().getMovies() == null || response.body().getMovies().size() == 0) {
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results);
                    return;
                }

                //update data set, notify the adapter
                movieAdapter.addAll(response.body().getMovies());

                //hide progress indicator and empty state views
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                //display recycler view
                recyclerView.setVisibility(View.VISIBLE);

                //notify the adapter that a new page data load is complete
                movieAdapter.setLoading(false);
                //increment the page number
                topRatedMoviesStartPage++;

                //enable pagination in case user wants to load the next page
                movieAdapter.setOnLoadMoreListener(MainActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResults> call, @NonNull Throwable t) {
                //display error messages on failure
                updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.error_no_results);
            }
        });
    }

    /**
     * called on API call failure or internet connection failure
     *
     * @param errorImage        resource ID of the image to be displayed indicating the error
     * @param errorText         user understandable error message
     * @param errorTextDrawable icon indicating the error
     * @param errorButtonText   text for the error button, prompting the user for an action
     */
    private void updateEmptyStateViews(int errorImage, int errorText, int errorTextDrawable, int errorButtonText) {
        //disable refresh progress indicator
        refreshLayout.setRefreshing(false);

        //display the error layout
        errorLayout.setVisibility(View.VISIBLE);
        //hide progress indicator and RecyclerView
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        //update error images/text according to the error
        this.errorImage.setImageResource(errorImage);
        this.errorText.setText(errorText);
        this.errorText.setCompoundDrawablesWithIntrinsicBounds(0, errorTextDrawable, 0, 0);
        errorButton.setText(errorButtonText);
    }

    /**
     * called on swipe to refresh
     */
    @Override
    public void onRefresh() {

        //hide all the views
        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        //hide progress indicator when refresh indicator is displayed on swipe
        //rather than user clicking on "Try Again" when the internet connection is down
        if (!fromErrorButton) {
            progressBar.setVisibility(View.GONE);
        } else {
            displayToast(getString(R.string.trying_again_alert));
            fromErrorButton = false;
        }

        //disable pagination to avoid unexpected results
        movieAdapter.setOnLoadMoreListener(null);
        movieAdapter.setLoading(false);

        //reset page counters
        mostPopularMoviesStartPage = 1;
        topRatedMoviesStartPage = 1;

        //clear the list, notify the adapter
        movieAdapter.clear();

        //invoke API call based on selected sort order
        if (mostPopularMenuItem.isChecked()) {
            getPopularMovies();
        } else if (topRatedMenuItem.isChecked()) {
            getTopRatedMovies();
        }
    }

    /**
     * invoked on loading a new page from the API results for pagination
     */
    @Override
    public void onLoadMore() {

        //disable swipe to refresh to avoid unexpected results
        refreshLayout.setEnabled(false);

        //add a progress loading indicator at the bottom of the RecyclerView
        movieAdapter.addLoader(null);

        //invoke API call based on selected sort order
        if (mostPopularMenuItem.isChecked()) {
            getPopularMovies();
        } else if (topRatedMenuItem.isChecked()) {
            getTopRatedMovies();
        }
    }

    /**
     * handle view clicks
     *
     * @param view reference to the view that receives the click event
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.errorButton:
                if (((Button) view).getText().toString().trim().equalsIgnoreCase(getString(R.string.error_try_again))) {
                    //call refresh action on clicking "Try Again"
                    progressBar.setVisibility(View.VISIBLE);
                    fromErrorButton = true;
                    onRefresh();
                } else if (((Button) view).getText().toString().trim().equalsIgnoreCase(getString(R.string.browse_movies))) {
                    //display movies on clicking "Browse Movies"
                    errorLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    //reset start page
                    mostPopularMoviesStartPage = 1;
                    topRatedMoviesStartPage = 1;

                    //display popular movies category by default
                    mostPopularMenuItem.setChecked(true);
                    getPopularMovies();
                } else {
                    //close the app on server error
                    finish();
                }
                break;
        }
    }

    /**
     * displays the specified message as a toast to alert the user
     *
     * @param message message to be displayed
     */
    private void displayToast(String message) {
        cancelToast();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        if (toast != null) {
            //cancel any outstanding toasts
            toast.cancel();
        }
    }

    /**
     * called before activity gets destroyed
     *
     * @param outState bundle object that can hold any state that we want to save
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save state of menu items
        outState.putBoolean(Constants.MOST_POPULAR_OPTION_CHECKED, mostPopularOptionChecked);
        outState.putBoolean(Constants.TOP_RATED_OPTION_CHECKED, topRatedOptionChecked);

        //save movie array list
        outState.putParcelableArrayList(Constants.MOVIES_LIST, movies);

        //save start page numbers for all categories
        outState.putInt(Constants.MOST_POPULAR_START_PAGE, mostPopularMoviesStartPage);
        outState.putInt(Constants.TOP_RATED_START_PAGE, topRatedMoviesStartPage);

        //save recycler view scroll state
        outState.putParcelable(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelToast();
    }
}
