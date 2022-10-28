package bgu.spl.net.api.bidi;


import bgu.spl.net.api.bidi.Messages.BidiMessage;
import bgu.spl.net.api.bidi.Messages.NotificationMessage;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedData {
    private static class SharedDataHolder {
        private static SharedData instance = new SharedData();
    }
    //------------------------------------------------fields------------------------------------------------------------
    private static SharedData instance;
    private ConcurrentHashMap<String, ClientData> registered;
    private Collection<String> connected;
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<NotificationMessage>> messages;
    private Collection<String> filtered;
    private LocalDate date;



    //-----------------------------------------------Constructor-----------------------------------------------------------------
    public SharedData() {
        registered = new ConcurrentHashMap<>();
        connected = new ConcurrentLinkedQueue<>();
        messages = new ConcurrentHashMap<>();
        filtered = new ConcurrentLinkedQueue<>();
        date = LocalDate.now();
    }

    public static SharedData getInstance() {
        return SharedDataHolder.instance;
    }

    public void addDataToRegistered(ClientData newClient) {
        registered.put(newClient.getUserName(), newClient);
    }

    public void addMessage(String userName, NotificationMessage msg){
        messages.get(userName).add(msg);
    }

    public void addClient(String userName) {
       ConcurrentLinkedQueue myMessages = new ConcurrentLinkedQueue();
        messages.put(userName, myMessages);
    }
    public int getIdOfRegistered(String name){
        return registered.get(name).getMyId();
    }
    public ClientData getClient(String name){
        return registered.get(name);
    }
    public Collection<String> getFiltered() {
        return filtered;
    }
    public BidiMessage getMessage(String client){
        return  messages.get(client).poll();
    }
    public void removeMessageOfBlocked(String blocked, String client){
        ConcurrentLinkedQueue<NotificationMessage> clientD = messages.get(client);
        for (NotificationMessage m :  clientD) {
            if (m.getPostingUser().equals(blocked))
                messages.get(client).remove(m);
        }
    }
    public Map<String,ClientData> getRegistered() {
        return  registered;
    }
    public  boolean isRegistered(String name){
        return registered.containsKey(name);
    }
    public Map getMessagesPerClient() {
        return messages;
    }

    public void addToFiltered(String wordToFilter) {
        filtered.add(wordToFilter);
    }

    public boolean shouldFilter(String word) {
        return filtered.contains(word);
    }
}