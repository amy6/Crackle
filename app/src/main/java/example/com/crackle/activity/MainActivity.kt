package example.com.crackle.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import example.com.crackle.R
import example.com.crackle.adapter.MovieAdapter
import example.com.crackle.listener.OnLoadMoreListener
import example.com.crackle.model.Movie
import example.com.crackle.model.MovieResults
import example.com.crackle.utils.Constants
import example.com.crackle.utils.MovieApiService.client
import example.com.crackle.utils.Utils.checkInternetConnection
import example.com.crackle.utils.Utils.setupRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnRefreshListener, OnLoadMoreListener, OnClickListener {
    @JvmField
    @BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null
    @JvmField
    @BindView(R.id.errorLayout)
    var errorLayout: ScrollView? = null
    @JvmField
    @BindView(R.id.errorImage)
    var errorImage: ImageView? = null
    @JvmField
    @BindView(R.id.errorText)
    var errorText: TextView? = null
    @JvmField
    @BindView(R.id.errorButton)
    var errorButton: Button? = null
    @JvmField
    @BindView(R.id.swipeRefreshLayout)
    var refreshLayout: SwipeRefreshLayout? = null
    private var movies: ArrayList<Movie?>? = null
    private var movieAdapter: MovieAdapter? = null
    private var viewModel: MainActivityViewModel? = null
    private var call: Call<MovieResults>? = null
    private var mostPopularMoviesStartPage = 1
    private var topRatedMoviesStartPage = 1
    private var mostPopularMenuItem: MenuItem? = null
    private var topRatedMenuItem: MenuItem? = null
    private var favoritesMenuItem: MenuItem? = null
    private var mostPopularOptionChecked = true
    private var topRatedOptionChecked = false
    private var toast: Toast? = null
    private var fromErrorButton = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //resolve references to views
        ButterKnife.bind(this)
        //initialize movies array
        movies = ArrayList()
        //set up RecyclerView - define caching properties and default animator
        setupRecyclerView(this, recyclerView!!, Constants.GRID_LAYOUT)
        //set up adapter
        movieAdapter = MovieAdapter(this, movies, recyclerView!!)
        recyclerView!!.adapter = movieAdapter
        //initialize view model
        viewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel::class.java)
        //set up observer to get notified when movies are added/removed from favorites database
        viewModel!!.favoriteMovies.observe(this, Observer<List<Movie?>> { favorites: List<Movie?>? ->
            if (favorites != null && !mostPopularOptionChecked && !topRatedOptionChecked) {
                if (movies == null) {
                    movies = ArrayList()
                } else {
                    movieAdapter!!.clear()
                }
                movieAdapter!!.addAll(favorites)
                if (movies!!.size == 0) {
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                            R.drawable.ic_error_outline, R.string.browse_movies)
                }
            }
        })
        //register refresh layout listener
        refreshLayout!!.setOnRefreshListener(this)
        //customize refresh layout color scheme
        refreshLayout!!.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))

        //set up button click listener for error/empty state views
        errorButton!!.setOnClickListener(this)
        //restore any previously saved data on activity state changed
        if (savedInstanceState != null) { //restore movies list data
            if (savedInstanceState.containsKey(Constants.MOVIES_LIST)) {
                progressBar!!.visibility = View.GONE
                movies = savedInstanceState.getParcelableArrayList(Constants.MOVIES_LIST)
                movieAdapter!!.setData(movies)
            }
            //restore page numbers for paginated data
            if (savedInstanceState.containsKey(Constants.MOST_POPULAR_START_PAGE)) {
                mostPopularMoviesStartPage = savedInstanceState.getInt(Constants.MOST_POPULAR_START_PAGE)
            }
            if (savedInstanceState.containsKey(Constants.TOP_RATED_START_PAGE)) {
                topRatedMoviesStartPage = savedInstanceState.getInt(Constants.TOP_RATED_START_PAGE)
            }
            //get the currently selected menu item
            if (savedInstanceState.containsKey(Constants.MOST_POPULAR_OPTION_CHECKED)) {
                mostPopularOptionChecked = savedInstanceState.getBoolean(Constants.MOST_POPULAR_OPTION_CHECKED)
            }
            if (savedInstanceState.containsKey(Constants.TOP_RATED_OPTION_CHECKED)) {
                topRatedOptionChecked = savedInstanceState.getBoolean(Constants.TOP_RATED_OPTION_CHECKED)
            }
            //get recycler view layout manager state to restore scroll position
            if (savedInstanceState.containsKey(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE)) {
                recyclerView!!.layoutManager!!.onRestoreInstanceState(
                        savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE))
            }
        }
        //enable pagination only if the list data is fetched from network (most popular/top rated)
