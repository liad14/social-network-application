package bgu.spl.net.api.bidi.Messages;

public class FollowMessage implements BidiMessage {
    final int opcode = 4;
    String followName;
    int follow_unfollow;
    public FollowMessage(String followName, int follow_unfollow ){
       this.followName = followName;
       this.follow_unfollow =follow_unfollow;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getFollowName() {
        return followName;
    }

    public int getFollow_unfollow() {
        return follow_unfollow;
    }
}
