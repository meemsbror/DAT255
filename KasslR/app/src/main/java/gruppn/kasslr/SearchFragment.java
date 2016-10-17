package gruppn.kasslr;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

import static gruppn.kasslr.R.layout.fragment_search;


public class SearchFragment extends Fragment {

    private Kasslr app;
    private RecyclerView recyclerViewFeed_search;
    private SearchView searchView_search;
    private VocabularyFeedAdapter va;
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


        va = new VocabularyFeedAdapter((MainActivity) getActivity(), searchShelf.getVocabularies());
        va.setAdapterSwitch(R.id.recyclerViewFeed_search);
        recyclerViewFeed_search.setAdapter(va);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFeed_search.setLayoutManager(llm);

        searchView_search.setQueryHint("Sök efter glosböcker");
        searchView_search.setIconified(false);

        searchView_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public boolean onQueryTextSubmit(String s) {
                updateSearch();
                if (s.isEmpty()) {
                    searchShelf.clear();
                }
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
                if (s.isEmpty()) {
                    searchShelf.clear();
                }
                /*if (recyclerViewFeed_search.getLayoutManager().) {
                    searchView_search.onActionViewCollapsed();
                    System.out.println("wtf");
                }*/
                return false;
            }
        });

    }

    public void updateSearch(){
        searchShelf.clear();
        recyclerViewFeed_search.removeAllViews();
        String string = searchView_search.getQuery().toString().toLowerCase();

        if (!string.isEmpty()) {

            for (Vocabulary vocabulary : app.getFeedVocabularies()) {

                if (!searchShelf.getVocabularies().contains(vocabulary)) {

                    if (vocabulary.getTitle().toLowerCase().equals(string) || vocabulary.getTitle().toLowerCase().contains(string)) {
                        searchShelf.addVocabulary(vocabulary);

                    } else {
                        for (VocabularyItem vocabularyItem : vocabulary.getItems()) {
                            if (!searchShelf.getVocabularies().contains(vocabulary) && (vocabularyItem.getName().toLowerCase().equals(string) || vocabularyItem.getName().toLowerCase().contains(string))) {
                                searchShelf.addVocabulary(vocabulary);

                            }
                        }
                    }
                }
            }
        }
        va.notifyDataSetChanged();
        va.setVocabularyList(searchShelf.getVocabularies());

    }
}
