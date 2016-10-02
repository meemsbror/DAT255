package gruppn.kasslr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gruppn.kasslr.model.Vocabulary;

/**
 * Created by Daniel on 2016-09-30.
 */

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private List<Vocabulary> vocabularyList;

    public VocabularyAdapter(List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
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

    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {

        protected TextView owner;
        protected TextView title;


        public VocabularyViewHolder(View v) {
            super(v);
            owner = (TextView) v.findViewById(R.id.txtOwner);
            title = (TextView) v.findViewById(R.id.txtTitle);

        }

    }

}
