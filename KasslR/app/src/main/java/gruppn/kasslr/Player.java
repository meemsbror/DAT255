package gruppn.kasslr;

import android.widget.Switch;

/**
 * Created by admin on 2016-10-01.
 */

public class Player {
    public static final String EXTRA_USER_ID = "userId";

    public static int score = 0;

    public static final int COMPLETE_VOCABULARY_SCORE = 2;
    public static final int COMPLETE_ACHIEVEMENT_SCORE = 20;
    public static final int CREATE_VOCABULARY_SCORE = 25;

    public enum CompletedAction{
        COMPLETE_VOCABULARY, CREATE_VOCABULARY, ACHIEVEMENT
    }

    public Player(){
        //score = DB.getScore
    }
    public static void incScore(CompletedAction completedAction){
        switch(completedAction){
            case CREATE_VOCABULARY: score = score + CREATE_VOCABULARY_SCORE;
                break;
            case COMPLETE_VOCABULARY: score = score + COMPLETE_VOCABULARY_SCORE;
                break;
            case ACHIEVEMENT: score = score + COMPLETE_ACHIEVEMENT_SCORE;
                break;

        }
    }

    public static int getScore() {
        return score;
    }
}
