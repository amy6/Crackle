package example.com.crackle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    private List<Movie> movies;
    private MovieAdapter movieAdapter;
    private MovieApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //resolve references to view
        ButterKnife.bind(this);

        movies = new ArrayList<>();

        movieAdapter = new MovieAdapter(movies);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdapter);

        client = MovieApiService.getClient().create(MovieApiClient.class);
        Call<MovieResults> call = client.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(@NonNull Call<MovieResults> call, @NonNull Response<MovieResults> response) {
                movies.addAll(response.body().getMovies());
                movieAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResults> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching movies", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