//rather than favorites which is fetched from DB
        if (mostPopularOptionChecked || topRatedOptionChecked) { //set up pagination listener
            movieAdapter!!.setOnLoadMoreListener(this)
        }
        //call API based on the selected sort order - popular movies being default
//do not explicitly invoke the API on orientation change, wait for the list to be scrolled to the bottom
        if (savedInstanceState == null) {
            if (mostPopularOptionChecked) {
                popularMovies
            } else if (topRatedOptionChecked) {
                topRatedMovies
            }
        }
    }

    /**
     * inflates the menu on the action bar
     *
     * @param menu reference to the menu in which to inflate the options
     * @return boolean flag indicating whether inflating was successfully handled
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        //get references to the movie sort order menu items
        mostPopularMenuItem = menu.findItem(R.id.sort_most_popular)
        topRatedMenuItem = menu.findItem(R.id.sort_top_rated)
        favoritesMenuItem = menu.findItem(R.id.sort_favorites)
        return true
    }

    /**
     * handle selection of menu items
     *
     * @param item reference to the selected menu item
     * @return boolean flag indicating whether menu item selection action was successfully handled
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        cancelToast()
        when (item.itemId) {
            R.id.sort_most_popular ->  //if the item is already selected, exit early
                if (item.isChecked) {
                    return false
                } else {
                    errorLayout!!.visibility = View.GONE
                    //display progress indicator
                    progressBar!!.visibility = View.VISIBLE
                    //set the item as selected
                    item.isChecked = true
                    //unregister pagination listener before invoking the API to avoid unexpected behaviours
                    movieAdapter!!.setOnLoadMoreListener(null)
                    //clear the list, notify the adapter
                    movieAdapter!!.clear()
                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = true
                    topRatedOptionChecked = false
                    //reset the page number to load from the beginning
                    mostPopularMoviesStartPage = 1
                    //display popular movies
                    popularMovies
                }
            R.id.sort_top_rated ->  //if the item is already selected, exit early
                if (item.isChecked) {
                    return false
                } else {
                    errorLayout!!.visibility = View.GONE
                    //display progress indicator
                    progressBar!!.visibility = View.VISIBLE
                    //set the item as selected
                    item.isChecked = true
                    //unregister pagination listener before invoking the API to avoid unexpected behaviours
                    movieAdapter!!.setOnLoadMoreListener(null)
                    //clear the list, notify the adapter
                    movieAdapter!!.clear()
                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = false
                    topRatedOptionChecked = true
                    //reset the page number to load from the beginning
                    topRatedMoviesStartPage = 1
                    //display top rated movies
                    topRatedMovies
                }
            R.id.sort_favorites ->  //if the item is already selected, exit early
                if (item.isChecked) {
                    return false
                } else { //ignore internet connectivity check for displaying favorites
                    if (checkInternetConnection(this)) {
                        errorLayout!!.visibility = View.GONE
                        recyclerView!!.visibility = View.VISIBLE
                    }
                    progressBar!!.visibility = View.VISIBLE
                    //set the item as selected
                    item.isChecked = true
                    movieAdapter!!.setOnLoadMoreListener(null)
                    //clear the list, notify the adapter
                    movieAdapter!!.clear()
                    //disable refresh layout
                    refreshLayout!!.isEnabled = false
                    //reset scroll position to 0
                    recyclerView!!.scrollToPosition(0)
                    //set boolean flag to indicate the sort order chosen
                    mostPopularOptionChecked = false
                    topRatedOptionChecked = false
                    //hide loading indicator
                    progressBar!!.visibility = View.GONE
                    //get the list of favorite movies from the database
                    val favoriteMovies = viewModel!!.favoriteMovies.value
                    if (favoriteMovies != null && favoriteMovies.size > 0) { //update the list, notify the adapter
                        movieAdapter!!.addAll(favoriteMovies)
                    } else { //display empty state views if there are no favorites saved
                        updateEmptyStateViews(R.drawable.no_search_results, R.string.no_favorites,
                                R.drawable.ic_error_outline, R.string.browse_movies)
                    }
                }
        }
        return true
    }

    /**
     * called before displaying the menu to the user
     *
     * @param menu reference to the menu item that can be modified
     * @return boolean flag indicating whether any required modifications were successfully handled
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean { //set the checked item based on the boolean flag saved along activity lifecycle
        if (mostPopularOptionChecked) {
            mostPopularMenuItem!!.isChecked = true
        } else if (topRatedOptionChecked) {
            topRatedMenuItem!!.isChecked = true
        } else {
            favoritesMenuItem!!.isChecked = true
        }
        return true
    }//display error messages on failure//remove pagination loading indicator
    //hide refresh layout progress indicator
    //enable refresh action
    //verify if the response body or the fetched results are empty/null
    //update data set, notify the adapter
    //hide progress indicator and empty state views
    //display recycler view
    //notify the adapter that a new page data load is complete
    //increment the page number
    //enable pagination in case user wants to load the next page
//exit early if internet is not connected
    //define the call object that wraps the API response
    //invoke the call asynchronously

    /**
     * invoke TMDB API To fetch movies sorted by popularity
     */
    private val popularMovies: Unit
        private get() { //exit early if internet is not connected
            if (checkInternetConnection(this)) {
                updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection,
                        R.drawable.ic_cloud_off, R.string.error_try_again)
                return
            }
            //define the call object that wraps the API response
            call = client.getPopularMovies(Constants.API_KEY, mostPopularMoviesStartPage)
            //invoke the call asynchronously
            call!!.enqueue(object : Callback<MovieResults?> {
                override fun onResponse(call: Call<MovieResults?>, response: Response<MovieResults?>) { //remove pagination loading indicator
                    movieAdapter!!.removeLoader(null)
                    //hide refresh layout progress indicator
                    refreshLayout!!.isRefreshing = false
                    //enable refresh action
                    refreshLayout!!.isEnabled = true
                    //verify if the response body or the fetched results are empty/null
                    if (response.body() == null || response.body()!!.movies == null || response.body()!!.movies.size == 0) {
                        updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results)
                        return
                    }
                    //update data set, notify the adapter
                    movieAdapter!!.addAll(response.body()!!.movies)
                    //hide progress indicator and empty state views
                    errorLayout!!.visibility = View.GONE
                    progressBar!!.visibility = View.GONE
                    //display recycler view
                    recyclerView!!.visibility = View.VISIBLE
                    //notify the adapter that a new page data load is complete
                    movieAdapter!!.setLoading(false)
                    //increment the page number
                    mostPopularMoviesStartPage++
                    //enable pagination in case user wants to load the next page
                    movieAdapter!!.setOnLoadMoreListener(this@MainActivity)
                }

                override fun onFailure(call: Call<MovieResults?>, t: Throwable) { //display error messages on failure
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.browse_movies)
                }
            })
        }//display error messages on failure//remove pagination loading indicator
    //hide refresh layout progress indicator
    //enable refresh action
    //verify if the response body or the fetched results are empty/null
    //update data set, notify the adapter
    //hide progress indicator and empty state views
    //display recycler view
    //notify the adapter that a new page data load is complete
    //increment the page number
    //enable pagination in case user wants to load the next page
