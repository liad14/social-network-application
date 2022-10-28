package bgu.spl.net.api.bidi.Messages;

public class RegisterMessage implements BidiMessage {
    final int opcode = 1;
    String userName;
    String password;
    String birthday;
    public RegisterMessage(String userName, String password, String birthday){
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }
}
