package gruppn.kasslr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

import static gruppn.kasslr.R.layout.fragment_search;


public class SearchFragment extends Fragment {

    private Kasslr app;
    private RecyclerView recyclerViewFeed_search;
    private SearchView searchView_search;
    private VocabularyFeedAdapter va;
    private List<Vocabulary> vocabularyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(fragment_search, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState); //add point

        this.app = (Kasslr) getActivity().getApplication();

        recyclerViewFeed_search = (RecyclerView) getView().findViewById(R.id.recyclerViewFeed_search);
        searchView_search = (SearchView) getView().findViewById(R.id.searchView_search);
        va = new VocabularyFeedAdapter(getActivity(), vocabularyList);
        va.setSearchAdapter(true);
        recyclerViewFeed_search.setAdapter(va);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFeed_search.setLayoutManager(llm);

        /*
        GridLayoutManager GLM = new GridLayoutManager(getActivity(), 3);
        
        recyclerView_search.setLayoutManager(GLM);
        */
        searchView_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                updateSearch();
                //System.out.println("submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateSearch();
                //System.out.println("change");
                return false;
            }
        });


    }


    public void updateSearch(){
        vocabularyList.clear();
        recyclerViewFeed_search.removeAllViews();
        String string = searchView_search.getQuery().toString();

        if (!string.isEmpty()) {

            for (Vocabulary vocabulary : app.getOnlineShelf()) {

                if (!vocabularyList.contains(vocabulary)) {

                    if (vocabulary.getTitle().equals(string) || vocabulary.getTitle().contains(string)) {
                        vocabularyList.add(vocabulary);

                    } else {
                        for (VocabularyItem vocabularyItem : vocabulary.getItems()) {
                            if (!vocabularyList.contains(vocabulary) && (vocabularyItem.getName().equals(string) || vocabularyItem.getName().contains(string))) {
                                vocabularyList.add(vocabulary);

                            }
                        }
                    }
                }
            }
        }
        //va = new VocabularyAdapter(getActivity(), vocabularyList);
        va.setVocabularyList(vocabularyList);

    }
}
