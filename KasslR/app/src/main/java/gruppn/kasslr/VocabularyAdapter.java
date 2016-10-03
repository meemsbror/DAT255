package gruppn.kasslr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gruppn.kasslr.model.Vocabulary;

/**
 * Created by Daniel on 2016-09-30.
 */

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private List<Vocabulary> vocabularyList;
    private Activity activity;

    public VocabularyAdapter(Activity activity, List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
        this.activity = activity;

    }


    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    @Override
    public void onBindViewHolder(VocabularyViewHolder holder, int position) {
        Vocabulary v = vocabularyList.get(position);
        holder.owner.setText(v.getOwner());
        holder.title.setText(v.getTitle());

    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vocabulary_card, parent, false);
        return new VocabularyViewHolder(itemView);
    }

    public class VocabularyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView owner;
        protected TextView title;

        public VocabularyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            owner = (TextView) v.findViewById(R.id.txtOwner);
            title = (TextView) v.findViewById(R.id.txtTitle);

        }

        public void vocabularyTransition(View view){

            Intent intent = new Intent(activity, VocabularyTransitionActivity.class);

            String transitionName = activity.getString(R.string.transition_vocabulary);

            CardView cardView = (CardView) view.findViewById(R.id.card_view);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                            cardView,   // The view which starts the transition
                            transitionName    // The transitionName of the view we’re transitioning to
                    );
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        }

        @Override
        public void onClick(View v) {
            vocabularyTransition(v);
        }


    }

}
