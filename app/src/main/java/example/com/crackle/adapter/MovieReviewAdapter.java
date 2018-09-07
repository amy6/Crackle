package example.com.crackle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        holder.content.post(new Runnable() {
            @Override
            public void run() {
                final int lineCount = holder.content.getLineCount();

                if (lineCount > MAX_LINES) {
                    holder.content.setMaxLines(MAX_LINES);
                    holder.content.setEllipsize(TextUtils.TruncateAt.END);
                    holder.button.setVisibility(View.VISIBLE);
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (expandable) {
                                expandable = false;
                                holder.content.setMaxLines(lineCount);
                                holder.button.setImageResource(R.drawable.ic_keyboard_arrow_up);
                            } else {
                                expandable = true;
                                holder.content.setMaxLines(MAX_LINES);
                                holder.button.setImageResource(R.drawable.ic_keyboard_arrow_down);
                            }

                        }
                    });
                } else {
                    holder.button.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }


    class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.button)
        ImageButton button;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
