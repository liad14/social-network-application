package bgu.spl.net.api.bidi.Messages;

public class AckMessage implements BidiMessage {

    final int opcode = 10;
    int messageOpcode;
    String additionalMsg;

    public AckMessage(int messageOpcode, String additionalMsg) {
        this.messageOpcode = messageOpcode;
        this.additionalMsg = additionalMsg;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public String getAdditionalMsg() {
        return additionalMsg;
    }
}
