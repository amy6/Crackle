package example.com.crackle;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static example.com.crackle.Constants.IMAGE_URL_SIZE;
import static example.com.crackle.Constants.LOG_TAG;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int PROGRESS = 1;

    private Context context;
    private List<Movie> movies;

    private int itemCount;
    private int lastVisibleItemPosition;
    private int viewThreshold = 1;

    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading = false;

    public MovieAdapter(Context context, List<Movie> movies, RecyclerView recyclerView) {
        this.context = context;
        this.movies = movies;

        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (layoutManager != null) {

                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (getItemViewType(position)) {
                                case ITEM:
                                    return 1;
                                case PROGRESS:
                                    return 2;
                                default:
                                    return -1;
                            }
                        }
                    });

                    itemCount = layoutManager.getItemCount();
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (!isLoading() && itemCount <= (lastVisibleItemPosition + viewThreshold)) {
                        Log.d(LOG_TAG, "End Reached");
                        if (onLoadMoreListener != null) {
                            Log.d(LOG_TAG, "calling OnLoadMore method");
                            onLoadMoreListener.onLoadMore();
                        }
                        setLoading(true);
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                View itemView = LayoutInflater.from(context).inflate(R.layout.layout_movie_item, parent, false);
                viewHolder = new MovieViewHolder(itemView);
                break;
            case PROGRESS:
                View progressView = LayoutInflater.from(context).inflate(R.layout.layout_progress_item, parent, false);
                viewHolder = new ProgressViewHolder(progressView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MovieViewHolder) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            Movie movie = movies.get(position);
            String imageUrl = IMAGE_URL_SIZE.concat(movie.getImageUrl());
            Glide.with(context)
                    .load(imageUrl)
                    .into(movieViewHolder.imageView);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return movies.get(position) != null ? ITEM : PROGRESS;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poster_image)
        ImageView imageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = movies.get(getAdapterPosition());
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, movie);
            v.getContext().startActivity(intent);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addLoader(Movie movie) {
        movies.add(movie);
        notifyItemInserted(movies.size() - 1);
    }

    public void removeLoader(Movie movie) {
        movies.remove(movie);
        notifyItemRemoved(movies.size() - 1);
    }


    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
