package bgu.spl.net.api.bidi.Messages;

public class LogoutMessage implements BidiMessage {
    final int opcode = 3;

    public LogoutMessage() {
    }
    @Override
    public int getOpcode() {
        return opcode;
    }

}
