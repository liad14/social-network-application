package bgu.spl.net.api.bidi;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientData {

    //----------------------------------fields-----------------------------------------------------
    private String userName;
    private String password;
    private LocalDate birthday;
    private boolean loggedIn;
    private int MyId;
    private Queue<String> following;
    private Queue<String> followers;
    private int numOfPosts;
    private Queue<String> blocked;


    //-----------------------------------Constructor-----------------------------------------------


    public ClientData(String userName, String password, String birthday) {
        this.userName = userName;
        this.password = password;
        int dayOfBirthday = Integer.parseInt(birthday.substring(0, 2));
        int monthOfBirthday = Integer.parseInt(birthday.substring(3, 5));
        int yearOfBirthday = Integer.parseInt(birthday.substring(6));
        LocalDate birthDate = LocalDate.of(yearOfBirthday, monthOfBirthday, dayOfBirthday);
        this.birthday = birthDate;
        following = new ConcurrentLinkedQueue<>();
        followers = new ConcurrentLinkedQueue<>();
        blocked = new ConcurrentLinkedQueue<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public int getMyId() {
        return MyId;
    }

    public void setMyId(int myId) {
        MyId = myId;
    }
    public void addToFollowing(String userName) {
        following.add(userName);
    }
    public boolean containsFollowing(String userName) {
        return following.contains(userName);
    }
    public void removeFromFollowing(String userName) {
        following.remove(userName);
    }

    public Collection<String> getFollowers() {        return followers;
    }
    public Collection<String>  getFollowing() {        return following;
    }
    synchronized public void increaseNumOfPosts(){
        numOfPosts++;
    }
    public int getNumOfPosts(){
        return numOfPosts;
    }
    public  Collection<String> getBlocked() {return blocked;}
    public boolean isBlocked(String userToCheck){
        return blocked.contains(userToCheck);
    }
    public boolean isFollower(String userToCheck){
        return followers.contains(userToCheck);
    }
}
