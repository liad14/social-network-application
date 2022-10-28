package bgu.spl.net.api.bidi.Messages;

public class LogstatMessage implements BidiMessage {

    final int opcode = 7;
    public LogstatMessage(){}

    @Override
    public int getOpcode() {
        return opcode;
    }
}
