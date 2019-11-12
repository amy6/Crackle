package example.com.crackle.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import example.com.crackle.R
import example.com.crackle.model.Cast
import example.com.crackle.utils.Constants
import example.com.crackle.utils.Utils

class MovieCastAdapter(private val context: Context, private val castList: List<Cast>?) : RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder>() {

    /**
     * returns a cast item view
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates the type of view to be returned
     * @return new view holder associated with the item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCastViewHolder {
        return MovieCastViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_cast_list_item, parent, false))
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    override fun onBindViewHolder(holder: MovieCastViewHolder, position: Int) {
        //get the current cast
        val cast = castList!![position]
        //update view data
        val imageUrl = Constants.IMAGE_URL_SIZE + (cast.profileUrl ?: "")
        Glide.with(context)
                .setDefaultRequestOptions(Utils.setupGlide(Constants.CAST_IMG))
                .load(imageUrl)
                .into(holder.profileImage!!)
        holder.name!!.text = cast.name
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    override fun getItemCount(): Int {
        return castList?.size ?: 0
    }

    //ViewHolder for cast item view to help reduce findViewById calls
    inner class MovieCastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.profileImage)
        var profileImage: CircleImageView? = null
        @JvmField
        @BindView(R.id.name)
        var name: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
