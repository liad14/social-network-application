package bgu.spl.net.api;

import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {
    private static class ConnectionsImplHolder<T> {
        private static ConnectionsImpl instance = new ConnectionsImpl();
    }
    //------------------------------------------------fields------------------------------------------------------------
    private static ConnectionsImpl instance;

    Map<Integer, ConnectionHandler> activeConnectionHandlerMap;

    private ConnectionsImpl() {
        activeConnectionHandlerMap = new ConcurrentHashMap<>();
    }

    public static ConnectionsImpl getInstance(){
        if (instance == null)
            instance = new ConnectionsImpl();
        return ConnectionsImpl.instance;
    }

    @Override
    public synchronized boolean send(int connectionId, Object msg) {
        synchronized (this) {
            //TODO conHandler send(msg) + why shis method is boolean??? why check (if(...)?
            if (!activeConnectionHandlerMap.containsKey(connectionId))//why msg can be null?
                return false;

            activeConnectionHandlerMap.get(connectionId).send(msg);
            return true;
        }
    }
    public ConnectionHandler getIdHandler(int id) {
        synchronized (this) {
            return activeConnectionHandlerMap.get(id);
        }
    }
    @Override
    public void broadcast(Object msg) {
        Iterator<Integer> conHandIdIter = activeConnectionHandlerMap.keySet().iterator();
        while (conHandIdIter.hasNext()) {
            int connectionId = conHandIdIter.next();
            send(connectionId, msg);
        }
    }

    public synchronized void connect(ConnectionHandler ConnectionHandlerToAdd) {
        synchronized (this) {
            activeConnectionHandlerMap.put(ConnectionHandlerToAdd.getMyId(), ConnectionHandlerToAdd);
        }
    }

    @Override
    public synchronized void disconnect(int connectionId) {
        synchronized (this) {
            activeConnectionHandlerMap.remove(connectionId);
        }
    }

    public Map<Integer, ConnectionHandler> getActiveConnectionHandlerMap() {
        return activeConnectionHandlerMap;
    }
    public ConnectionHandler getHandler(int connectionId){
        synchronized (this) {
            return activeConnectionHandlerMap.get(connectionId);
        }
    }
}
