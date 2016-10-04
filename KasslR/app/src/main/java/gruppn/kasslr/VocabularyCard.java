package gruppn.kasslr;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import gruppn.kasslr.model.Vocabulary;

public class VocabularyCard extends CardView {

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
}
