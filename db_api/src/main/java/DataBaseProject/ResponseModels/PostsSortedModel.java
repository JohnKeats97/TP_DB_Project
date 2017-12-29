package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class PostsSortedModel {
    private List<PostModel> posts;
    private String marker;

    @JsonCreator
    public PostsSortedModel(@JsonProperty("marker") String marker,
                            @JsonProperty("posts") List<PostModel> posts) {
        this.marker = marker;
        this.posts = posts;
    }

    public List<PostModel> getPosts() {
        return this.posts;
    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }

    public String getMarker() {
        return this.marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}