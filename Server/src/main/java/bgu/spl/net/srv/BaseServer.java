package bgu.spl.net.srv;

import bgu.spl.net.api.ConnectionsImpl;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.SharedData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {
    private int counter=0;
    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
    }


    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            Scanner myScanner = new Scanner(System.in);
            SharedData sharedData = SharedData.getInstance();
            System.out.println("type words to filter. after each word - please press enter. for finish - please type 555");
            String toFilter = myScanner.nextLine();
            while (!(toFilter.equals("555"))) {
                sharedData.addToFiltered(toFilter);
                System.out.println(toFilter + "inserted");
                toFilter = myScanner.nextLine();
            }

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());
                handler.setMyId(counter);
                ConnectionsImpl.getInstance().connect(handler);//Mayan
                handler.getProtocol().start(counter,ConnectionsImpl.getInstance());
                counter ++;
                execute(handler);
            }
        } catch (IOException ex) {
        }
        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
        if (sock != null)
            sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}