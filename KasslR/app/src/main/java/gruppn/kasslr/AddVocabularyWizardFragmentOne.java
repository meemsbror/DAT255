package gruppn.kasslr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.VocabularyItem;


public class AddVocabularyWizardFragmentOne extends Fragment {

    private Kasslr app;
    private GalleryFragment galleryFragment;
    List<VocabularyItem> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_vocabulary_wizard_page_one, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        Button btnSave = (Button) getActivity().findViewById(R.id.nextButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStepWizard();
            }
        });

        galleryFragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putBoolean(GalleryFragment.EXTRA_SELECT_MODE, true);
        galleryFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_vocabulary_items, galleryFragment).commit();
    }

    private void nextStepWizard() {
        items = new ArrayList<>(galleryFragment.getSelectedItems());
        int size = items.size();
        if (size < 3) {
            Toast.makeText(getContext(), R.string.vocabulary_missing_items, Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Integer> picureIndex = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            picureIndex.add(app.getShelf().getItems().indexOf(items.get(i)));
        }

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("pictureIndex", picureIndex);

        AddVocabularyWizardFragmentTwo fragobj = new AddVocabularyWizardFragmentTwo();
        fragobj.setArguments(bundle);

        MainActivity Mac = (MainActivity)getActivity();
        Mac.slideToFragment(fragobj, MainActivity.Direction.LEFT, true);
    }


}
