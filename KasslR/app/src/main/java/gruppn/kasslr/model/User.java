package gruppn.kasslr.model;


public class User {

    private String id, name, profilePicURL;

    public User(String name){
        this.name = name;
    }

    public User(String name, String id){
        this(name);
        this.id = id;
    }

    public User(String name, String id, String profilePicURL){
        this(name, id);
        this.profilePicURL = profilePicURL;
    }


    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public void setProfilePic(String profilePicURL){
        this.profilePicURL = profilePicURL;
    }

    public String getProfilePic(){
        return this.profilePicURL;
    }
}
