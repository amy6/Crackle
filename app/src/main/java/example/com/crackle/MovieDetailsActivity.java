package example.com.crackle;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

import static example.com.crackle.Constants.IMAGE_URL_SIZE;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getIntent() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                movie = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
                setTitle(movie.getTitle());

                title.setText(movie.getTitle());
                Glide.with(this)
                        .load(IMAGE_URL_SIZE.concat(movie.getImageUrl()))
                        .into(posterImage);
                ratingBar.setRating((float) movie.getUserRating());
            }
        }

        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(), movie));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
