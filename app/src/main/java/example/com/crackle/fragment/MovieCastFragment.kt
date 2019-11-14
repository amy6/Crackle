package example.com.crackle.fragment

import android.os.Bundle
import android.os.Parcelable
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
import example.com.crackle.adapter.MovieCastAdapter
import example.com.crackle.listener.MovieApiClient
import example.com.crackle.model.Cast
import example.com.crackle.model.CreditResults
import example.com.crackle.model.Movie
import example.com.crackle.utils.Constants
import example.com.crackle.utils.MovieApiService.client
import example.com.crackle.utils.Utils.setupRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class MovieCastFragment : Fragment() {
    @JvmField
    @BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    @JvmField
    @BindView(R.id.emptyTextView)
    var emptyTextView: TextView? = null
    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null
    private var castList: List<Cast>? = null
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
        return inflater.inflate(R.layout.fragment_movie_cast, container, false)
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
        setupRecyclerView(context!!, recyclerView!!, Constants.GRID_LAYOUT)
        //initialize data set and set up the adapter
        castList = ArrayList()
        val adapter = MovieCastAdapter(context!!, castList)
        recyclerView!!.adapter = adapter
        //initialize retrofit client and call object that wraps the response
        val client = client.create(MovieApiClient::class.java)
        //invoke movie credits call passing the movie id and API KEY
        val call = client.getMovieCredits((arguments!!.getParcelable<Parcelable>(Constants.MOVIE) as Movie?)!!.movieId, Constants.API_KEY)
        //invoke API call asynchronously
        call.enqueue(object : Callback<CreditResults?> {
            override fun onResponse(call: Call<CreditResults?>, response: Response<CreditResults?>) {
                progressBar!!.visibility = View.GONE
                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body()!!.castList == null) {
                    return
                }
                //update data set, notify the adapter
//update view visibility accordingly
                if (response.body()!!.castList.size > 0) {
                    (castList as ArrayList<Cast>).addAll(response.body()!!.castList)
                    adapter.notifyDataSetChanged()
                    emptyTextView!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                } else {
                    emptyTextView!!.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<CreditResults?>, t: Throwable) {
                progressBar!!.visibility = View.GONE
                Toast.makeText(context, R.string.error_movie_cast, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        /**
         * return new instance of fragment with movie data passed in as arguments
         *
         * @param movie reference to movie object set as one of fragment's arguments
         * @return instance of fragment
         */
        fun newInstance(movie: Movie?): Fragment {
            val fragment = MovieCastFragment()
            val args = Bundle()
            args.putParcelable(Constants.MOVIE, movie)
            fragment.arguments = args
            return fragment
        }
    }
}