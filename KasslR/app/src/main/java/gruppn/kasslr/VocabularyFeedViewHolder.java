package gruppn.kasslr;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import gruppn.kasslr.model.Vocabulary;


public class VocabularyFeedViewHolder extends RecyclerView.ViewHolder{

    protected TextView name, owner, playText;
    private ImageView image1, image2, image3, ownerPicture;
    protected Vocabulary vocabulary;
    private LinearLayout detailed;
    protected CardView cardView;
    protected ImageView fakePlayButton;
    protected ImageView informationButton;
    protected ImageView thumbsDownButton;
    protected ImageView closeButton;
    protected LinearLayout playContainer;
    protected ImageView favouriteButton;


    public VocabularyFeedViewHolder(View v){
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
        fakePlayButton = (ImageView) v.findViewById(R.id.fake_play_button);
        informationButton = (ImageView) v.findViewById(R.id.information_button);
        thumbsDownButton = (ImageView) v.findViewById(R.id.thumbs_down_button);
        closeButton = (ImageView) v.findViewById(R.id.close_button);
        favouriteButton = (ImageView) v.findViewById(R.id.favourite_button);


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

