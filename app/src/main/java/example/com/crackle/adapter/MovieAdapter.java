package example.com.crackle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.activity.MainActivity;
import example.com.crackle.activity.MovieDetailsActivity;
import example.com.crackle.listener.OnLoadMoreListener;
import example.com.crackle.model.Movie;
import example.com.crackle.utils.Constants;
import example.com.crackle.utils.Utils;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

        //get reference to layout manager
        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        //register scroll listener on RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (layoutManager != null) {
                    //set span size depending on the type of view being displayed
                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (getItemViewType(position)) {
                                case Constants.ITEM:
                                    return 1;
                                case Constants.PROGRESS:
                                    return layoutManager.getSpanCount();
                                default:
                                    return -1;
                            }
                        }
                    });

                    //get total number of items currently displayed
                    itemCount = layoutManager.getItemCount();
                    //find the last visible item position
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    //if RecyclerView is scrolled to end, call load more to fetch next page of results
                    if (!loading && itemCount <= (lastVisibleItemPosition + viewThreshold)) {
                        Log.d(Constants.LOG_TAG, "RecyclerView has been scrolled to end");
                        if (onLoadMoreListener != null) {
                            Log.d(Constants.LOG_TAG, "Listener is active");
                            onLoadMoreListener.onLoadMore();
                        }
                        setLoading(true);
                    }
                }
            }
        });

    }

    /**
     * returns a view - item view or a progress indicator view to be displayed in the RecyclerView
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates whether the view is an individual item view or a progress indicator view
     * @return new view holder associated with the item view
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        //inflate the views based on view type
        switch (viewType) {
            case Constants.ITEM:
                View itemView = LayoutInflater.from(context).inflate(R.layout.layout_movie_item, parent, false);
                viewHolder = new MovieViewHolder(itemView);
                break;
            case Constants.PROGRESS:
                View progressView = LayoutInflater.from(context).inflate(R.layout.layout_progress_item, parent, false);
                viewHolder = new ProgressViewHolder(progressView);
                break;
        }
        return viewHolder;
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        //binding logic for movie item view
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            //get the current movie
            Movie movie = movies.get(position);
            //update view data
            String imageUrl = Constants.IMAGE_URL_SIZE.concat(movie.getImageUrl() != null ? movie.getImageUrl() : "");
            Glide.with(context)
                    .setDefaultRequestOptions(Utils.setupGlide(Constants.POSTER_IMG))
                    .load(imageUrl)
                    //set up a listener to handle success and failure of image loading
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((MovieViewHolder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((MovieViewHolder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(movieViewHolder.imageView);
        } else {
            //set progress indicator for loading view type - used for pagination
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    /**
     * called by the RecyclerView to get the item type to be displayed
     *
     * @param position position of the item that is needed by the RecyclerView
     * @return type of the view - movie item view or progress indicator view
     */
    @Override
    public int getItemViewType(int position) {
        return movies.get(position) != null ? Constants.ITEM : Constants.PROGRESS;
    }

    /**
     * called to update data set
     *
     * @param movies list of new data
     */
    public void setData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    /**
     * clear all data
     */
    public void clear() {
        this.movies.clear();
        notifyDataSetChanged();
    }

    /**
     * add data fetched from API
     *
     * @param movies list of fetched data
     */
    public void addAll(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    //ViewHolder for movie item view to help reduce findViewById calls
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poster_image)
        ImageView imageView;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        //define on click listener for movie item view to transition to details screen
        @Override
        public void onClick(View v) {
            Movie movie = movies.get(getAdapterPosition());
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            //pass the movie object data
            intent.putExtra(Intent.EXTRA_TEXT, movie);
            ActivityOptionsCompat options;
            //define shared element transition for the movie poster image
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptionsCompat.makeSceneTransitionAnimation((MainActivity) context, imageView, imageView.getTransitionName());
                v.getContext().startActivity(intent, options.toBundle());
            } else {
                v.getContext().startActivity(intent);
            }

        }
    }

    //ViewHolder for progress indicator view to help reduce findViewById calls
    class ProgressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * adds a progress indicator at the bottom of the RecyclerView while getting next page results
     *
     * @param movie reference to movie object to be added to data set (in this case a null)
     */
    public void addLoader(Movie movie) {
        movies.add(movie);
        new Handler().post(() -> {
            //notify the adapter of the change in data set
            notifyItemInserted(movies.size() - 1);
        });

    }

    /**
     * removes the progress indicator at the bottom of the RecyclerView after getting a new page's results
     *
     * @param movie reference to movie object to be removed to data set (in this case a null)
     */
    public void removeLoader(Movie movie) {
        movies.remove(movie);
        //notify the adapter of the change in data set
        notifyItemRemoved(movies.size() - 1);
    }


    /**
     * setter for loading flag that indicates whether a new page data is being loaded
     *
     * @param loading flag indicating data loading
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * register pagination listener
     *
     * @param onLoadMoreListener reference to listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
