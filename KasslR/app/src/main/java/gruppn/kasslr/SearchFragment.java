package gruppn.kasslr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;

import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

import static gruppn.kasslr.R.layout.fragment_search;


public class SearchFragment extends Fragment {

    ScrollView scrollView;

    Shelf shelf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(fragment_search, container, false);
    }

    public void setShelf(Shelf shelf){

    }

    public void onClick(View view){
        updateSearch(shelf);
    }

    public void updateSearch(Shelf shelf){

        String string = getView().findViewById(R.id.searchView).toString();

        scrollView.removeAllViews();

        for (Vocabulary vocabulary : shelf.getVocabularies()) {

            if (vocabulary.getTitle().equals(string) || vocabulary.getTitle().contains(string)){

                //scrollView.addView(vocabulary);

            } else {
                for(VocabularyItem  vocabularyItem : vocabulary.getItems()){
                    if (vocabularyItem.getName().equals(string) || vocabularyItem.getName().contains(string)) {

                        //något här

                    }
                }
            }
        }
    }
}
