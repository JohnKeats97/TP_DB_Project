package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ThreadModel {
    private String author;
    private String created;
    private String forum;
    private Integer id;
    private String message;
    private String slug;
    private String title;
    private Integer votes;

    @JsonCreator
    public ThreadModel(@JsonProperty("author") String author,
                       @JsonProperty("created") String created,
                       @JsonProperty("forum") String forum,
                       @JsonProperty("id") Integer id,
                       @JsonProperty("message") String message,
                       @JsonProperty("slug") String slug,
                       @JsonProperty("title") String title,
                       @JsonProperty("votes") Integer votes) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getForum() {
        return this.forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVotes() {
        return this.votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}