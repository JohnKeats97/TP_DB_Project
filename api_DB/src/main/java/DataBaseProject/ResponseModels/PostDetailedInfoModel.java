package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PostDetailedInfoModel {
    private UserModel author;
    private ForumModel forum;
    private PostModel post;
    private ThreadModel thread;

    @JsonCreator
    public PostDetailedInfoModel(@JsonProperty("author") UserModel author,
                                 @JsonProperty("forum") ForumModel forum,
                                 @JsonProperty("post") PostModel post,
                                 @JsonProperty("thread") ThreadModel thread) {
        this.author = author;
        this.forum = forum;
        this.post = post;
        this.thread = thread;
    }

    public UserModel getAuthor() {
        return this.author;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    public ForumModel getForum() {
        return this.forum;
    }

    public void setForum(ForumModel forum) {
        this.forum = forum;
    }

    public PostModel getPost() {
        return this.post;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

    public ThreadModel getThread() {
        return this.thread;
    }

    public void setThread(ThreadModel thread) {
        this.thread = thread;
    }
}
