package example.com.crackle.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.model.Review;

import static example.com.crackle.utils.Constants.MAX_LINES;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private boolean expandable;

    public MovieReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    /**
     * returns a review item view
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates the type of view to be returned
     * @return new view holder associated with the item view
     */
    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_review_list_item, parent, false));
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    @Override
    public void onBindViewHolder(@NonNull final MovieReviewViewHolder holder, int position) {
        //get the current review
        Review review = reviewList.get(position);
        //update view data
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        //get the total line count for the review TextView and set the maxlines accordingly
        // to expand and collapse cardview wrapping the review content
        holder.content.post(new Runnable() {
            @Override
            public void run() {
                final int lineCount = holder.content.getLineCount();

                if (lineCount > MAX_LINES) {
                    //initially display the content in the set max number of lines
                    holder.content.setMaxLines(MAX_LINES);
                    holder.content.setEllipsize(TextUtils.TruncateAt.END);
                    //show a button to enable expanding truncated text
                    holder.button.setVisibility(View.VISIBLE);

                    //set click listener on the "show more" button
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (expandable) {
                                expandable = false;
                                //extent the max lines to be able to display the whole review content
                                //if "show more" is clicked
                                holder.content.setMaxLines(lineCount);
                                holder.button.setImageResource(R.drawable.ic_keyboard_arrow_up);
                            } else {
                                expandable = true;
                                //reset the max lines when "show less" is clicked
                                holder.content.setMaxLines(MAX_LINES);
                                holder.button.setImageResource(R.drawable.ic_keyboard_arrow_down);
                            }

                        }
                    });
                } else {
                    //hide the button if the review content lines is not more than the set max lines property
                    holder.button.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    //ViewHolder for review item view to help reduce findViewById calls
    class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.button)
        ImageButton button;

        MovieReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
