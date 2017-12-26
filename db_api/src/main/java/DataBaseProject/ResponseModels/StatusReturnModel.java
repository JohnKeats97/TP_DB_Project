package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;


public class StatusReturnModel {
    private Integer forum;
    private Integer post;
    private Integer thread;
    private Integer user;

    @JsonCreator
    public StatusReturnModel(@JsonProperty("forum") Integer forum,
                             @JsonProperty("post") Integer post,
                             @JsonProperty("thread") Integer thread,
                             @JsonProperty("user") Integer user) {
        this.forum = forum;
        this.post = post;
        this.thread = thread;
        this.user = user;
    }

    public final Integer getForum() {
        return this.forum;
    }

    public void setForum(final Integer forum) {
        this.forum = forum;
    }

    public final Integer getPost() {
        return this.post;
    }

    public void setPost(final Integer post) {
        this.post = post;
    }

    public final Integer getThread() {
        return this.thread;
    }

    public void setThread(final Integer thread) {
        this.thread = thread;
    }

    public final Integer getUser() {
        return this.user;
    }

    public void setUser(final Integer user) {
        this.user = user;
    }
}