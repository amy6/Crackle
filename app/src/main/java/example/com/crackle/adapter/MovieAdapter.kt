package example.com.crackle.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import example.com.crackle.R
import example.com.crackle.activity.MainActivity
import example.com.crackle.activity.MovieDetailsActivity
import example.com.crackle.listener.OnLoadMoreListener
import example.com.crackle.model.Movie
import example.com.crackle.utils.Constants
import example.com.crackle.utils.Utils

class MovieAdapter(private val context: Context, private var movies: MutableList<Movie?>?, recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemCount: Int = 0
    private var lastVisibleItemPosition: Int = 0
    private val viewThreshold = 1

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loading = false

    init {

        //get reference to layout manager
        val layoutManager = recyclerView.layoutManager as GridLayoutManager?

        //register scroll listener on RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (layoutManager != null) {
                    //set span size depending on the type of view being displayed
                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (getItemViewType(position)) {
                                Constants.ITEM -> 1
                                Constants.PROGRESS -> layoutManager.spanCount
                                else -> -1
                            }
                        }
                    }

                    //get total number of items currently displayed
                    itemCount = layoutManager.itemCount
                    //find the last visible item position
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    //if RecyclerView is scrolled to end, call load more to fetch next page of results
                    if (!loading && itemCount <= lastVisibleItemPosition + viewThreshold) {
                        Log.d(Constants.LOG_TAG, "RecyclerView has been scrolled to end")
                        if (onLoadMoreListener != null) {
                            Log.d(Constants.LOG_TAG, "Listener is active")
                            onLoadMoreListener!!.onLoadMore()
                        }
                        setLoading(true)
                    }
                }
            }
        })

    }

    /**
     * returns a view - item view or a progress indicator view to be displayed in the RecyclerView
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates whether the view is an individual item view or a progress indicator view
     * @return new view holder associated with the item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        //inflate the views based on view type
        when (viewType) {
            Constants.ITEM -> {
                val itemView = LayoutInflater.from(context).inflate(R.layout.layout_movie_item, parent, false)
                viewHolder = MovieViewHolder(itemView)
            }
            Constants.PROGRESS -> {
                val progressView = LayoutInflater.from(context).inflate(R.layout.layout_progress_item, parent, false)
                viewHolder = ProgressViewHolder(progressView)
            }
        }
        return viewHolder!!
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //binding logic for movie item view
        if (holder is MovieViewHolder) {
//get the current movie
            val movie = movies!![position]
            //update view data
            val imageUrl = Constants.IMAGE_URL_SIZE + if (movie?.imageUrl != null) movie.imageUrl else ""
            Glide.with(context)
                    .setDefaultRequestOptions(Utils.setupGlide(Constants.POSTER_IMG))
                    .load(imageUrl)
                    //set up a listener to handle success and failure of image loading
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            holder.progressBar!!.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            holder.progressBar!!.visibility = View.GONE
                            return false
                        }
                    })
                    .into(holder.imageView!!)
        } else {
            //set progress indicator for loading view type - used for pagination
            (holder as ProgressViewHolder).progressBar!!.isIndeterminate = true
        }

    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    override fun getItemCount(): Int {
        return if (movies == null) 0 else movies!!.size
    }

    /**
     * called by the RecyclerView to get the item type to be displayed
     *
     * @param position position of the item that is needed by the RecyclerView
     * @return type of the view - movie item view or progress indicator view
     */
    override fun getItemViewType(position: Int): Int {
        return if (movies!![position] != null) Constants.ITEM else Constants.PROGRESS
    }

    /**
     * called to update data set
     *
     * @param movies list of new data
     */
    fun setData(movies: MutableList<Movie?>?) {
        this.movies = movies
        notifyDataSetChanged()
    }

    /**
     * clear all data
     */
    fun clear() {
        this.movies!!.clear()
        notifyDataSetChanged()
    }

    /**
     * add data fetched from API
     *
     * @param movies list of fetched data
     */
    fun addAll(movies: List<Movie?>?) {
        movies?.let { this.movies!!.addAll(it) }
        notifyDataSetChanged()
    }

    //ViewHolder for movie item view to help reduce findViewById calls
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        @JvmField
        @BindView(R.id.poster_image)
        var imageView: ImageView? = null
        @JvmField
        @BindView(R.id.progressBar)
        var progressBar: ProgressBar? = null

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }

        //define on click listener for movie item view to transition to details screen
        override fun onClick(v: View) {
            val movie = movies!![adapterPosition]
            val intent = Intent(context, MovieDetailsActivity::class.java)
            //pass the movie object data
            intent.putExtra(Intent.EXTRA_TEXT, movie)
            val options: ActivityOptionsCompat
            //define shared element transition for the movie poster image
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptionsCompat.makeSceneTransitionAnimation(context as MainActivity, imageView!!, imageView!!.transitionName)
                v.context.startActivity(intent, options.toBundle())
            } else {
                v.context.startActivity(intent)
            }

        }
    }

    //ViewHolder for progress indicator view to help reduce findViewById calls
    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.progressBar)
        var progressBar: ProgressBar? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    /**
     * adds a progress indicator at the bottom of the RecyclerView while getting next page results
     *
     * @param movie reference to movie object to be added to data set (in this case a null)
     */
    fun addLoader(movie: Movie?) {
        movies!!.add(movie)
        Handler().post {
            //notify the adapter of the change in data set
            notifyItemInserted(movies!!.size - 1)
        }

    }

    /**
     * removes the progress indicator at the bottom of the RecyclerView after getting a new page's results
     *
     * @param movie reference to movie object to be removed to data set (in this case a null)
     */
    fun removeLoader(movie: Movie?) {
        movies!!.remove(movie)
        //notify the adapter of the change in data set
        notifyItemRemoved(movies!!.size - 1)
    }


    /**
     * setter for loading flag that indicates whether a new page data is being loaded
     *
     * @param loading flag indicating data loading
     */
    fun setLoading(loading: Boolean) {
        this.loading = loading
    }

    /**
     * register pagination listener
     *
     * @param onLoadMoreListener reference to listener
     */
    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }
}
