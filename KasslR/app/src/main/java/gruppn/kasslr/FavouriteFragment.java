package gruppn.kasslr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.R;
import gruppn.kasslr.VocabularyFeedAdapter;
import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;

public class FavouriteFragment extends Fragment {

    private Kasslr app;
    private RecyclerView recyclerViewFeed_favourite;
    private VocabularyFeedAdapter va;
    private Shelf favouriteShelf = new Shelf();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { super.onActivityCreated(savedInstanceState); //add point
        this.app = (gruppn.kasslr.Kasslr) getActivity().getApplication();

        recyclerViewFeed_favourite = (RecyclerView) getView().findViewById(R.id.recyclerViewFeed_favourite);
        recyclerViewFeed_favourite.setItemAnimator(null);

        va = new VocabularyFeedAdapter(getActivity(), favouriteShelf.getVocabularies());

        va.setAdapterSwitch(R.id.recyclerViewFeed_favourite);

        recyclerViewFeed_favourite.setAdapter(va);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFeed_favourite.setLayoutManager(llm);

        for (Vocabulary vocabulary : app.getBigShelf().getVocabularies()) {
            if (vocabulary.isFavourite()) {
                favouriteShelf.addVocabulary(vocabulary);
            }
        }
        va.setVocabularyList(favouriteShelf.getVocabularies());
    }

}
