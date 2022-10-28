package bgu.spl.net.api.bidi.Messages;

public class BlockMessage implements BidiMessage  {

    String userToBlock;
    int opcode = 12;
    public BlockMessage(String userToBlock) {
        this.userToBlock = userToBlock;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }

    public String getUserToBlock() {
        return userToBlock;
    }
}
