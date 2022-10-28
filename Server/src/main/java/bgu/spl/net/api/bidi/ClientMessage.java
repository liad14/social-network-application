package bgu.spl.net.api.bidi;

import bgu.spl.net.api.ConnectionsImpl;
import bgu.spl.net.api.bidi.Messages.*;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ClientMessage<T> {

    //-----------------------------fields--------------------------------------
    //String error = "";
    int clientId;
    ConnectionsImpl connections;
    //BlockingConnectionHandler myConHandler;
    ClientData myClientData;
    private boolean login;
    public ClientMessage(ConnectionsImpl connections, int clientId){
        this.connections = connections;
        this.clientId = clientId;
        this.login =false;
        // this.myConHandler = myConHandler;

    }
    public ClientData getMyClientData() {
        return myClientData;
    }

    public void readMessage(BidiMessage msg) {

        if (msg.getOpcode()==1)
            register((RegisterMessage) msg);
        if (msg.getOpcode()==2)
            logIn((LoginMessage) msg);
        if (msg.getOpcode()==3)
            logOut((LogoutMessage) msg);
        if (msg.getOpcode()== 4)
            follow((FollowMessage) msg);
        if (msg.getOpcode()== 5)
            post((PostMessage) msg);
        if (msg.getOpcode()==6)
            pm((PMMessage) msg);
        if (msg.getOpcode()== 7)
            logStat((LogstatMessage) msg);
        if (msg.getOpcode()== 8)
            stat((StatMessage) msg);
        if (msg.getOpcode()== 12)
            block((BlockMessage) msg);
    }

    public void register(RegisterMessage msg) {
       
        String userName = msg.getUserName();
        if (SharedData.getInstance().getRegistered().containsKey(userName)){
            error(1);
        }
        else {
            String password = msg.getPassword();
            String birthday = msg.getBirthday();
            myClientData = new ClientData(userName, password, birthday);
            myClientData.setMyId(clientId);
            SharedData.getInstance().addDataToRegistered(myClientData);
            SharedData.getInstance().addClient(userName);
            

            ack(1, "");
        }
    }

    public void logIn(LoginMessage msg) {//
       
        connections.getHandler(clientId).setUserName(msg.getUserName());
        String userName = msg.getUserName();
        ClientData clientToLogin = SharedData.getInstance().getRegistered().get(userName);
        String password = msg.getPassword();
        if ((SharedData.getInstance().getRegistered().containsKey(userName)) && (Objects.equals(clientToLogin.getPassword(), password))
                && (!(clientToLogin.isLoggedIn())) && msg.getCaptcha() == 1) {
            clientToLogin.setLoggedIn(true);
            myClientData = clientToLogin;
            myClientData.setMyId(clientId);
            ack(2, "");
            login = true;
            NotificationMessage m = (NotificationMessage) SharedData.getInstance().getMessage(clientToLogin.getUserName());//TODO: CHANGE!!!
            while ( m !=null) {
                notification(m.getContent(),myClientData.getUserName(),m.getPM_Public(),m.getPostingUser());
                m = (NotificationMessage) SharedData.getInstance().getMessage(clientToLogin.getUserName());
            }
        }
        else {
            error(2);

        }
    } //TODO send ERROE or ACK,

    public void logOut(LogoutMessage msg) {//TODO save id conHandler in ClientData
        if (!login) {
            error(3);

        }
        else {
            myClientData.setLoggedIn(false);
           
            ack(3, "");
            connections.disconnect(clientId);
        }
        //TODO terminate after reciving ack
    }

    public void follow(FollowMessage msg) {
        if(login) {
          
            if (!myClientData.isLoggedIn()) {
                error(4);

            }
            String userName = msg.getFollowName();
            if (msg.getFollow_unfollow() == 0) {
                if (!SharedData.getInstance().getRegistered().containsKey(userName) || myClientData.containsFollowing(userName) || myClientData.getBlocked().contains(userName) )
                    error(4);
                else {
                    myClientData.addToFollowing(userName);
                    SharedData.getInstance().getRegistered().get(userName).getFollowers().add(myClientData.getUserName()); //add me to the user that now i am follower of
                    ack(4, "0 " + userName);

                }
            } else {
                if (!(myClientData.containsFollowing(userName)))
                    error(4);
                else {
                    myClientData.removeFromFollowing(userName);
                    SharedData.getInstance().getRegistered().get(userName).getFollowers().remove(userName); //remove me to the user that now i am follower of
                    ack(4, "1 " + userName);
                }
            }
        }
        else {
            error(4);
        }
    }

    public void post(PostMessage postMessage) {
        if (login) {
           
            String msg = postMessage.getContent();
            String findTag = postMessage.getContent();
            if (!myClientData.isLoggedIn())
                error(5);
            else {
                int nextShtrudel = msg.indexOf('@');
                while (nextShtrudel != -1) {
                    findTag = findTag.substring(nextShtrudel + 1);
                    int nextSpace = findTag.indexOf(" ");
                    if (nextSpace > 0) {
                        String userNameToCheck = findTag.substring(0, nextSpace);
                        if (SharedData.getInstance().getRegistered().containsKey(userNameToCheck)) {
                            if (!(myClientData.getBlocked().contains(userNameToCheck)) && !(myClientData.getFollowers().contains(userNameToCheck)))
                                notification(msg, userNameToCheck, 1, myClientData.getUserName());
                        }
                    }
                    else {
                        String userNameToCheck = findTag;
                        if (SharedData.getInstance().isRegistered(userNameToCheck)) {
                            if (!(myClientData.isBlocked(userNameToCheck)) && !(myClientData.isFollower(userNameToCheck))) {
                                notification(msg, userNameToCheck, 1, myClientData.getUserName());
                                
                            }
                        }
                    }
                    nextShtrudel = findTag.indexOf('@');
                }
                Iterator<String> followersIter = myClientData.getFollowers().iterator();
                while (followersIter.hasNext()) {
                    notification(msg, followersIter.next(), 1, myClientData.getUserName());
                }
                myClientData.increaseNumOfPosts();

                ack(5, "");
            }
        }
        else
            error(5);
    }

    public void pm(PMMessage msg) {
        if (!login)
            error(6);
        else {
            
            if (!myClientData.isLoggedIn())
                error(6);
            else {
                String findFilter;
                String userNameToCheck = msg.getUserName();//check if this user blocked me
                if (myClientData.getBlocked().contains(userNameToCheck))
                    error(6);
                else {
                    String content = msg.getContent();
                    String dateAndTime = msg.getDateAndTime();
                    if (SharedData.getInstance().getRegistered().containsKey(userNameToCheck)) { //&& myClientData.containsFollowing(userNameToCheck)) {
                        String[] wordsToFilter = content.split(" ");
                        for (int wordToFilterInd = 0; wordToFilterInd < wordsToFilter.length; wordToFilterInd++) {
                            if (SharedData.getInstance().shouldFilter(wordsToFilter[wordToFilterInd]))
                                wordsToFilter[wordToFilterInd] = "<filtered>";
                        }
                        String filteredContent = "";
                        for (int i = 0; i < wordsToFilter.length; i++) {
                            filteredContent = filteredContent + wordsToFilter[i] + " ";
                        }
                        content = filteredContent + "Date and Time: " + dateAndTime;
                        if (!(myClientData.getUserName().equals(userNameToCheck))) {
                            notification(content, userNameToCheck, 0, myClientData.getUserName());
                        }
                        ack(6, "");
                        
                    } else
                        error(6);
                }
            }
        }
    }

    public void logStat(LogstatMessage msg) {
        if (!login)
            error(7);
        else {
           
            if (SharedData.getInstance().getRegistered().containsKey(myClientData.getUserName()) && !myClientData.isLoggedIn())
                error(7);
            else {
                Iterator<Map.Entry<Integer, ConnectionHandler>> iter = connections.getActiveConnectionHandlerMap().entrySet().iterator();
                String output = "";
                while (iter.hasNext()) {
                    Map.Entry<Integer, ConnectionHandler> pair = (Map.Entry<Integer, ConnectionHandler>) iter.next();
                    ClientData clientToUse = SharedData.getInstance().getRegistered().get(pair.getValue().getUserName());
                    if (!(myClientData.getBlocked().contains(clientToUse))) {
                        int age = getAge(clientToUse.getBirthday());
                        int numOfPosts = clientToUse.getNumOfPosts();
                        int numOfFollowers = clientToUse.getFollowers().size();
                        int numOfFollowing = clientToUse.getFollowing().size();
                        output = age + " " + numOfPosts + " " + numOfFollowers + " " + numOfFollowing;
                        ack(7, output);
                       
                    }
                }


            }
        }
    }

    public int getAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public void stat(StatMessage statMessage) {
        if(!login)
            error(8);
        else {
          
            if (!SharedData.getInstance().getRegistered().containsKey(myClientData.getUserName()))
                error(8);
            else {
                String listOfUserName = statMessage.getUsersList();
                String output = "";
                int endOfName =listOfUserName.indexOf('|');
                if (endOfName <= 0)
                    checkThisUser(listOfUserName);
                else {
                    boolean lastWord = false;
                    while (!lastWord) {
                        String userToCheck = listOfUserName.substring(0, endOfName);
                        checkThisUser(userToCheck);
                        endOfName++;
                        listOfUserName = listOfUserName.substring(endOfName);
                        endOfName = listOfUserName.indexOf('|');
                        if (endOfName <= 0) {
                            lastWord = true;
                            checkThisUser(listOfUserName);
                        }
                    }
                }
            }
        }
    }
    public void checkThisUser(String userToCheck) {
        String output = "";
        if (!(SharedData.getInstance().getRegistered().containsKey(userToCheck)))
            error(8);
        else {
            if ((myClientData.getBlocked().contains(userToCheck))) {
                error(8);
            }
            else {
                output = "";
                ClientData clientToUse = SharedData.getInstance().getRegistered().get(userToCheck);
                int age = getAge(clientToUse.getBirthday());
                int numOfPosts = clientToUse.getNumOfPosts();
                int numOfFollowers = clientToUse.getFollowers().size();
                int numOfFollowing = clientToUse.getFollowing().size();
                output = age + " " + numOfPosts + " " + numOfFollowers + " " + numOfFollowing;
               
                ack(8, output);
            }
        }

    }
    //send once at all each PM or Post with "NOTIFICATION" at the start string
    //notification is added to the messages map anyway - either if the reciever is logged in or logged out
    // - the client will recieve these messages when he will log in
    // send this notification to the conHandler of the recieved client
    public void notification( String msg, String receiverName, int pm_public , String sender_name) {
       
        int id_receiver = SharedData.getInstance().getIdOfRegistered(receiverName);
        NotificationMessage message = new NotificationMessage(pm_public,sender_name,msg);
        if(!connections.send(id_receiver,message)) {//if not connected save the message (else send it to him)
            String sender = myClientData.getUserName();
            NotificationMessage msgTOAdd = new NotificationMessage(pm_public, myClientData.getUserName(), msg);
            SharedData.getInstance().addMessage(receiverName,msgTOAdd);
        }
    }
    //flush opcode + optional Data
    public void ack(int opCode, String optionalData) {
       
        connections.send(clientId, new AckMessage(opCode,optionalData));
    }

    //flush opcode
    public void error(int opCode) {
        
        connections.send(clientId, new Error_Message(opCode) );
    }

    public void block(BlockMessage msg) {
       
        if (!login)
            error(12);
        else {
           
            if (SharedData.getInstance().getRegistered().containsKey(myClientData.getUserName()) && !myClientData.isLoggedIn())
                error(9);
            else {
                String userToBlock = msg.getUserToBlock();
                if (!(SharedData.getInstance().getRegistered().containsKey(userToBlock)))
                    error(9);
                else {
                    myClientData.getFollowing().remove(userToBlock);
                    myClientData.getFollowers().remove(userToBlock);
                    ClientData clientToBlock = SharedData.getInstance().getRegistered().get(userToBlock);
                    clientToBlock.getFollowers().remove(myClientData.getUserName()); // remove myself from following\followers of blocked
                    clientToBlock.getFollowing().remove(myClientData.getUserName());
                    SharedData.getInstance().removeMessageOfBlocked(userToBlock, myClientData.getUserName());//delete messages that already sent between us
                    SharedData.getInstance().removeMessageOfBlocked(myClientData.getUserName(), userToBlock);
                    myClientData.getBlocked().add(userToBlock);//add to the my blocked list and to the user i blocked
                    SharedData.getInstance().getRegistered().get(userToBlock).getBlocked().add(myClientData.getUserName());
                    ack(12, "");

                }
            }

        }
    }
}
