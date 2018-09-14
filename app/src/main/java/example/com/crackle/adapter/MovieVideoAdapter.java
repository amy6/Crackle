package example.com.crackle.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.model.Video;

import static example.com.crackle.utils.Constants.SITE_FILTER_YOUTUBE;
import static example.com.crackle.utils.Constants.YOUTUBE_IMG_BASE_URI;
import static example.com.crackle.utils.Constants.YOUTUBE_IMG_EXTENSION;
import static example.com.crackle.utils.Constants.YOUTUBE_VID_BASE_URI;

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videos;

    public MovieVideoAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
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
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_movie_trailer, parent, false));
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        //get current video item
        Video video = videos.get(position);
        if (video.getSite().equalsIgnoreCase(SITE_FILTER_YOUTUBE)) {
            //set up url for thumbnail
            Uri uri = Uri.parse(YOUTUBE_IMG_BASE_URI + video.getKey()  + YOUTUBE_IMG_EXTENSION);
            holder.name.setText(video.getTitle());
            Glide.with(context)
                    .load(video.getKey() != null ? uri : "")
                    .into(holder.imageView);
        }
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    @Override
    public int getItemCount() {
        return videos != null ? videos.size() : 0;
    }

    //ViewHolder for review item view to help reduce findViewById calls
    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumbnail)
        ImageView imageView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.play_button)
        ImageButton play;

        VideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            //set click listener on button/list item view play the video in youtube app/browser
            play.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //get the current video
            Video video = videos.get(getAdapterPosition());
            if (video != null) {
                //initialize a new intent with action
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //set intent data
                intent.setData(Uri.parse(YOUTUBE_VID_BASE_URI + video.getKey()));
                //handle resolving intent
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    //display appropriate error message
                    Toast.makeText(context, R.string.error_video_play, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
