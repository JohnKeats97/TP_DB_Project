package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ForumModel {
    private Integer posts;
    private String slug;
    private Integer threads;
    private String title;
    private String user;

    @JsonCreator
    public ForumModel(@JsonProperty("posts") final Integer posts,
                      @JsonProperty("slug") final String slug,
                      @JsonProperty("threads") final Integer threads,
                      @JsonProperty("title") final String title,
                      @JsonProperty("user") final String user) {
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
    }


    public Integer getPosts() {
        return this.posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getThreads() {
        return this.threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}