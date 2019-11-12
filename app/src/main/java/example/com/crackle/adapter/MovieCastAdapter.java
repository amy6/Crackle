package example.com.crackle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import example.com.crackle.R;
import example.com.crackle.model.Cast;
import example.com.crackle.utils.Constants;
import example.com.crackle.utils.Utils;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder> {

    private Context context;
    private List<Cast> castList;

    public MovieCastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    /**
     * returns a cast item view
     *
     * @param parent   reference to the parent view group - which is the RecyclerView in this case
     * @param viewType indicates the type of view to be returned
     * @return new view holder associated with the item view
     */
    @NonNull
    @Override
    public MovieCastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieCastViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_cast_list_item, parent, false));
    }

    /**
     * binds the views with the data
     *
     * @param holder   reference to the view holder
     * @param position position of the view to be modified
     */
    @Override
    public void onBindViewHolder(@NonNull final MovieCastViewHolder holder, int position) {
        //get the current cast
        Cast cast = castList.get(position);
        //update view data
        String imageUrl = Constants.IMAGE_URL_SIZE.concat(cast.getProfileUrl() != null ? cast.getProfileUrl() : "");
        Glide.with(context)
                .setDefaultRequestOptions(Utils.INSTANCE.setupGlide(Constants.CAST_IMG))
                .load(imageUrl)
                .into(holder.profileImage);
        holder.name.setText(cast.getName());
    }

    /**
     * called by the RecyclerView to get the number of items to be displayed
     *
     * @return number of items to be displayed by the RecyclerView
     */
    @Override
    public int getItemCount() {
        return castList == null ? 0 : castList.size();
    }

    //ViewHolder for cast item view to help reduce findViewById calls
    class MovieCastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profileImage)
        CircleImageView profileImage;
        @BindView(R.id.name)
        TextView name;

        MovieCastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
