package gruppn.kasslr;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class VocabularyFeedAdapter extends RecyclerView.Adapter<VocabularyFeedViewHolder>{

    private List<Vocabulary> vocabularies;
    private Activity activity;
    private Kasslr app;
    private Context mContext;

    public VocabularyFeedAdapter(Activity activity, List<Vocabulary> vocabularies){
        this.vocabularies = vocabularies;
        this.activity = activity;
        app = (Kasslr) activity.getApplication();
    }

    @Override
    public int getItemCount(){
        return vocabularies.size();
    }

    @Override
    public VocabularyFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_feed_card,parent,false);

        this.mContext = parent.getContext();

        return new VocabularyFeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VocabularyFeedViewHolder holder, int position){
        updateView(holder, position);
    }

    private void updateView(VocabularyFeedViewHolder holder, int position){

        Vocabulary v = vocabularies.get(position);
        List<VocabularyItem> items = v.getItems();

        setPictures(holder, items);
        holder.setVocabulary(v);


    }

    private void setPictures(VocabularyFeedViewHolder holder, final List<VocabularyItem> items){

        int maxImages = 3;
        if(items.size() < 3)
            maxImages = items.size();

        for(int i = 0; i < maxImages; i++) {
            final ImageView img = holder.getImageView(i+1);
            final File imageFile = app.getImageFile(items.get(i));
            img.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(mContext).load(imageFile).centerCrop().fit().into(img);
                }
            });

        }

    }

    private Bitmap createBitmap(VocabularyItem item){
        Bitmap bm = BitmapFactory.decodeFile(app.getImageFile(item).getAbsolutePath());
        return bm;
    }
}
