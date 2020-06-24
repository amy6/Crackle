package example.com.crackle.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import butterknife.BindView
import butterknife.ButterKnife
import example.com.crackle.R
import example.com.crackle.model.Review
import example.com.crackle.utils.Constants

class MovieReviewAdapter(private val context: Context, private val reviewList: List<Review>?) : RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder>() {
    private var expandable: Boolean = false

    /**
     * returns a review item view
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates the type of view to be returned
     * @return new view holder associated with the item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieReviewViewHolder {
        return MovieReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_review_list_item, parent, false))
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    override fun onBindViewHolder(holder: MovieReviewViewHolder, position: Int) {
        //get the current review
        val review = reviewList!![position]
        //update view data
        holder.author!!.text = review.author
        holder.content!!.text = review.content

        //get the total line count for the review TextView and set the maxlines accordingly
        // to expand and collapse cardview wrapping the review content
        holder.content!!.post {
            val lineCount = holder.content!!.lineCount

            if (lineCount > Constants.MAX_LINES) {
                //initially display the content in the set max number of lines
                holder.content!!.maxLines = Constants.MAX_LINES
                holder.content!!.ellipsize = TextUtils.TruncateAt.END
                //show a button to enable expanding truncated text
                holder.button!!.visibility = View.VISIBLE

                //set click listener on the "show more" button
                holder.button!!.setOnClickListener {
                    if (expandable) {
                        expandable = false
                        //extent the max lines to be able to display the whole review content
                        //if "show more" is clicked
                        holder.content!!.maxLines = lineCount
                        holder.button!!.setImageResource(R.drawable.ic_keyboard_arrow_up)
                    } else {
                        expandable = true
                        //reset the max lines when "show less" is clicked
                        holder.content!!.maxLines = Constants.MAX_LINES
                        holder.button!!.setImageResource(R.drawable.ic_keyboard_arrow_down)
                    }
                }
            } else {
                //hide the button if the review content lines is not more than the set max lines property
                holder.button!!.visibility = View.GONE
            }
        }
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    override fun getItemCount(): Int {
        return reviewList?.size ?: 0
    }

    //ViewHolder for review item view to help reduce findViewById calls
    inner class MovieReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.author)
        var author: TextView? = null
        @JvmField
        @BindView(R.id.content)
        var content: TextView? = null
        @JvmField
        @BindView(R.id.button)
        var button: ImageButton? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
