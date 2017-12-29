package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PostModel {
    private String author;
    private String created;
    private String forum;
    private Integer id;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private Integer thread;

    @JsonCreator
    public PostModel(@JsonProperty("author") String author,
                     @JsonProperty("created") String created,
                     @JsonProperty("forum") String forum,
                     @JsonProperty("id") Integer id,
                     @JsonProperty("isEdited") Boolean isEdited,
                     @JsonProperty("message") String message,
                     @JsonProperty("parent") Integer parent,
                     @JsonProperty("thread") Integer thread) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent == null ? 0 : parent;
        this.thread = thread;
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

    public Boolean getIsEdited() {
        return this.isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getParent() {
        return this.parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getThread() {
        return this.thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }
}