package bgu.spl.net.api.bidi.Messages;

public class PMMessage implements BidiMessage {
    String userName;
    String content;
    String dateAndTime;
    int opcode = 6;
   public PMMessage(String userName, String content, String dateAndTime){
       this.userName = userName;
       this.content = content;
       this.dateAndTime = dateAndTime;
   }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }
}
