package gruppn.kasslr.model;


public class User {

    private int ID;
    private String name, profilePicURL;

    public User(String name){
        this.name = name;
    }

    public User(String name, int ID){
        this(name);
        this.ID = ID;
    }

    public User(String name,int ID, String profilePicURL){
        this(name,ID);
        this.profilePicURL = profilePicURL;
    }


    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getID(){
        return this.ID;
    }

    public void setProfilePic(String profilePicURL){
        this.profilePicURL = profilePicURL;
    }

    public String getProfilePic(){
        return this.profilePicURL;
    }
}
