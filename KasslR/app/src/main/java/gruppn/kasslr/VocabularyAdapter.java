package gruppn.kasslr;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import gruppn.kasslr.model.Vocabulary;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private List<Vocabulary> vocabularyList;
    private Activity activity;
    private int mExpandedPosition = -1;

    public VocabularyAdapter(Activity activity, List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
        this.activity = activity;

    }

    public void setVocabularyList(List<Vocabulary> vocabularyList){
        this.vocabularyList = vocabularyList;
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    @Override
    public void onBindViewHolder(final VocabularyViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.detailed.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.fakePlayButton.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.cardView.setActivated(isExpanded);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(holder.recyclerView);
                notifyDataSetChanged();
            }
        });

        Vocabulary v = vocabularyList.get(position);
        //holder.owner.setText(v.getOwner());
        //holder.title.setText(v.getTitle());

    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_feed_card, parent, false);
        return new VocabularyViewHolder(itemView);
    }

    public class VocabularyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected LinearLayout detailed;
        protected CardView cardView;
        protected RecyclerView recyclerView;
        protected TextView owner;
        protected TextView title;
        protected ImageView fakePlayButton;

        public VocabularyViewHolder(View v) {
            super(v);

            detailed = (LinearLayout) v.findViewById(R.id.expand_area);
            cardView = (CardView) v.findViewById(R.id.card_view_feed);
            recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
            fakePlayButton = (ImageView) v.findViewById(R.id.fake_play_button);
            //owner = (TextView) v.findViewById(R.id.txtOwner);
            //title = (TextView) v.findViewById(R.id.txtTitle);

        }
        /*
        public void vocabularyTransition(View view){

            Intent intent = new Intent(activity, VocabularyTransitionActivity.class);

            String transitionName = activity.getString(R.string.transition_vocabulary);

            CardView cardView = (CardView) view.findViewById(R.id.card_view_feed);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                            cardView,   // The view which starts the transition
                            transitionName    // The transitionName of the view we’re transitioning to
                    );
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        }
        */

        @Override
        public void onClick(View v) {

        }
    }

}
