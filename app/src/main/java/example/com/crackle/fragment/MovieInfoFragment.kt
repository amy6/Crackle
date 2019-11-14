package example.com.crackle.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import example.com.crackle.R
import example.com.crackle.adapter.MovieVideoAdapter
import example.com.crackle.model.CreditResults
import example.com.crackle.model.Crew
import example.com.crackle.model.Movie
import example.com.crackle.model.Video
import example.com.crackle.utils.Constants
import example.com.crackle.utils.MovieApiService.client
import example.com.crackle.utils.Utils.fetchAllLanguages
import example.com.crackle.utils.Utils.setupRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 */
class MovieInfoFragment : Fragment() {
    @JvmField
    @BindView(R.id.tmdbRating)
    var tmdbRating: TextView? = null
    @JvmField
    @BindView(R.id.ratingBar)
    var ratingBar: RatingBar? = null
    @JvmField
    @BindView(R.id.popularity)
    var popularity: TextView? = null
    @JvmField
    @BindView(R.id.language)
    var language: TextView? = null
    @JvmField
    @BindView(R.id.plot)
    var plotTextView: TextView? = null
    @JvmField
    @BindView(R.id.director)
    var directorTextView: TextView? = null
    @JvmField
    @BindView(R.id.release_date)
    var releaseDateTextView: TextView? = null
    @JvmField
    @BindView(R.id.homepage)
    var homepage: TextView? = null
    @JvmField
    @BindView(R.id.originalTitle)
    var originalTitle: TextView? = null
    @JvmField
    @BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    @JvmField
    @BindView(R.id.emptyTextView)
    var emptyTextView: TextView? = null

    /**
     * inflates the view for the fragment
     *
     * @param inflater           reference to inflater service
     * @param container          parent for the fragment
     * @param savedInstanceState reference to bundle object that can be used to save activity states
     * @return inflated view for fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_info, container, false)
    }

    /**
     * called after onCreateView returns - resolve references to child views here
     *
     * @param view               reference to created view that can be modified
     * @param savedInstanceState reference to bundle object that can be used to save activity states
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        //initialize data set and set up the adapter
        val videoList: MutableList<Video> = ArrayList()
        val crewList: MutableList<Crew> = ArrayList()
        //set up RecyclerView - define caching properties and default animator
        setupRecyclerView(context!!, recyclerView!!, Constants.LINEAR_LAYOUT_HORIZONTAL)
        //set up adapter for RecyclerView
        val adapter = MovieVideoAdapter(context!!, videoList)
        recyclerView!!.adapter = adapter
        //fetch list of language code and corresponding names
        val languageMap = fetchAllLanguages(context!!)
        //get movie object
        if (arguments != null) {
            val movie: Movie = arguments!!.getParcelable(Constants.MOVIE)!!
            if (movie != null) { //set up view data
                plotTextView!!.text = movie.plot
                releaseDateTextView!!.text = movie.releaseDate
                val rating = if (movie.userRating == 0.0) getString(R.string.no_ratings) else DecimalFormat.getNumberInstance().format(movie.userRating) + "/10"
                tmdbRating!!.text = rating
                ratingBar!!.rating = (movie.userRating / 2f).toFloat()
                popularity!!.text = DecimalFormat.getNumberInstance().format(movie.popularity)
                language!!.text = languageMap!![movie.language]
                if (!TextUtils.isEmpty(movie.homepage)) {
                    homepage!!.text = movie.homepage
                }
                if (!TextUtils.isEmpty(movie.originalTitle)) {
                    originalTitle!!.text = movie.originalTitle
                }

                //invoke movie credits call passing the movie id and API KEY
                val creditResultsCall = client.getMovieCredits(movie.movieId, Constants.API_KEY)
                //invoke API call asynchronously
                creditResultsCall.enqueue(object : Callback<CreditResults?> {
                    override fun onResponse(call: Call<CreditResults?>, response: Response<CreditResults?>) { //verify if the response body or the fetched results are empty/null
                        if (response.body() == null || response.body()!!.crewList == null || response.body()!!.crewList.size == 0) {
                            return
                        }
                        //update data set, update the views accordingly
                        crewList.addAll(response.body()!!.crewList)
                        directorTextView!!.text = ""
                        for (i in crewList.indices) {
                            if (crewList[i].job.equals("director", ignoreCase = true)) {
                                directorTextView!!.append(crewList[i].name)
                                if (i < crewList.size - 1 && crewList[i + 1].job.equals("director", ignoreCase = true)) {
                                    directorTextView!!.append("\n")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<CreditResults?>, t: Throwable) {
                        Toast.makeText(context, R.string.error_movie_director, Toast.LENGTH_SHORT).show()
                    }
                })
                //display trailer thumbnails
                if (movie.videoResults != null && movie.videoResults.videos != null && movie.videoResults.videos.size > 0) {
                    recyclerView!!.visibility = View.VISIBLE
                    emptyTextView!!.visibility = View.GONE
                    videoList.addAll(movie.videoResults.videos)
                    adapter.notifyDataSetChanged()
                } else {
                    emptyTextView!!.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        /**
         * return new instance of fragment with movie data passed in as arguments
         *
         * @param movie reference to movie object set as one of fragment's arguments
         * @return instance of fragment
         */
        fun newInstance(movie: Movie?): Fragment {
            val fragment = MovieInfoFragment()
            val args = Bundle()
            args.putParcelable(Constants.MOVIE, movie)
            fragment.arguments = args
            return fragment
        }
    }
}