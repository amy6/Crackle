package example.com.crackle.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
import example.com.crackle.adapter.MovieCastAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.Cast;
import example.com.crackle.model.CreditResults;
import example.com.crackle.model.Movie;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.GRID_LAYOUT;
import static example.com.crackle.utils.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieCastFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private List<Cast> castList;


    public MovieCastFragment() {
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
        return inflater.inflate(R.layout.fragment_movie_cast, container, false);
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
        Utils.setupRecyclerView(getContext(), recyclerView, GRID_LAYOUT);

        //initialize data set and set up the adapter
        castList = new ArrayList<>();
        final MovieCastAdapter adapter = new MovieCastAdapter(getContext(), castList);
        recyclerView.setAdapter(adapter);

        //initialize retrofit client and call object that wraps the response
        MovieApiClient client = MovieApiService.getClient().create(MovieApiClient.class);
        //invoke movie credits call passing the movie id and API KEY
        Call<CreditResults> call = client.getMovieCredits(((Movie) getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        //invoke API call asynchronously
        call.enqueue(new Callback<CreditResults>() {
            @Override
            public void onResponse(@NonNull Call<CreditResults> call, @NonNull Response<CreditResults> response) {
                progressBar.setVisibility(View.GONE);
                //verify if the response body or the fetched results are empty/null
                if (response.body() == null || response.body().getCastList() == null) {
                    return;
                }

                //update data set, notify the adapter
                //update view visibility accordingly
                if (response.body().getCastList().size() > 0) {
                    castList.addAll(response.body().getCastList());
                    adapter.notifyDataSetChanged();
                    emptyTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResults> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.error_movie_cast, Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * return new instance of fragment with movie data passed in as arguments
     *
     * @param movie reference to movie object set as one of fragment's arguments
     * @return instance of fragment
     */
    public static Fragment newInstance(Movie movie) {
        MovieCastFragment fragment = new MovieCastFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
