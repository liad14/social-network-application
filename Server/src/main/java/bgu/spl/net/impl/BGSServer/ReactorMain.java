package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
       // SharedData Data = new SharedData(); //one shared object


        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                7777, //port
                BidiMessagingProtocolImpl::new, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}
