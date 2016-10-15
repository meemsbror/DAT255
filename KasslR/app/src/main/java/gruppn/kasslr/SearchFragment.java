package gruppn.kasslr;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
    private Shelf searchShelf = new Shelf();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(fragment_search, container, false);

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState); //add point

        this.app = (Kasslr) getActivity().getApplication();

        recyclerViewFeed_search = (RecyclerView) getView().findViewById(R.id.recyclerViewFeed_search);
        searchView_search = (SearchView) getView().findViewById(R.id.searchView_search);
        searchView_search.setIconifiedByDefault(false);
        searchView_search.requestFocus();
        searchView_search.onActionViewExpanded();


        va = new VocabularyFeedAdapter(getActivity(), searchShelf.getVocabularies());
        va.setAdapterSwitch(R.id.recyclerViewFeed_search);
        recyclerViewFeed_search.setAdapter(va);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFeed_search.setLayoutManager(llm);

<<<<<<< HEAD
        /*
        GridLayoutManager GLM = new GridLayoutManager(getActivity(), 3);
        
        recyclerView_search.setLayoutManager(GLM);
        */
        searchView_search.setQueryHint("Sök efter glosböcker");
        searchView_search.setIconified(false);
=======

>>>>>>> changes made to search and favourit now works
        searchView_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public boolean onQueryTextSubmit(String s) {
                updateSearch();
                /*if (recyclerViewFeed_search.callOnClick()) {
                    searchView_search.onActionViewCollapsed();
                    System.out.println("wtf");
                }*/
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public boolean onQueryTextChange(String s) {
                updateSearch();
                /*if (recyclerViewFeed_search.getLayoutManager().) {
                    searchView_search.onActionViewCollapsed();
                    System.out.println("wtf");
                }*/
                return false;
            }
        });

    }


    public void updateSearch(){
        vocabularyList.clear();
        recyclerViewFeed_search.removeAllViews();
        String string = searchView_search.getQuery().toString();

        if (!string.isEmpty()) {

            for (Vocabulary vocabulary : app.getBigShelf().getVocabularies()) {

                //if (!vocabularyList.contains(vocabulary)) {
                if (!searchShelf.getVocabularies().contains(vocabulary)) {

                    if (vocabulary.getTitle().equals(string) || vocabulary.getTitle().contains(string)) {
                        //vocabularyList.add(vocabulary);
                        searchShelf.addVocabulary(vocabulary);

                    } else {
                        for (VocabularyItem vocabularyItem : vocabulary.getItems()) {
                            //if (!vocabularyList.contains(vocabulary) && (vocabularyItem.getName().equals(string) || vocabularyItem.getName().contains(string))) {
                            if (!searchShelf.getVocabularies().contains(vocabulary) && (vocabularyItem.getName().equals(string) || vocabularyItem.getName().contains(string))) {
                                //vocabularyList.add(vocabulary);
                                searchShelf.addVocabulary(vocabulary);

                            }
                        }
                    }
                }
            }
        }
<<<<<<< HEAD
        //va = new VocabularyAdapter(getActivity(), vocabularyList);
        va.setVocabularyList(vocabularyList);
        va.notifyDataSetChanged();
=======
        //searchShelf.addVocabularies(vocabularyList);
        va.setVocabularyList(searchShelf.getVocabularies());
>>>>>>> changes made to search and favourit now works

    }
}
