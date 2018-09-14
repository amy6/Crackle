package example.com.crackle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import example.com.crackle.R;
import example.com.crackle.model.Image;

import static example.com.crackle.utils.Constants.IMAGE_URL_SIZE;
import static example.com.crackle.utils.Constants.LOG_TAG;

public class MovieImageAdapter extends PagerAdapter {

    private Context context;
    private List<Image> imageUrl;

    public MovieImageAdapter(Context context, List<Image> imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_movie_image, container, false);

        ImageView imageView = view.findViewById(R.id.backdrop_image);
        Glide.with(context)
                .load(IMAGE_URL_SIZE + imageUrl.get(position).getFilePath())
                .into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return imageUrl != null ? imageUrl.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
