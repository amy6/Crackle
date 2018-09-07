package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
