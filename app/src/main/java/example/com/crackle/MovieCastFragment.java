package example.com.crackle;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.Constants.API_KEY;
import static example.com.crackle.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieCastFragment extends Fragment {

//    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    TextView emptyTextView;

    private MovieApiClient client;
    private Call<CreditResults> call;
    private List<Cast> castList;


    public MovieCastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_cast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ButterKnife.bind(this, view);

        emptyTextView = view.findViewById(R.id.emptyTextView);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);

        castList = new ArrayList<>();
        final MovieCastAdapter adapter = new MovieCastAdapter(getContext(), castList);
        recyclerView.setAdapter(adapter);

        client = MovieApiService.getClient().create(MovieApiClient.class);
        call = client.getMovieCredits(((Movie)getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        call.enqueue(new Callback<CreditResults>() {
            @Override
            public void onResponse(Call<CreditResults> call, Response<CreditResults> response) {
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
            public void onFailure(Call<CreditResults> call, Throwable t) {
                Toast.makeText(getContext(), "Error getting movie cast", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public static Fragment newInstance(Movie movie) {
        MovieCastFragment fragment = new MovieCastFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}
