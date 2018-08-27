package example.com.crackle;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.Constants.API_KEY;
import static example.com.crackle.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieInfoFragment extends Fragment {

    TextView plotTextView;
    TextView directorTextView;
    TextView releaseDateTextView;
    private MovieApiClient client;
    private Call<CreditResults> call;
    private List<Crew> crewList;


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

        plotTextView = view.findViewById(R.id.plot);
        directorTextView = view.findViewById(R.id.director);
        releaseDateTextView = view.findViewById(R.id.release_date);

        crewList = new ArrayList<>();

        client = MovieApiService.getClient().create(MovieApiClient.class);
        call = client.getMovieCredits(((Movie)getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        call.enqueue(new Callback<CreditResults>() {
            @Override
            public void onResponse(Call<CreditResults> call, Response<CreditResults> response) {
                crewList.addAll(response.body().getCrewList());
                for (int i = 0; i < crewList.size(); i ++) {
                    if (crewList.get(i).getJob().equalsIgnoreCase("director")) {
                        directorTextView.append(crewList.get(i).getName());
                        if (i < crewList.size() - 1) {
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

        plotTextView.setText(movie.getPlot());
        releaseDateTextView.setText(movie.getReleaseDate());
    }

    public static Fragment newInstance(Movie movie) {
        MovieInfoFragment fragment = new MovieInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
