package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class PostsSortedModel {
    private List<PostModel> posts;
    private String marker;

    @JsonCreator
    public PostsSortedModel(@JsonProperty("marker") final String marker,
                            @JsonProperty("posts") final List<PostModel> posts) {
        this.marker = marker;
        this.posts = posts;
    }

    public final List<PostModel> getPosts() {
        return this.posts;
    }

    public void setPosts(final List<PostModel> posts) {
        this.posts = posts;
    }

    public final String getMarker() {
        return this.marker;
    }

    public void setMarker(final String marker) {
        this.marker = marker;
    }
}