//exit early if internet is not connected
    //define the call object that wraps the API response
    //invoke the call asynchronously

    /**
     * invoke TMDB API To fetch movies sorted by ratings
     */
    private val topRatedMovies: Unit
        private get() { //exit early if internet is not connected
            if (checkInternetConnection(this)) {
                updateEmptyStateViews(R.drawable.no_internet_connection, R.string.no_internet_connection, R.drawable.ic_cloud_off, R.string.error_try_again)
                return
            }
            //define the call object that wraps the API response
            call = client.getTopRatedMovies(Constants.API_KEY, topRatedMoviesStartPage)
            //invoke the call asynchronously
            call!!.enqueue(object : Callback<MovieResults?> {
                override fun onResponse(call: Call<MovieResults?>, response: Response<MovieResults?>) { //remove pagination loading indicator
                    movieAdapter!!.removeLoader(null)
                    //hide refresh layout progress indicator
                    refreshLayout!!.isRefreshing = false
                    //enable refresh action
                    refreshLayout!!.isEnabled = true
                    //verify if the response body or the fetched results are empty/null
                    if (response.body() == null || response.body()!!.movies == null || response.body()!!.movies.size == 0) {
                        updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_movie, R.string.error_no_results)
                        return
                    }
                    //update data set, notify the adapter
                    movieAdapter!!.addAll(response.body()!!.movies)
                    //hide progress indicator and empty state views
                    errorLayout!!.visibility = View.GONE
                    progressBar!!.visibility = View.GONE
                    //display recycler view
                    recyclerView!!.visibility = View.VISIBLE
                    //notify the adapter that a new page data load is complete
                    movieAdapter!!.setLoading(false)
                    //increment the page number
                    topRatedMoviesStartPage++
                    //enable pagination in case user wants to load the next page
                    movieAdapter!!.setOnLoadMoreListener(this@MainActivity)
                }

                override fun onFailure(call: Call<MovieResults?>, t: Throwable) { //display error messages on failure
                    updateEmptyStateViews(R.drawable.no_search_results, R.string.no_search_results, R.drawable.ic_error_outline, R.string.error_no_results)
                }
            })
        }

    /**
     * called on API call failure or internet connection failure
     *
     * @param errorImage        resource ID of the image to be displayed indicating the error
     * @param errorText         user understandable error message
     * @param errorTextDrawable icon indicating the error
     * @param errorButtonText   text for the error button, prompting the user for an action
     */
    private fun updateEmptyStateViews(errorImage: Int, errorText: Int, errorTextDrawable: Int, errorButtonText: Int) { //disable refresh progress indicator
        refreshLayout!!.isRefreshing = false
        //display the error layout
        errorLayout!!.visibility = View.VISIBLE
        //hide progress indicator and RecyclerView
        progressBar!!.visibility = View.GONE
        recyclerView!!.visibility = View.GONE
        //update error images/text according to the error
        this.errorImage!!.setImageResource(errorImage)
        this.errorText!!.setText(errorText)
        this.errorText!!.setCompoundDrawablesWithIntrinsicBounds(0, errorTextDrawable, 0, 0)
        errorButton!!.setText(errorButtonText)
    }

    /**
     * called on swipe to refresh
     */
    override fun onRefresh() { //hide all the views
        recyclerView!!.visibility = View.GONE
        errorLayout!!.visibility = View.GONE
        //hide progress indicator when refresh indicator is displayed on swipe
//rather than user clicking on "Try Again" when the internet connection is down
        if (!fromErrorButton) {
            progressBar!!.visibility = View.GONE
        } else {
            displayToast(getString(R.string.trying_again_alert))
            fromErrorButton = false
        }
        //disable pagination to avoid unexpected results
        movieAdapter!!.setOnLoadMoreListener(null)
        movieAdapter!!.setLoading(false)
        //reset page counters
        mostPopularMoviesStartPage = 1
        topRatedMoviesStartPage = 1
        //clear the list, notify the adapter
        movieAdapter!!.clear()
        //invoke API call based on selected sort order
        if (mostPopularMenuItem!!.isChecked) {
            popularMovies
        } else if (topRatedMenuItem!!.isChecked) {
            topRatedMovies
        }
    }

    /**
     * invoked on loading a new page from the API results for pagination
     */
    override fun onLoadMore() { //disable swipe to refresh to avoid unexpected results
        refreshLayout!!.isEnabled = false
        //add a progress loading indicator at the bottom of the RecyclerView
        movieAdapter!!.addLoader(null)
        //invoke API call based on selected sort order
        if (mostPopularMenuItem!!.isChecked) {
            popularMovies
        } else if (topRatedMenuItem!!.isChecked) {
            topRatedMovies
        }
    }

    /**
     * handle view clicks
     *
     * @param view reference to the view that receives the click event
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.errorButton -> if ((view as Button).text.toString().trim { it <= ' ' }.equals(getString(R.string.error_try_again), ignoreCase = true)) { //call refresh action on clicking "Try Again"
                progressBar!!.visibility = View.VISIBLE
                fromErrorButton = true
                onRefresh()
            } else if (view.text.toString().trim { it <= ' ' }.equals(getString(R.string.browse_movies), ignoreCase = true)) { //display movies on clicking "Browse Movies"
                errorLayout!!.visibility = View.GONE
                progressBar!!.visibility = View.VISIBLE
                //reset start page
                mostPopularMoviesStartPage = 1
                topRatedMoviesStartPage = 1
                //display popular movies category by default
                mostPopularMenuItem!!.isChecked = true
                popularMovies
            } else { //close the app on server error
                finish()
            }
        }
    }

    /**
     * displays the specified message as a toast to alert the user
     *
     * @param message message to be displayed
     */
    private fun displayToast(message: String) {
        cancelToast()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        (toast as Toast).show()
    }

    private fun cancelToast() {
        if (toast != null) { //cancel any outstanding toasts
            toast!!.cancel()
        }
    }

    /**
     * called before activity gets destroyed
     *
     * @param outState bundle object that can hold any state that we want to save
     */
    override fun onSaveInstanceState(outState: Bundle) { //save state of menu items
        outState.putBoolean(Constants.MOST_POPULAR_OPTION_CHECKED, mostPopularOptionChecked)
        outState.putBoolean(Constants.TOP_RATED_OPTION_CHECKED, topRatedOptionChecked)
        //save movie array list
        outState.putParcelableArrayList(Constants.MOVIES_LIST, movies)
        //save start page numbers for all categories
        outState.putInt(Constants.MOST_POPULAR_START_PAGE, mostPopularMoviesStartPage)
        outState.putInt(Constants.TOP_RATED_START_PAGE, topRatedMoviesStartPage)
        //save recycler view scroll state
        outState.putParcelable(Constants.RECYCLER_VIEW_LAYOUT_MANAGER_STATE, recyclerView!!.layoutManager!!.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        cancelToast()
    }
}