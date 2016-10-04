package gruppn.kasslr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Game extends AppCompatActivity{

    String activityFeed = "feed";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    public void answer1Click(View view){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    private void changeActivity(String activity){

     Intent myIntent = new Intent(this, MainActivity.class);

        switch(activity){
            case "feed" :
        }
        System.out.println("yolO");
        startActivity(myIntent);
    }
}
