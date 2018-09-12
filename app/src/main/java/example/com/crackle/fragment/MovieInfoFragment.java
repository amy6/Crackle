package example.com.crackle.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import example.com.crackle.model.DetailResults;
import example.com.crackle.model.Movie;
import example.com.crackle.model.Video;
import example.com.crackle.model.VideoResults;
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

    private List<Crew> crewList;
    private List<Video> videoList;


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

        //set up RecyclerView - define caching properties and default animator
        Utils.setupRecyclerView(getContext(), recyclerView, LINEAR_LAYOUT_HORIZONTAL);

        //initialize data set and set up the adapter
        videoList = new ArrayList<>();
        final MovieVideoAdapter adapter = new MovieVideoAdapter(getContext(), videoList);
        recyclerView.setAdapter(adapter);

        //get movie object
        Movie movie = getArguments().getParcelable(MOVIE);

        //initialize data set
        crewList = new ArrayList<>();
        //fetch list of language code and corresponding names
        HashMap<String, String> languageMap = Utils.fetchAllLanguages(getContext());

        //initialize retrofit client and call object that wraps the response
        MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);
        //invoke movie credits call passing the movie id and API KEY
        Call<CreditResults> creditResultsCall = client.getMovieCredits(((Movie) getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
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

        Call<DetailResults> detailResultsCall = client.getMovieDetails(((Movie) getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        detailResultsCall.enqueue(new Callback<DetailResults>() {
            @Override
            public void onResponse(@NonNull Call<DetailResults> call, @NonNull Response<DetailResults> response) {
                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body().getHomepage() == null) {
                    return;
                }
                //update view with the fetched data
                homepage.setText(response.body().getHomepage());
                originalTitle.setText(response.body().getOriginalTitle());
            }

            @Override
            public void onFailure(@NonNull Call<DetailResults> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.error_movie_details, Toast.LENGTH_SHORT).show();
            }
        });

        Call<VideoResults> videoResultsCall = client.getMovieVideos(((Movie) getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        videoResultsCall.enqueue(new Callback<VideoResults>() {
            @Override
            public void onResponse(@NonNull Call<VideoResults> call, @NonNull Response<VideoResults> response) {
                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body().getVideos() == null) {
                    return;
                }

                //update data set and notify the adapter
                if (response.body().getVideos().size() > 0) {
                    videoList.addAll(response.body().getVideos());
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(@NonNull Call<VideoResults> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.error_movie_trailers, Toast.LENGTH_SHORT).show();
            }
        });

        if (movie != null) {
            plotTextView.setText(movie.getPlot());
            releaseDateTextView.setText(movie.getReleaseDate());
            String rating = movie.getUserRating() == 0 ? getString(R.string.no_ratings) : DecimalFormat.getNumberInstance().format(movie.getUserRating()).concat("/10");
            tmdbRating.setText(rating);
            ratingBar.setRating((float) (movie.getUserRating() / 2f));
            popularity.setText(DecimalFormat.getNumberInstance().format(movie.getPopularity()));
            language.setText(languageMap.get(movie.getLanguage()));
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
