package example.com.crackle.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import example.com.crackle.R
import example.com.crackle.adapter.MovieReviewAdapter
import example.com.crackle.model.Movie
import example.com.crackle.model.Review
import example.com.crackle.model.ReviewResults
import example.com.crackle.utils.Constants
import example.com.crackle.utils.MovieApiService.client
import example.com.crackle.utils.Utils.setupRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class MovieReviewsFragment : Fragment() {

    @JvmField
    @BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    @JvmField
    @BindView(R.id.emptyTextView)
    var emptyTextView: TextView? = null
    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null

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
        return inflater.inflate(R.layout.fragment_movie_reviews, container, false)
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
        //set up RecyclerView - define caching properties and default animator
        setupRecyclerView(context!!, recyclerView!!, Constants.LINEAR_LAYOUT_VERTICAL)
        //initialize data set and set up the adapter
        val reviewList: MutableList<Review> = ArrayList()
        val adapter = MovieReviewAdapter(context!!, reviewList)
        recyclerView!!.adapter = adapter

        if (arguments != null) {
            val movie: Movie = arguments!!.getParcelable(Constants.MOVIE)!!
            //invoke movie reviews call passing the movie id and API KEY
            val call = client.getMovieReviews(movie.movieId, Constants.API_KEY)
            //invoke API call asynchronously
            call.enqueue(object : Callback<ReviewResults?> {
                override fun onResponse(call: Call<ReviewResults?>, response: Response<ReviewResults?>) {
                    progressBar!!.visibility = View.GONE
                    //verify if the response body or the fetched results are empty/null
                    if (response.body() == null) {
                        return
                    }
                    //update data set, notify the adapter, update view visibility accordingly
                    if (response.body()!!.reviewList.isNotEmpty()) {
                        reviewList.addAll(response.body()!!.reviewList)
                        adapter.notifyDataSetChanged()
                        emptyTextView!!.visibility = View.GONE
                        recyclerView!!.visibility = View.VISIBLE
                    } else {
                        recyclerView!!.visibility = View.GONE
                        emptyTextView!!.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ReviewResults?>, t: Throwable) {
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(context, R.string.error_movie_review, Toast.LENGTH_SHORT).show()
                }
            })
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
            val fragment = MovieReviewsFragment()
            val args = Bundle()
            args.putParcelable(Constants.MOVIE, movie)
            fragment.arguments = args
            return fragment
        }
    }
}