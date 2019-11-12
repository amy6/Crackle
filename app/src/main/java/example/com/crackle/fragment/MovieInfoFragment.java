package example.com.crackle.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieVideoAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.CreditResults;
import example.com.crackle.model.Crew;
import example.com.crackle.model.Movie;
import example.com.crackle.model.Video;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.LINEAR_LAYOUT_HORIZONTAL;
import static example.com.crackle.utils.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieInfoFragment extends Fragment {

    @BindView(R.id.tmdbRating)
    TextView tmdbRating;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.popularity)
    TextView popularity;
    @BindView(R.id.language)
    TextView language;
    @BindView(R.id.plot)
    TextView plotTextView;
    @BindView(R.id.director)
    TextView directorTextView;
    @BindView(R.id.release_date)
    TextView releaseDateTextView;
    @BindView(R.id.homepage)
    TextView homepage;
    @BindView(R.id.originalTitle)
    TextView originalTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;


    public MovieInfoFragment() {
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
        return inflater.inflate(R.layout.fragment_movie_info, container, false);
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

        //initialize data set and set up the adapter
        List<Video> videoList = new ArrayList<>();
        List<Crew> crewList = new ArrayList<>();

        //set up RecyclerView - define caching properties and default animator
        Utils.setupRecyclerView(getContext(), recyclerView, LINEAR_LAYOUT_HORIZONTAL);

        //set up adapter for RecyclerView
        final MovieVideoAdapter adapter = new MovieVideoAdapter(getContext(), videoList);
        recyclerView.setAdapter(adapter);

        //fetch list of language code and corresponding names
        HashMap<String, String> languageMap = Utils.fetchAllLanguages(getContext());

        //get movie object
        if (getArguments() != null) {
            Movie movie = getArguments().getParcelable(MOVIE);
            if (movie != null) {

                //set up view data
                plotTextView.setText(movie.getPlot());
                releaseDateTextView.setText(movie.getReleaseDate());
                String rating = movie.getUserRating() == 0 ? getString(R.string.no_ratings) : DecimalFormat.getNumberInstance().format(movie.getUserRating()).concat("/10");
                tmdbRating.setText(rating);
                ratingBar.setRating((float) (movie.getUserRating() / 2f));
                popularity.setText(DecimalFormat.getNumberInstance().format(movie.getPopularity()));
                language.setText(languageMap.get(movie.getLanguage()));
                if (!TextUtils.isEmpty(movie.getHomepage())) {
                    homepage.setText(movie.getHomepage());
                }
                if (!TextUtils.isEmpty(movie.getOriginalTitle())) {
                    originalTitle.setText(movie.getOriginalTitle());
                }

                //initialize retrofit client and call object that wraps the response
                MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);
                //invoke movie credits call passing the movie id and API KEY
                Call<CreditResults> creditResultsCall = client.getMovieCredits(movie.getMovieId(), API_KEY);
                //invoke API call asynchronously
                creditResultsCall.enqueue(new Callback<CreditResults>() {
                    @Override
                    public void onResponse(@NonNull Call<CreditResults> call, @NonNull Response<CreditResults> response) {
                        //verify if the response body or the fetched results are empty/null
                        if (response.body() == null || response.body().getCrewList() == null || response.body().getCrewList().size() == 0) {
                            return;
                        }

                        //update data set, update the views accordingly
                        crewList.addAll(response.body().getCrewList());
                        directorTextView.setText("");
                        for (int i = 0; i < crewList.size(); i++) {
                            if (crewList.get(i).getJob().equalsIgnoreCase("director")) {
                                directorTextView.append(crewList.get(i).getName());
                                if (i < crewList.size() - 1 && crewList.get(i + 1).getJob().equalsIgnoreCase("director")) {
                                    directorTextView.append("\n");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CreditResults> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), R.string.error_movie_director, Toast.LENGTH_SHORT).show();
                    }
                });

                //display trailer thumbnails
                if (movie.getVideoResults() != null && movie.getVideoResults().getVideos() != null && movie.getVideoResults().getVideos().size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                    videoList.addAll(movie.getVideoResults().getVideos());
                    adapter.notifyDataSetChanged();
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
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
        MovieInfoFragment fragment = new MovieInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
