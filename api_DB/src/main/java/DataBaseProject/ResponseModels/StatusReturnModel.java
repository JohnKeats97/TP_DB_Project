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

    public Integer getForum() {
        return this.forum;
    }

    public void setForum(Integer forum) {
        this.forum = forum;
    }

    public Integer getPost() {
        return this.post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getThread() {
        return this.thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public Integer getUser() {
        return this.user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}