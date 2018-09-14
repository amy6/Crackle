package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("file_path")
    private String filePath;

    public Image(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
