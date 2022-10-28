package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Connections;
import bgu.spl.net.api.ConnectionsImpl;
import bgu.spl.net.api.bidi.Messages.BidiMessage;
import bgu.spl.net.srv.ConnectionHandler;

import java.time.LocalDateTime;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<BidiMessage> {
    private int id;
   // private ConnectionsImpl connections;
    private Connections connections;
    private  boolean shouldTerminate = false;
    private ClientMessage myClientMessage;

    public BidiMessagingProtocolImpl(){}

    public void start(int connectionId, Connections<BidiMessage> newConnections) {//TODO WHAT WE DO HERE?
        id = connectionId;
        connections = newConnections;
        //connections = ConnectionsImpl.getInstance();
        myClientMessage = new ClientMessage(ConnectionsImpl.getInstance(),connectionId);
    }

    @Override
    public void process(BidiMessage msg) {
        // MessageEncoderDecoderImpl myEnDec = (MessageEncoderDecoderImpl) myHandler.getEncdec();
        //MessageEncoderDecoderImpl encdec = myHandler.getEncdec();
        myClientMessage.readMessage(msg);
//        if (msg.getOpcode() == 3) {
//            shouldTerminate = true;
//
//        }
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}


