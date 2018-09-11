package example.com.crackle.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.model.Video;

import static example.com.crackle.utils.Constants.LOG_TAG;

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.VideoViewHolder> {

    public static final String YOUTUBE_IMG_BASE_URI = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_IMG_EXTENSION = "/mqdefault.jpg";

    private Context context;
    private List<Video> videos;

    public MovieVideoAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_movie_trailer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        if (video.getSite().equalsIgnoreCase("youtube")) {
            Uri uri = Uri.parse(YOUTUBE_IMG_BASE_URI + video.getKey() + YOUTUBE_IMG_EXTENSION);
            Log.d(LOG_TAG, "Uri is : " + uri.toString());
            Glide.with(context)
                    .load(uri)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return videos != null ? videos.size() : 0;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView imageView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
