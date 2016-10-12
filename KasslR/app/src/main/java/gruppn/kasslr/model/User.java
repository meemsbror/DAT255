package gruppn.kasslr.model;


public class User {

    private final int ID;
    private String name, profilePicURL;

    

    public User(String name,int ID, String profilePicURL){
        this.name = name;
        this.ID = ID;
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
