package gruppn.kasslr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by marcu on 2016-10-12.
 */

public class AddVocabularyWizardFragmentTwo extends Fragment {

    private EditText txtTitle;
    private SwitchCompat switchUpload;

    private Kasslr app;

    ArrayList<Integer> pictureIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_vocabulary_wizard_page_two, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();
        pictureIndex = getArguments().getIntegerArrayList("pictureIndex");
    }
}
