package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResults {

    @SerializedName("results")
    private List<Review> reviewList;

    public ReviewResults(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }
}
