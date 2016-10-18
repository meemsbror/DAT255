package gruppn.kasslr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.Vocabulary;

public class FavoriteFragment extends Fragment {

    private Kasslr app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { super.onActivityCreated(savedInstanceState);

        this.app = (Kasslr) getActivity().getApplication();

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_favorite);

        List<Vocabulary> favorites = new ArrayList<>();
        for (Vocabulary vocabulary : app.getShelf().getVocabularies()) {
            // TODO this is incredibly confusing and wrong but there isn't enough time to do this shit properly. hail spaghetti
            if (vocabulary.getUniversalId() != 0) {
                favorites.add(vocabulary);
            }
        }

        VocabularyFeedAdapter va = new VocabularyFeedAdapter((MainActivity) getActivity(), favorites);
        recyclerView.setAdapter(va);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

}
