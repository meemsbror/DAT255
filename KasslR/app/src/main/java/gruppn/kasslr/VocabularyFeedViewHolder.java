package gruppn.kasslr;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gruppn.kasslr.game.LaneGame;
import gruppn.kasslr.model.Vocabulary;

import static java.security.AccessController.getContext;


public class VocabularyFeedViewHolder extends RecyclerView.ViewHolder{

    protected TextView name, owner;
    private ImageView image1, image2, image3;
    private ImageButton playButton;
    private Vocabulary vocabulary;


    public VocabularyFeedViewHolder(View v){
        super(v);
        name = (TextView) v.findViewById(R.id.vocabulary_name);
        image1 = (ImageView) v.findViewById(R.id.imageView1);
        image2 = (ImageView) v.findViewById(R.id.imageView2);
        image3 = (ImageView) v.findViewById(R.id.imageView3);

        playButton = (ImageButton) v.findViewById(R.id.imageButton2);

        playButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                Context baseContext = ((ContextWrapper)v.getContext()).getBaseContext();
                if(baseContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) baseContext;
                    mainActivity.changeToGame(v, vocabulary);
                }
            }
        });

    }

    public void setPictures(Bitmap [] bitmaps){
        this.image1.setImageBitmap(bitmaps[0]);

        if(bitmaps[1]!=null){
            this.image2.setImageBitmap(bitmaps[1]);
        }
        if(bitmaps[2]!=null){
            this.image3.setImageBitmap(bitmaps[2]);
        }
    }

    public void setVocabulary(Vocabulary vocabulary){
        this.vocabulary = vocabulary;
        this.name.setText(vocabulary.getTitle());
    }
}

