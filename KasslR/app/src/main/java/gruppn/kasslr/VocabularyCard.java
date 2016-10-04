package gruppn.kasslr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class VocabularyCard extends CardView {

    private Vocabulary vocabulary;

    public VocabularyCard (Context context){
        super(context);
        this.initComponent(context);
    }

    public VocabularyCard (Context context, AttributeSet attrs){
        super(context,attrs);
        this.initComponent(context);
    }

    public VocabularyCard (Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        this.initComponent(context);
    }

    private void initComponent(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.vocabulary_card, null,false);
        this.addView(view);
    }

    public void loadVocabulary(Vocabulary vocabulary){
        this.vocabulary = vocabulary;
        setTitle(this.vocabulary);
        setPictures(this.vocabulary);
    }

    private void setTitle(Vocabulary vocabulary){
        TextView Name = (TextView)findViewById(R.id.textView);
        Name.setText(vocabulary.getTitle());
    }

    private void setPictures(Vocabulary vocabulary){
        ImageView image1 = (ImageView)findViewById(R.id.imageView1);
        ImageView image2 = (ImageView)findViewById(R.id.imageView2);
        ImageView image3 = (ImageView)findViewById(R.id.imageView3);

        List<VocabularyItem> items = vocabulary.getItems();

        if(items.size()>2){
           setImage(image3,items.get(2).getImageUrl());
        }
        if(items.size()>1){
            setImage(image2,items.get(1).getImageUrl());
        }
        if(items.size()>0){
            setImage(image1, items.get(0).getImageUrl());
        }else{
            throw new IllegalStateException("No items in vocabulary");
        }
    }

    private void setImage(ImageView imageView, String imgUrl){
        Bitmap bm = BitmapFactory.decodeFile(imgUrl);
        imageView.setImageBitmap(bm);
    }

    //public getters and setters
    public Vocabulary getVocabulary(){
        return this.vocabulary;
    }
}
