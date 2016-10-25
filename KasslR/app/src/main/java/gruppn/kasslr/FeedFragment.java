package gruppn.kasslr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import gruppn.kasslr.model.Vocabulary;

public class FeedFragment extends Fragment {

    private Kasslr app;
    private VocabularyFeedAdapter va;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState); //add point
        this.app = (Kasslr) getActivity().getApplication();
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_feed);

        va = new VocabularyFeedAdapter((MainActivity) getActivity(), new ArrayList<Vocabulary>());
        recyclerView.setAdapter(va);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if (app.shouldUpdateFeed()) {
            app.loadFeedItems(getContext(), va);
        } else {
            va.setVocabularyList(app.getFeedVocabularies());
        }
    }
}
