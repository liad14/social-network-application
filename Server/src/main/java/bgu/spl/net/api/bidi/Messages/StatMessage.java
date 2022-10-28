package bgu.spl.net.api.bidi.Messages;

public class StatMessage implements BidiMessage {

    final int opcode = 8;
    String usersList;

    public StatMessage(String usersList) {
        this.usersList = usersList;
    }

    public String getUsersList() {
        return usersList;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }
}

