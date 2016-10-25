package gruppn.kasslr.model;

import android.net.Uri;

public class ProfileInformation {

    private int score = 0;
    //private User user;
    private String name = "Namn";
    private String userId = "dassid";


    private Uri profilePicture;

    private static final int COMPLETE_VOCABULARY_SCORE = 2;
    private static final int COMPLETE_ACHIEVEMENT_SCORE = 20;
    private static final int CREATE_VOCABULARY_SCORE = 25;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public enum CompletedAction{
        COMPLETE_VOCABULARY, CREATE_VOCABULARY, ACHIEVEMENT
    }

    public ProfileInformation(){
        //Todo, load name, profile picture and score from DB
        //score = DB.getScore
        //name = DB.getName
    }

    public void incScore(CompletedAction completedAction) {
        switch (completedAction) {
            case CREATE_VOCABULARY:
                score += CREATE_VOCABULARY_SCORE;
                break;
            case COMPLETE_VOCABULARY:
                score += COMPLETE_VOCABULARY_SCORE;
                break;
            case ACHIEVEMENT:
                score += COMPLETE_ACHIEVEMENT_SCORE;
                break;
        }
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPicture() {
        return profilePicture;
    }

    public void setPicture(Uri picture) {
        this.profilePicture = picture;
    }
}
