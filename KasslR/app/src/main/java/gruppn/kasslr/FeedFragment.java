package gruppn.kasslr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;

public class FeedFragment extends Fragment {

    private Kasslr app;
    private VocabularyFeedAdapter va;
    private Shelf feedShelf = new Shelf();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { super.onActivityCreated(savedInstanceState); //add point
        feedShelf.clear();
        this.app = (Kasslr) getActivity().getApplication();
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_feed);
        recyclerView.setItemAnimator(null);

        va = new VocabularyFeedAdapter(getActivity(), feedShelf.getVocabularies());
        va.setAdapterSwitch(R.id.recycler_view_feed);
        recyclerView.setAdapter(va);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if (app.getOnlyOnce()) {
            app.loadFeedItems(getContext(), va);
        }
        feedShelf.addVocabularies(app.getBigShelf().getVocabularies());
        va.setVocabularyList(feedShelf.getVocabularies());

    }
}
