package bgu.spl.net.api.bidi.Messages;

public class NotificationMessage implements BidiMessage {

    final int opcode = 9;
    int PM_Public;
    String postingUser;
    String content;

    public NotificationMessage(int PM_Public, String postingUser, String content) {
        this.PM_Public = PM_Public;
        this.postingUser = postingUser;
        this.content = content;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getPM_Public() {
        return PM_Public;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}
