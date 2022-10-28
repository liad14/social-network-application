package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.Messages.*;
//import bgu.spl.net.api.bidi.Messages.BidiMessage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<BidiMessage> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int currentOpcode;


//    public static void main(String[] args) {
//        String output;
//        String toCheck = "check this";
//        bytes   encode(toCheck);
//    }

    @Override
    public BidiMessage decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        //System.out.println("decode next byte is: "+ nextByte);

        if (nextByte == ';') {
            return popMessage();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }


    @Override
    public byte[] encode(BidiMessage MessageToSend) {
        byte [] opcode_a = new byte[2];
        byte [] opcode_msg = new  byte[2];
        byte[] ans = null;
        byte[] msg;
        if (MessageToSend.getOpcode()==9) {//notification
            NotificationMessage n = (NotificationMessage) MessageToSend;
            String content = n.getContent();
            int opcode = n.getOpcode();
            int pm_public = n.getPM_Public();
            String posting_user = n.getPostingUser();
            String output =  pm_public + posting_user + "\0" + content + "\0" + ";";
            msg = (output).getBytes(StandardCharsets.UTF_8);
            opcode_a = shortToBytes((short) opcode);
            ans = Arrays.copyOf(opcode_a, opcode_a.length + msg.length);
            System.arraycopy(msg, 0, ans, opcode_a.length, msg.length);

        }
        else if (MessageToSend.getOpcode()== 10) {//ack
           AckMessage ackMessage = (AckMessage) MessageToSend;
           int opcode = ackMessage.getOpcode();
           int opcode_2 = ackMessage.getMessageOpcode();
            opcode_a = shortToBytes((short) opcode);
            opcode_msg = shortToBytes((short) opcode_2);

           if (opcode == 3){
               ans = Arrays.copyOf(opcode_a, opcode_a.length + opcode_msg.length);
               System.arraycopy(opcode_msg, 0, ans, opcode_a.length, opcode_msg.length);
           }
           else {
               String content = "";
               content = content + ackMessage.getAdditionalMsg()+";";
               msg = content.getBytes(StandardCharsets.UTF_8);
               byte[] a_b = Arrays.copyOf(opcode_a, opcode_a.length + opcode_msg.length);
               System.arraycopy(opcode_msg, 0, a_b, opcode_a.length, opcode_msg.length);
               ans = Arrays.copyOf(a_b, a_b.length + msg.length);
               System.arraycopy(msg, 0, ans, a_b.length, msg.length);
           }
        }
        else { // (MessageToSend.getOpcode()== 11) {//error {
            Error_Message error_message = (Error_Message) MessageToSend;
            int opcode = error_message.getOpcode();
            int opcode_2 = error_message.getMessageOpcode();
            String content = ";";
            opcode_a = shortToBytes((short) opcode);
            opcode_msg = shortToBytes((short) opcode_2);
            msg = content.getBytes(StandardCharsets.UTF_8);
            byte[] a_b = Arrays.copyOf(opcode_a, opcode_a.length + opcode_msg.length);
            System.arraycopy(opcode_msg, 0, a_b, opcode_a.length, opcode_msg.length);
            ans = Arrays.copyOf(a_b, a_b.length + msg.length);
            System.arraycopy(msg, 0, ans, a_b.length, msg.length);

        }


        return ans;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private BidiMessage popMessage() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
       // byte[] opCode = Arrays.copyOfRange(bytes, 0, 1);
        BidiMessage output = null;
        byte[] opCode = Arrays.copyOfRange(bytes, 0, 2);
        int Opcode = bytesToShort(opCode);
        //bytesToShort(bytes);
        String msg = null;
        bytes = Arrays.copyOfRange(bytes, 2, len);//cut the opcode
        len -= 2;
            msg = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;

        if (Opcode == 1){
            int nextSpace = msg.indexOf("\0");
            String userName = msg.substring(0, nextSpace);
            msg = msg.substring(nextSpace+1);
            nextSpace = msg.indexOf("\0");
            String password = msg.substring(0, nextSpace);
            msg = msg.substring(nextSpace+1);
            nextSpace = msg.indexOf("\0");
            String birthday = msg.substring(0, nextSpace);
            output =  new RegisterMessage(userName,password,birthday);
        }
        else if (Opcode == 2){
            int nextSpace = msg.indexOf("\0");
            String userName = msg.substring(0, nextSpace);
            msg = msg.substring(nextSpace+1);
            nextSpace = msg.indexOf("\0");
            String password = msg.substring(0, nextSpace);
            msg = msg.substring(nextSpace+1);
            int captcha = Integer.parseInt(msg);
            output =  new LoginMessage(userName, password, captcha);
        }
        else if(Opcode==3)
           output = new LogoutMessage();
        else if(Opcode==4){
            int Folow_unfollow = Integer.parseInt(String.valueOf(msg.charAt(0)));
            msg = msg.substring(1);
            String userName = msg.substring(0, msg.indexOf("\0"));
            output = new FollowMessage(userName,Folow_unfollow);
        }
        else if(Opcode ==5){
            msg = msg.substring(0, msg.indexOf("\0"));
            output =  new PostMessage(msg);
        }
        else if(Opcode ==6){
            String findFilter = msg;
            int next0 = findFilter.indexOf("\0");
            String userName = findFilter.substring(0, next0);
            findFilter = findFilter.substring(next0+1);
            next0 = findFilter.indexOf("\0");
            String content = findFilter.substring(0, next0);
            findFilter = findFilter.substring(next0+1);
            next0 = findFilter.indexOf("\0");
            String dateAndTime = findFilter;
            output = new PMMessage(userName,content,dateAndTime);
        }
        else if(Opcode==7){
            output =  new LogstatMessage();
        }
        else if (Opcode==8){
            int endOfList = msg.indexOf("\0");
            String listOfUserName = msg.substring(0, endOfList);
            output =  new StatMessage(listOfUserName);
        }
        if (Opcode == 12){
            int end = msg.indexOf("\0");
            String UserName = msg.substring(0, end - 1);
            output =  new BlockMessage(UserName);
        }
        return output;
    }
}

