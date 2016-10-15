package gruppn.kasslr;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyViewHolder>{

    private List<Vocabulary> vocabularies;
    private Activity activity;
    private Kasslr app;
    private Context mContext;
    private int mExpandedPosition = -1;

    public VocabularyAdapter(Activity activity, List<Vocabulary> vocabularies){
        this.vocabularies = vocabularies;
        this.activity = activity;
        app = (Kasslr) activity.getApplication();
    }

    public void addVocabularies(List<Vocabulary> vocabularies){
        this.vocabularies = vocabularies;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return vocabularies.size();
    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_feed_card,parent,false);

        this.mContext = parent.getContext();

        return new VocabularyViewHolder(vocabularies, itemView);
    }

    @Override
    public void onBindViewHolder(final VocabularyViewHolder holder, final int position){
        final boolean isExpanded = position == mExpandedPosition;
        holder.detailed.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.fakePlayButton.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.playText.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.cardView.setActivated(isExpanded);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recycler_view_favorite));
                }
                notifyItemChanged(position);

                if (mExpandedPosition != -1 && mExpandedPosition != position) {
                    notifyItemChanged(mExpandedPosition);
                }
                mExpandedPosition = isExpanded ? -1 : position;
            }
        });


        holder.favouriteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO remove vocabulary
                //app.getShelf().addVocabulary(vocabularies.get(position));
            }
        });

        updateView(holder, position);
    }

    private void updateView(VocabularyViewHolder holder, int position){
        Vocabulary v = vocabularies.get(position);
        List<VocabularyItem> items = v.getItems();
        setPictures(holder, items);
        setOwner(holder,v);
        holder.setVocabulary(v);
    }

    private void setPictures(VocabularyViewHolder holder, final List<VocabularyItem> items){

        int maxImages = 3;
        if(items.size() < 3)
            maxImages = items.size();

        for(int i = 0; i < maxImages; i++) {
            final ImageView img = holder.getImageView(i+1);
            if(!items.get(i).getImageName().startsWith("http")) {
                final File imageFile = app.getImageFile(items.get(i));
                Picasso.with(mContext).load(imageFile).centerCrop().fit().into(img);
            }else{
                final String url = items.get(i).getImageName();
                Picasso.with(mContext).load(url).centerCrop().fit().into(img);
            }
        }
    }

    private void setOwner(VocabularyViewHolder holder, Vocabulary v){

        final ImageView img = holder.getImageView(4);
        final String image = v.getOwner().getProfilePic();
        img.post(new Runnable() {
            @Override
            public void run(){
                Picasso.with(mContext).load(image).centerCrop().fit().into(img);

            }
        });

        holder.owner.setText(v.getOwner().getName());
    }

    public void setVocabularyList(List<Vocabulary> vocabulaies) {
        this.vocabularies = vocabulaies;
    }
}

