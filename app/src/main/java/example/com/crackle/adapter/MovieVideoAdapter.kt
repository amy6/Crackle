package example.com.crackle.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import butterknife.BindView
import butterknife.ButterKnife
import example.com.crackle.R
import example.com.crackle.model.Video
import example.com.crackle.utils.Constants
import example.com.crackle.utils.Utils

class MovieVideoAdapter(private val context: Context, private val videos: List<Video>?) : RecyclerView.Adapter<MovieVideoAdapter.VideoViewHolder>() {

    /**
     * returns a view - item view or a progress indicator view to be displayed in the RecyclerView
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates whether the view is an individual item view or a progress indicator view
     * @return new view holder associated with the item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_movie_trailer, parent, false))
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        //get current video item
        val video = videos!![position]
        if (video.site.equals(Constants.SITE_FILTER_YOUTUBE, ignoreCase = true)) {
            //set up url for thumbnail
            val uri = Uri.parse(Constants.YOUTUBE_IMG_BASE_URI + video.key + Constants.YOUTUBE_IMG_EXTENSION)
            holder.name!!.text = video.title
            Glide.with(context)
                    .setDefaultRequestOptions(Utils.setupGlide(Constants.BACKDROP_IMG))
                    .load(if (video.key != null) uri else "")
                    .into(holder.imageView!!)
        }
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }

    //ViewHolder for review item view to help reduce findViewById calls
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        @JvmField
        @BindView(R.id.thumbnail)
        var imageView: ImageView? = null
        @JvmField
        @BindView(R.id.name)
        var name: TextView? = null
        @JvmField
        @BindView(R.id.play_button)
        var play: ImageButton? = null

        init {

            ButterKnife.bind(this, itemView)

            //set click listener on button/list item view play the video in youtube app/browser
            play!!.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            //get the current video
            val video = videos!![adapterPosition]
            if (video != null) {
                //initialize a new intent with action
                val intent = Intent(Intent.ACTION_VIEW)
                //set intent data
                intent.data = Uri.parse(Constants.YOUTUBE_VID_BASE_URI + video.key)
                //handle resolving intent
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    //display appropriate error message
                    Toast.makeText(context, R.string.error_video_play, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
