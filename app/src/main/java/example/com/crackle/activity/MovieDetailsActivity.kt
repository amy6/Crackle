package example.com.crackle.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import example.com.crackle.R
import example.com.crackle.adapter.MovieFragmentPagerAdapter
import example.com.crackle.adapter.MovieImageAdapter
import example.com.crackle.database.MovieDatabase.Companion.getInstance
import example.com.crackle.model.Certification
import example.com.crackle.model.Image
import example.com.crackle.model.Movie
import example.com.crackle.utils.AppExecutors.Companion.getExecutorInstance
import example.com.crackle.utils.Constants
import example.com.crackle.utils.MovieApiService.client
import example.com.crackle.utils.Utils.fetchAllGenres
import example.com.crackle.utils.Utils.formatDuration
import example.com.crackle.utils.Utils.setupGlide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity(), OnClickListener {
    @JvmField
    @BindView(R.id.poster_image)
    var posterImage: ImageView? = null
    @JvmField
    @BindView(R.id.backdrop_image_viewpager)
    var backdropImageViewPager: ViewPager? = null
    @JvmField
    @BindView(R.id.viewpager_indicator)
    var viewPagerIndicator: TabLayout? = null
    @JvmField
    @BindView(R.id.title)
    var title: TextView? = null
    @JvmField
    @BindView(R.id.year)
    var year: TextView? = null
    @JvmField
    @BindView(R.id.duration)
    var duration: TextView? = null
    @JvmField
    @BindView(R.id.genre)
    var genre: TextView? = null
    @JvmField
    @BindView(R.id.content_rating)
    var contentRating: TextView? = null
    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null
    @JvmField
    @BindView(R.id.tabLayout)
    var tabLayout: TabLayout? = null
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @JvmField
    @BindView(R.id.appbarLayout)
    var appBarLayout: AppBarLayout? = null
    @JvmField
    @BindView(R.id.collapsingtoolbar)
    var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    @JvmField
    @BindView(R.id.favorites)
    var favorites: FloatingActionButton? = null
    private var movie: Movie? = null
    private var movieId = 0
    private var images: MutableList<Image>? = null
    private var certifications: List<Certification>? = null
    private var viewModel: MovieDetailsActivityViewModel? = null
    private var toast: Toast? = null
    private var youtubeTrailerLink: String? = null
    private var isFavorite = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        //resolve references to views
        ButterKnife.bind(this)
        //setup toolbar
        setSupportActionBar(toolbar)
        //add back navigation option on toolbar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        //postpone shared element transition till the image is loaded from the API
        supportPostponeEnterTransition()
        //make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        //get reference to view model
        viewModel = ViewModelProviders.of(this).get(MovieDetailsActivityViewModel::class.java)
        //initialize data sets
        images = ArrayList()
        certifications = ArrayList()
        //set up click listener for favorites button
        favorites!!.setOnClickListener(this)
        //display toolbar title only when collapsed
        handleCollapsedToolbarTitle()
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) { //get movie object from intent
                movie = intent.getParcelableExtra(Intent.EXTRA_TEXT)
                movieId = (movie as Movie).movieId
                getExecutorInstance().diskIO.execute {
                    isFavorite = viewModel!!.isFavorite(movieId)
                    if (isFavorite) {
                        movie = getInstance(this)!!.movieDao().getMovie(movieId)
                        runOnUiThread { favorites!!.setImageResource(R.drawable.ic_favorite) }
                    } else {
                        runOnUiThread { favorites!!.setImageResource(R.drawable.ic_favorite_border) }
                    }
                }
            }
        }
        //set up Retrofit call to get movie details
        fetchMovieDetails()
    }

    /**
     * invokes TMDB API to get movie details
     *
     * @param client reference to Retrofit client
     */
    private fun fetchMovieDetails() {
        if (movieId != 0) {
            val detailResultsCall = client.getMovieDetails(movieId, Constants.API_KEY)
            //get the list of genres for the movie
            fetchMovieGenre()
            detailResultsCall.enqueue(object : Callback<Movie?> {
                override fun onResponse(call: Call<Movie?>, response: Response<Movie?>) {
                    if (response.body() == null) {
                        return
                    }
                    //fetch movie duration from details api call
                    val runtime = response.body()!!.duration
                    //display run time in h:m format
                    duration!!.text = formatDuration(this@MovieDetailsActivity, runtime)
                    //set movie homepage
                    if (response.body()!!.homepage != null && !TextUtils.isEmpty(response.body()!!.homepage)) {
                        movie!!.homepage = response.body()!!.homepage
                    }
                    //set movie title
                    if (response.body()!!.originalTitle != null && !TextUtils.isEmpty(response.body()!!.originalTitle)) {
                        movie!!.originalTitle = response.body()!!.originalTitle
                    }
                    //set up viewpager to display movie info, cast and reviews
                    viewPager!!.adapter = MovieFragmentPagerAdapter(supportFragmentManager, movie!!)
                    tabLayout!!.setupWithViewPager(viewPager)
                }

                override fun onFailure(call: Call<Movie?>, t: Throwable) {
                    if (movie != null) {
                        Log.d(Constants.LOG_TAG, "Movie already set by favorites")
                    } else {
                        displayToastMessage(R.string.error_movie_details)
                    }
                }
            })
            val extraDetailResultsCall = client.getMovieDetails(movieId, Constants.API_KEY, Constants.APPEND_TO_RESPONSE_VALUE)
            extraDetailResultsCall.enqueue(object : Callback<Movie?> {
                override fun onResponse(call: Call<Movie?>, response: Response<Movie?>) {
                    if (response.body() == null) {
                        return
                    }
                    //fetch backdrop images
                    if (response.body()!!.imageResults != null && response.body()!!.imageResults!!.backdrops != null && response.body()!!.imageResults!!.backdrops.size > 0) { //add fetched images to the list
                        if (response.body()!!.imageResults?.backdrops!!.size > 8) {
                            for (i in 0..7) {
                                images!!.add(response.body()!!.imageResults!!.backdrops[i])
                            }
                        } else {
                            images!!.addAll(response.body()!!.imageResults!!.backdrops)
                        }
                    }
                    //fetch movie trailers
                    if (response.body()!!.videoResults != null && response.body()!!.videoResults!!.videos != null && response.body()!!.videoResults!!.videos.size > 0) {
                        movie!!.videoResults = response.body()!!.videoResults
                        youtubeTrailerLink = movie!!.videoResults!!.videos[0].title
                    }
                    if (response.body()!!.certificationResults != null && response.body()!!.certificationResults!!.certificationList != null && response.body()!!.certificationResults!!.certificationList.size > 0) {
                        certifications = response.body()!!.certificationResults!!.certificationList
                        for (certification in certifications!!) {
                            if (certification.iso == "IN") {
                                if (!TextUtils.isEmpty(certification.certification)) {
                                    contentRating!!.text = certification.certification
                                }
                            }
                        }
                    }
                    //set up viewpager for backdrop image list
                    viewPagerIndicator!!.setupWithViewPager(backdropImageViewPager)
                    val adapter = MovieImageAdapter(this@MovieDetailsActivity, images)
                    backdropImageViewPager!!.adapter = adapter
                }

                override fun onFailure(call: Call<Movie?>, t: Throwable) {
                    if (movie!!.isFavorite && movie != null) {
                        Log.d(Constants.LOG_TAG, "Movie already set by favorites")
                        duration!!.text = formatDuration(this@MovieDetailsActivity, movie!!.duration)
                        movie!!.userRating = movie!!.userRating
                        movie!!.homepage = movie!!.homepage
                        movie!!.originalTitle = movie!!.originalTitle
                        //set up viewpager to display movie info, cast and reviews
                        viewPager!!.adapter = MovieFragmentPagerAdapter(supportFragmentManager, movie!!)
                        tabLayout!!.setupWithViewPager(viewPager)
                        val imageUrl = ArrayList<Image>()
                        val backdropImage = Image(movie!!.backdropImageUrl!!)
                        imageUrl.add(backdropImage)
                        images!!.addAll(imageUrl)
                        //set up viewpager for backdrop image list
                        viewPagerIndicator!!.setupWithViewPager(backdropImageViewPager)
                        val adapter = MovieImageAdapter(this@MovieDetailsActivity, images)
                        backdropImageViewPager!!.adapter = adapter
                    } else {
                        displayToastMessage(R.string.error_movie_details)
                    }
                }
            })
        }
    }

    /**
     * gets the applicable genre category for the movie based on genre code
     */
    private fun fetchMovieGenre() { //get the list of all genre code and corresponding names from local json file
        val genreMap = fetchAllGenres(this)
        //set the fields for the movie
        title!!.text = movie!!.title
        year!!.text = movie!!.releaseDate!!.substring(0, 4)
        //define default image in case the result is null
        val posterImageUrl = if (movie!!.imageUrl != null) Constants.IMAGE_URL_SIZE + movie!!.imageUrl else ""
        Glide.with(this)
                .setDefaultRequestOptions(setupGlide(Constants.BACKDROP_IMG))
                .load(posterImageUrl)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(posterImage!!)
        //get genre names based on genre codes
        if (movie!!.genres != null) {
            val genreId: List<Int> = ArrayList(movie!!.genres!!)
            var count = 0
            for (id in genreId) {
                genre!!.append(genreMap!![id])
                count++
                if (count < genreId.size) {
                    genre!!.append(", ")
                }
            }
        }
    }

    /**
     * sets the title on the toolbar only if the toolbar is collapsed
     */
    private fun handleCollapsedToolbarTitle() { //set title on on collapsed toolbar
        appBarLayout!!.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                //verify if the toolbar is completely collapsed and set the movie name as the title
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout!!.title = movie!!.title
                    isShow = true
                } else if (isShow) { //display an empty string when toolbar is expanded
                    collapsingToolbarLayout!!.title = " "
                    isShow = false
                }
            }
        })
    }

    /**
     * inflate menu options
     *
     * @param menu reference to menu object
     * @return boolean flag indicating whether the menu create action was handled successfully
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    /**
     * handle selection of menu options
     *
     * @param item reference to the menu item clicked
     * @return boolean flag indicating whether the menu click action was handled successfully
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent = Intent()
        when (item.itemId) {
            R.id.action_share -> {
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                //set text content
                var intentContent = String.format(getString(R.string.movie_share_intent_text),
                        movie!!.title,
                        Constants.TMDB_MOVIE_BASE_URI, movie!!.movieId.toString())
                //add youtube trailer link
                if (youtubeTrailerLink != null && !TextUtils.isEmpty(youtubeTrailerLink)) {
                    intentContent = intentContent + String.format(getString(R.string.youtube_link_text), youtubeTrailerLink)
                }
                intentContent = intentContent + getString(R.string.crackle_text)
                intent.putExtra(Intent.EXTRA_TEXT, intentContent)
                //set custom chooser title
                intent = Intent.createChooser(intent, String.format(getString(R.string.movie_share_intent_chooser_text),
                        movie!!.title))
            }
            R.id.action_playstore -> {
                intent.action = Intent.ACTION_VIEW
                //set PlayStore category to movies
                intent.data = Uri.parse(Constants.PLAYSTORE_BASE_URI +
                        movie!!.title).buildUpon()
                        .appendQueryParameter(Constants.PLAYSTORE_QUERY_PARAMETER_CATEGORY,
                                Constants.PLAYSTORE_QUERY_VALUE_CATEGORY).build()
            }
            R.id.action_tmdb -> {
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(Constants.TMDB_MOVIE_BASE_URI + movie!!.movieId)
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        //verify if the intent can be opened with a suitable app on the device
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            displayToastMessage(R.string.error_movie_intent)
        }
        return true
    }

    /**
     * handle click events on views
     *
     * @param view reference to the view that receives the click event
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.favorites -> {
                //set animation on click for favorite button
                val anim = AnimationUtils.loadAnimation(this, R.anim.shake)
                favorites!!.startAnimation(anim)
                getExecutorInstance().diskIO.execute {
                    //get saved state of the movie from the database
                    val isFavorite = viewModel!!.isFavorite(movieId)
                    if (isFavorite) { //handle removing the movie from favorites db
                        viewModel!!.removeMovieFromFavorites(movie)
                        runOnUiThread {
                            displayToastMessage(R.string.favorites_removed)
                            favorites!!.setImageResource(R.drawable.ic_favorite_border)
                        }
                    } else { //handle adding the movie to the favorites db
                        viewModel!!.addMovieToFavorites(movie)
                        runOnUiThread {
                            displayToastMessage(R.string.favorites_added)
                            favorites!!.setImageResource(R.drawable.ic_favorite)
                        }
                    }
                    //update saved state
                    viewModel!!.updateMovieFavorite(movieId, !isFavorite)
                }
            }
        }
    }

    /**
     * display message in toast
     *
     * @param messageId string resource id for the message to be displayed
     */
    private fun displayToastMessage(messageId: Int) {
        cancelToast()
        toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        (toast as Toast).show()
    }

    private fun cancelToast() { //dismiss any outstanding toast messages
        if (toast != null) {
            toast!!.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        cancelToast()
    }
}