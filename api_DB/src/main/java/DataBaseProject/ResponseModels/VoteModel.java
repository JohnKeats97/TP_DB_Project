package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class VoteModel {
    private String nickname;
    private Integer voice;

    @JsonCreator
    public VoteModel(@JsonProperty("nickname") String nickname,
                     @JsonProperty("voice") Integer voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getVoice() {
        return this.voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }
}
