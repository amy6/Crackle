package example.com.crackle.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieReviewAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.Movie;
import example.com.crackle.model.Review;
import example.com.crackle.model.ReviewResults;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.LINEAR_LAYOUT_VERTICAL;
import static example.com.crackle.utils.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    public MovieReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * inflates the view for the fragment
     *
     * @param inflater           reference to inflater service
     * @param container          parent for the fragment
     * @param savedInstanceState reference to bundle object that can be used to save activity states
     * @return inflated view for fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_reviews, container, false);
    }

    /**
     * called after onCreateView returns - resolve references to child views here
     *
     * @param view               reference to created view that can be modified
     * @param savedInstanceState reference to bundle object that can be used to save activity states
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        //set up RecyclerView - define caching properties and default animator
        Utils.setupRecyclerView(getContext(), recyclerView, LINEAR_LAYOUT_VERTICAL);

        //initialize data set and set up the adapter
        List<Review> reviewList = new ArrayList<>();
        final MovieReviewAdapter adapter = new MovieReviewAdapter(getContext(), reviewList);
        recyclerView.setAdapter(adapter);

        //initialize retrofit client and call object that wraps the response
        MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);

        if (getArguments() != null) {
            Movie movie = getArguments().getParcelable(MOVIE);

            if (movie != null) {
                //invoke movie reviews call passing the movie id and API KEY
                Call<ReviewResults> call = client.getMovieReviews(movie.getMovieId(), API_KEY);
                //invoke API call asynchronously
                call.enqueue(new Callback<ReviewResults>() {
                    @Override
                    public void onResponse(@NonNull Call<ReviewResults> call, @NonNull Response<ReviewResults> response) {
                        progressBar.setVisibility(View.GONE);
                        //verify if the response body or the fetched results are empty/null
                        if (response.body() == null || response.body().getReviewList() == null) {
                            return;
                        }

                        //update data set, notify the adapter, update view visibility accordingly
                        if (response.body().getReviewList().size() > 0) {
                            reviewList.addAll(response.body().getReviewList());
                            adapter.notifyDataSetChanged();
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<ReviewResults> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.error_movie_review, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * return new instance of fragment with movie data passed in as arguments
     *
     * @param movie reference to movie object set as one of fragment's arguments
     * @return instance of fragment
     */
    public static Fragment newInstance(Movie movie) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
