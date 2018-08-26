package example.com.crackle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResults {

    @SerializedName("results")
    private List<Review> reviewList;
    @SerializedName("total_results")
    private int totalResults;

    public ReviewResults(List<Review> reviewList, int totalResults) {
        this.reviewList = reviewList;
        this.totalResults = totalResults;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
