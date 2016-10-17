package gruppn.kasslr;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gruppn.kasslr.model.Vocabulary;


public class VocabularyViewHolder extends RecyclerView.ViewHolder{

    protected TextView name, owner, playText;
    protected ImageView image1, image2, image3, playButton, ownerPicture;
    private Vocabulary vocabulary;
    protected LinearLayout detailed;
    protected CardView cardView;
    protected RecyclerView recyclerView;
    protected ImageView fakePlayButton;
    protected ImageView informationButton;
    protected ImageView favouriteButton;
    protected ImageView thumbsDownButton;


    public VocabularyViewHolder(List<Vocabulary> vocabularyList, View v){
        super(v);
        name = (TextView) v.findViewById(R.id.vocabulary_name);
        owner = (TextView) v.findViewById(R.id.owner_name);
        playText = (TextView) v.findViewById(R.id.play_text);
        image1 = (ImageView) v.findViewById(R.id.vocabulary_image1);
        image2 = (ImageView) v.findViewById(R.id.vocabulary_image2);
        image3 = (ImageView) v.findViewById(R.id.vocabulary_image3);
        ownerPicture = (ImageView) v.findViewById(R.id.owner_picture);

        detailed = (LinearLayout) v.findViewById(R.id.expand_area);
        cardView = (CardView) v.findViewById(R.id.card_view_feed);
        favouriteButton = (ImageView)  v.findViewById(R.id.favourite_button);
        fakePlayButton = (ImageView) v.findViewById(R.id.fake_play_button);
        informationButton = (ImageView) v.findViewById(R.id.information_button);
        thumbsDownButton = (ImageView) v.findViewById(R.id.thumbs_down_button);

        for (Vocabulary vocabulary : vocabularyList) {
            if (vocabulary.isFavourite()) {
                favouriteButton.setImageResource(R.drawable.ic_favorite_red_24dp);
            } else {
                favouriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
        }

        favouriteButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                Context baseContext = ((ContextWrapper)v.getContext()).getBaseContext();
                if (vocabulary.isFavourite()){
                    vocabulary.setFavourite(false);
                    favouriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    vocabulary.setFavourite(true);
                    favouriteButton.setImageResource(R.drawable.ic_favorite_red_24dp);
                }
            }
        });

    }

    public ImageView getImageView(int i){
        switch (i){
            case 1:
                return this.image1;
            case 2:
                return this.image2;
            case 3:
                return this.image3;
            case 4:
                return this.ownerPicture;
            default:
                return null;
        }
    }

    public void setVocabulary(Vocabulary vocabulary){
        this.vocabulary = vocabulary;
        this.name.setText(vocabulary.getTitle());

    }
}

