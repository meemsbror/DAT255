package gruppn.kasslr;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;
import gruppn.kasslr.task.DownloadVocabularyTask;

public class VocabularyFeedAdapter extends RecyclerView.Adapter<VocabularyFeedViewHolder>{

    private List<Vocabulary> vocabularies;
    private Activity activity;
    private Kasslr app;
    private Context mContext;
    private int mExpandedPosition = -1;

    public VocabularyFeedAdapter(Activity activity, List<Vocabulary> vocabularies){
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
    public VocabularyFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_feed_card,parent,false);

        this.mContext = parent.getContext();

        return new VocabularyFeedViewHolder(itemView);
    }


    private boolean searchAdapter = false;

    public void setSearchAdapter(boolean a){
        searchAdapter = a;
    }

    @Override
    public void onBindViewHolder(final VocabularyFeedViewHolder holder, final int position){
        final boolean isExpanded = position == mExpandedPosition;
        holder.detailed.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.fakePlayButton.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.playText.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.cardView.setActivated(isExpanded);
        if (searchAdapter) {

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    /*
                    mExpandedPosition = isExpanded ? -1 : position;
                    TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recyclerViewFeed_search));
                    notifyDataSetChanged();
                    */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recyclerViewFeed_search));
                    }
                    notifyItemChanged(position);
                    if (mExpandedPosition != -1 && mExpandedPosition != position) {
                        notifyItemChanged(mExpandedPosition);
                    }
                    mExpandedPosition = isExpanded ? -1 : position;
                }
            });

        } else {

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    /*
                    mExpandedPosition = isExpanded ? -1 : position;
                    TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recycler_view_feed));
                    notifyDataSetChanged();
                    */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recycler_view_feed));
                    }
                    notifyItemChanged(position);
                    if (mExpandedPosition != -1 && mExpandedPosition != position) {
                        notifyItemChanged(mExpandedPosition);
                    }
                    mExpandedPosition = isExpanded ? -1 : position;
                }
            });

        }
       /*
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition((ViewGroup)activity.findViewById(R.id.recycler_view_feed));
                }
                notifyItemChanged(position);
                if (mExpandedPosition != -1 && mExpandedPosition != position) {
                    notifyItemChanged(mExpandedPosition);
                }
                mExpandedPosition = isExpanded ? -1 : position;
            }
        });
        */

        holder.favoriteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.favoriteButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
                new DownloadVocabularyTask(app).execute(vocabularies.get(position));
            }
        });

        holder.thumbsDownButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                holder.thumbsDownButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
                //Todo downvote stuff
            }
        });

        updateView(holder, position);
    }

    private void updateView(VocabularyFeedViewHolder holder, int position){
        Vocabulary v = vocabularies.get(position);
        List<VocabularyItem> items = v.getItems();
        setPictures(holder, items);
        setOwner(holder,v);
        holder.setVocabulary(v);
    }

    private void setPictures(VocabularyFeedViewHolder holder, final List<VocabularyItem> items){

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

    private void setOwner(VocabularyFeedViewHolder holder, Vocabulary v){

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

    /* TODO
    private class SaveVocabularyTask extends AsyncTask<Vocabulary, Void, Void> {
        @Override
        protected Void doInBackground(Vocabulary... vocabularies) {
            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(activity.getApplicationContext());

                for (Vocabulary vocabulary : vocabularies) {
                    if (vocabulary.getTitle().isEmpty()) {
                        db.remove(vocabulary);
                   } else {
                        db.save(vocabulary);
                    }
                }
                Log.d(DEBUG_TAG, "Successfully saved " + vocabularies.length + " vocabulary");
            } catch (SQLiteException e) {
                Log.e(DEBUG_TAG, "Failed to save vocabulary)", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            return null;
        }
    }
    */

    public void setVocabularyList(List<Vocabulary> vocabulaies) {
        this.vocabularies = vocabulaies;
    }
}
