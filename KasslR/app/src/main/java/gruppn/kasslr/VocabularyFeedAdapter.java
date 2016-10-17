package gruppn.kasslr;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;
import gruppn.kasslr.task.DownloadVocabularyTask;
import gruppn.kasslr.task.RemoveVocabularyTask;

public class VocabularyFeedAdapter extends RecyclerView.Adapter<VocabularyFeedViewHolder> {

    private List<Vocabulary> vocabularies = new ArrayList<>();
    private MainActivity activity;
    private Kasslr app;
    private Context mContext;
    private int adapterSwitch = R.id.recycler_view_feed;

    public VocabularyFeedAdapter(MainActivity activity, List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
        this.activity = activity;
        app = (Kasslr) activity.getApplication();
    }

    public void addVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    @Override
    public VocabularyFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_feed_card, parent, false);

        this.mContext = parent.getContext();


        VocabularyFeedViewHolder vfvh = new VocabularyFeedViewHolder(itemView);

        return vfvh;
    }

    public void setAdapterSwitch(int adapterSwitch){
        this.adapterSwitch = adapterSwitch;
    }

    @Override
    public void onBindViewHolder(final VocabularyFeedViewHolder holder, int position) {
        updateView(holder, position);

        holder.playContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.changeToGame(v, holder.vocabulary);
            }
        });
        holder.informationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                holder.informationButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
                app.setActiveVocabulary(holder.vocabulary);

                Intent intent = new Intent(activity, VocabularyInformationActivity.class);
                activity.startActivity(intent);
            }
        });


        holder.favouriteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.favouriteButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));

                Vocabulary fromShelf = app.getShelf().getVocabularyByUid(holder.vocabulary.getUniversalId());
                boolean favorite = fromShelf != null;

                if (favorite) {
                    // Remove from favorites
                    holder.favouriteButton.setImageResource(R.drawable.ic_favorite_grey_24dp);
                    app.getShelf().removeVocabulary(fromShelf);
                    new RemoveVocabularyTask(app).execute(fromShelf);

                    if (holder.vocabulary.getId() != 0) {
                        // Also remove card
                        int index = vocabularies.indexOf(holder.vocabulary);

                        vocabularies.remove(index);
                        notifyItemRemoved(index);
                    }
                } else {
                    // Save to favorites
                    new DownloadVocabularyTask(app).execute(vocabularies.get(holder.getAdapterPosition()));
                    holder.favouriteButton.setImageResource(R.drawable.ic_favorite_red_24dp);
                }
            }
        });

        holder.thumbsDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.thumbsDownButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
                //Todo downvote stuff
            }
        });


        holder.closeButton.setVisibility(holder.vocabulary.getId() == 0 ? View.GONE : View.VISIBLE);
        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.closeButton.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
                app.getShelf().getVocabularies().remove(holder.vocabulary);
                vocabularies.remove(holder.getAdapterPosition());
                new RemoveVocabularyTask(app).execute(holder.vocabulary);
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

    }

    private void updateView(VocabularyFeedViewHolder holder, int position) {
        Vocabulary v = vocabularies.get(position);
        List<VocabularyItem> items = v.getItems();
        setPictures(holder, items);
        setOwner(holder, v);
        holder.setVocabulary(v);

        Vocabulary fromShelf = app.getShelf().getVocabularyByUid(holder.vocabulary.getUniversalId());
        boolean favorite = fromShelf != null;

        if (favorite) {
            holder.favouriteButton.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            holder.favouriteButton.setImageResource(R.drawable.ic_favorite_grey_24dp);
        }

    }

    private void setPictures(VocabularyFeedViewHolder holder, final List<VocabularyItem> items) {

        int maxImages = 3;
        if (items.size() < 3)
            maxImages = items.size();

        for (int i = 0; i < maxImages; i++) {
            final ImageView img = holder.getImageView(i + 1);
            if (!items.get(i).getImageName().startsWith("http")) {
                final File imageFile = app.getImageFile(items.get(i));
                Picasso.with(mContext).load(imageFile).centerCrop().fit().into(img);
            } else {
                final String url = items.get(i).getImageName();
                Picasso.with(mContext).load(url).centerCrop().fit().into(img);
            }
        }
    }

    private void setOwner(VocabularyFeedViewHolder holder, Vocabulary v) {

        final ImageView img = holder.getImageView(4);
        final String image = v.getOwner().getProfilePic();
        img.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(mContext).load(image).centerCrop().fit().into(img);

            }
        });

        holder.owner.setText("Av "+v.getOwner().getName());
    }

    public void setVocabularyList(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }
}
