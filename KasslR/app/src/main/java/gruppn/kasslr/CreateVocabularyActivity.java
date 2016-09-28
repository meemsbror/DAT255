package gruppn.kasslr;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import gruppn.kasslr.FeedFragment;
import gruppn.kasslr.R;
import gruppn.kasslr.Vocabulary;

public class CreateVocabularyActivity extends AppCompatActivity{
    public Vocabulary mainVocabulary;

    String activityFeed = "feed";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
    }

    public void answer1Click(View view){
        Intent myIntent = new Intent(this, FeedFragment.class);
        startActivity(myIntent);
    }

    public void createVocabulary(View view){

        //Här tar man kort osv. Vet inte om den ska finnas här men det gör den atm
        //Todo fixit
        mainVocabulary = new Vocabulary("mata in namn");
        mainVocabulary.addWordPair("hund", "hund.jpg");
        mainVocabulary.addWordPair("katt", "katt.jpg");
        mainVocabulary.addWordPair("fästing", "fästing.jpg");
        try {
            mainVocabulary.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView tv = (TextView)findViewById(R.id.textView5);
        tv.setText(mainVocabulary.getWords().get(0));
        //Todo replace with image
        TextView tv2 = (TextView)findViewById(R.id.textView6);
        tv2.setText(mainVocabulary.getPictures().get(0));
        System.out.println(mainVocabulary.getWords().get(1) + " " + mainVocabulary.getPictures().get(1) + " " + mainVocabulary.getWords().get(2) + " " + mainVocabulary.getPictures()
                .get(2));
    }

    private void changeActivity(String activity){

        Intent myIntent = new Intent(this, FeedFragment.class);

        switch(activity){
            case "feed" :
        }
        System.out.println("yolO");
        startActivity(myIntent);
    }
}