package DataBaseProject.ResponseModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class VoteModel {
    private String nickname;
    private Integer voice;

    @JsonCreator
    public VoteModel(@JsonProperty("nickname") final String nickname,
                     @JsonProperty("voice") final Integer voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public final String getNickname() {
        return this.nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public final Integer getVoice() {
        return this.voice;
    }

    public void setVoice(final Integer voice) {
        this.voice = voice;
    }
}
