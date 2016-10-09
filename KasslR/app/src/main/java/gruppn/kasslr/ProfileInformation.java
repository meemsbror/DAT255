package gruppn.kasslr;

import android.net.Uri;
import android.widget.Switch;

/**
 * Created by admin on 2016-10-01.
 */

public class ProfileInformation {

    public int score = 0;
    public String name = "Namn";


    public Uri profilePicture;

    private final int COMPLETE_VOCABULARY_SCORE = 2;
    private final int COMPLETE_ACHIEVEMENT_SCORE = 20;
    private final int CREATE_VOCABULARY_SCORE = 25;



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
