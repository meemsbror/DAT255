package gruppn.kasslr;

import android.widget.Switch;

/**
 * Created by admin on 2016-10-01.
 */

public class Player {
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {

        return userId;
    }

    public String userId = "Namn";

    public int score = 0;

    public final int COMPLETE_VOCABULARY_SCORE = 2;
    public final int COMPLETE_ACHIEVEMENT_SCORE = 20;
    public final int CREATE_VOCABULARY_SCORE = 25;

    public enum CompletedAction{
        COMPLETE_VOCABULARY, CREATE_VOCABULARY, ACHIEVEMENT
    }

    public Player(){
        //score = DB.getScore
    }
    public Player(String name){
        userId = name;
    }
    public void incScore(CompletedAction completedAction){
        switch(completedAction){
            case CREATE_VOCABULARY: score = score + CREATE_VOCABULARY_SCORE;
                break;
            case COMPLETE_VOCABULARY: score = score + COMPLETE_VOCABULARY_SCORE;
                break;
            case ACHIEVEMENT: score = score + COMPLETE_ACHIEVEMENT_SCORE;
                break;

        }
    }

    public int getScore() {
        return score;
    }
}
