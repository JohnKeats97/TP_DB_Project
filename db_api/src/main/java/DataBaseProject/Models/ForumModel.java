package DataBaseProject.Models;

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


    public final Integer getPosts() {
        return this.posts;
    }

    public void setPosts(final Integer posts) {
        this.posts = posts;
    }

    public final String getSlug() {
        return this.slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public final Integer getThreads() {
        return this.threads;
    }

    public void setThreads(final Integer threads) {
        this.threads = threads;
    }

    public final String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public final String getUser() {
        return this.user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

}