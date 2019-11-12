package example.com.crackle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.model.Image;
import example.com.crackle.utils.Constants;
import example.com.crackle.utils.Utils;

public class MovieImageAdapter extends PagerAdapter {

    private Context context;
    private List<Image> imageUrl;

    @BindView(R.id.backdrop_image)
    ImageView imageView;

    public MovieImageAdapter(Context context, List<Image> imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    /**
     * called by the adapter to instantiate individual viewpager view data
     *
     * @param container parent view group for the view to be inflated
     * @param position  position of the inflated view
     * @return reference to inflated view
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_movie_image, container, false);

        ButterKnife.bind(this, view);

        //set up url for backdrop images
        String backdropImageUrl = Constants.IMAGE_URL_SIZE.concat(imageUrl.get(position).getFilePath()
                != null ? imageUrl.get(position).getFilePath() : "");
        Glide.with(context)
                .setDefaultRequestOptions(Utils.setupGlide(Constants.BACKDROP_IMG))
                .load(backdropImageUrl)
                .into(imageView);

        //add inflated view to viewpager
        container.addView(view);

        return view;
    }

    /**
     * called by the ViewPager to know the number of fragments to be displayed
     *
     * @return number of ViewPager fragments
     */
    @Override
    public int getCount() {
        return imageUrl != null ? imageUrl.size() : 0;
    }

    /**
     * determines if the view is associated with the object (that is returned in instantiateitem method)
     *
     * @param view   reference to the view that needs to checked against the object
     * @param object reference to the key object that needs to be checked against the view
     * @return boolean indicating whether the view is associated with the key object
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    /**
     * removes a view from the given page
     *
     * @param container reference to the containing viewgroup that holds the view to be removed
     * @param position  position of the view to be removed
     * @param object    reference to the assoicated object returned in instantiateitem method
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
