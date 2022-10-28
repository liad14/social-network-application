package bgu.spl.net.api.bidi.Messages;

public class Error_Message implements BidiMessage {

    int opcode = 11;
    int messageOpcode;

    public Error_Message(int messageOpcodeToSet) {
        this.messageOpcode = messageOpcodeToSet;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }
}
