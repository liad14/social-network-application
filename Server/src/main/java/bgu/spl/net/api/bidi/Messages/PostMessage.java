package bgu.spl.net.api.bidi.Messages;

public class PostMessage implements BidiMessage {
    final int opcode = 5;
    String content;
    public PostMessage(String content){
        this.content = content;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getContent() {
        return content;
    }
}
