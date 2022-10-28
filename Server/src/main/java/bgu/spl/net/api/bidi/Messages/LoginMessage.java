package bgu.spl.net.api.bidi.Messages;

public class LoginMessage implements BidiMessage {
    final int opcode = 2;
    String userName;
    String password;
    int captcha;

    public LoginMessage(String userName, String password, int captcha){
        this.userName = userName;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }

    public int getCaptcha() {
        return captcha;
    }
}
