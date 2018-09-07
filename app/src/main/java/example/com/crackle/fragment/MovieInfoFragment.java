package example.com.crackle.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.CreditResults;
import example.com.crackle.model.Crew;
import example.com.crackle.model.DetailResults;
import example.com.crackle.model.Movie;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
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

    private MovieApiClient client;
    private Call<CreditResults> creditResultsCall;
    private Call<DetailResults> detailResultsCall;
    private List<Crew> crewList;
    private HashMap<String, String> languageMap;


    public MovieInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Movie movie = getArguments().getParcelable(MOVIE);
        ButterKnife.bind(this, view);

        crewList = new ArrayList<>();
        languageMap = Utils.fetchAllLanguages(getContext());

        client = MovieApiService.getClient().create(MovieApiClient.class);
        creditResultsCall = client.getMovieCredits(((Movie)getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        creditResultsCall.enqueue(new Callback<CreditResults>() {
            @Override
            public void onResponse(Call<CreditResults> call, Response<CreditResults> response) {
                if (response.body() == null || response.body().getCrewList() == null || response.body().getCrewList().size() == 0) {
                    return;
                }
                crewList.addAll(response.body().getCrewList());
                directorTextView.setText("");
                for (int i = 0; i < crewList.size(); i ++) {
                    if (crewList.get(i).getJob().equalsIgnoreCase("director")) {
                        directorTextView.append(crewList.get(i).getName());
                        if (i < crewList.size() - 1 && crewList.get(i+1).getJob().equalsIgnoreCase("director")) {
                            directorTextView.append("\n");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreditResults> call, Throwable t) {
                Toast.makeText(getContext(), "Error getting movie director details", Toast.LENGTH_SHORT).show();
            }
        });

        detailResultsCall = client.getMovieDetails(((Movie)getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        detailResultsCall.enqueue(new Callback<DetailResults>() {
            @Override
            public void onResponse(Call<DetailResults> call, Response<DetailResults> response) {
                if (response.body() == null || response.body().getHomepage() == null) {
                    return;
                }
                homepage.setText(response.body().getHomepage());
            }

            @Override
            public void onFailure(Call<DetailResults> call, Throwable t) {
                Toast.makeText(getContext(), "Error getting movie details", Toast.LENGTH_SHORT).show();
            }
        });

        plotTextView.setText(movie.getPlot());
        releaseDateTextView.setText(movie.getReleaseDate());
        String rating = movie.getUserRating() == 0 ? "No ratings" : DecimalFormat.getNumberInstance().format(movie.getUserRating()).concat("/10");
        tmdbRating.setText(rating);
        ratingBar.setRating((float) (movie.getUserRating()/2f));
        popularity.setText(DecimalFormat.getNumberInstance().format(movie.getPopularity()));
        language.setText(languageMap.get(movie.getLanguage()));
    }


    public static Fragment newInstance(Movie movie) {
        MovieInfoFragment fragment = new MovieInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
