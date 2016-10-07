package gruppn.kasslr;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class VocabularyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView owner;
    protected TextView title;
    private Activity activity;

    public VocabularyViewHolder(View v, Activity activity) {
        super(v);
        this.activity = activity;
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
