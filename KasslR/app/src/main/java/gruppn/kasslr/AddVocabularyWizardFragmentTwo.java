package gruppn.kasslr;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

/**
 * Created by marcu on 2016-10-12.
 */

public class AddVocabularyWizardFragmentTwo extends Fragment {
    private static final String DEBUG_TAG = "AddVocabularyFragment";

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
        System.out.print(pictureIndex);

        txtTitle = (EditText) getActivity().findViewById(R.id.newVocName);
        switchUpload = (SwitchCompat) getActivity().findViewById(R.id.switch_upload);

        Button btnTemp = (Button) getActivity().findViewById(R.id.createButton);
        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createVocabulary();
            }
        });

        btnTemp = (Button)getActivity().findViewById(R.id.backButton);
        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPreviousPage();
            }
        });
    }

    private void openPreviousPage() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void createVocabulary() {
        String title = txtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), R.string.vocabulary_missing_title, Toast.LENGTH_LONG).show();
            return;
        }

        List<VocabularyItem> items = new ArrayList<>();
        for (Integer i : pictureIndex) {
            items.add(app.getShelf().getItems().get(i));
        }

        Vocabulary vocabulary = new Vocabulary(app.getUserId(), title);
        vocabulary.setItems(items);

        app.getShelf().addVocabulary(vocabulary);

        if (switchUpload.isChecked()) {
            app.uploadVocabulary(getContext(), vocabulary);
        }

        new SaveVocabulariesTask().execute(vocabulary);

        // Clear entire backstack
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private class SaveVocabulariesTask extends AsyncTask<Vocabulary, Void, Void> {
        @Override
        protected Void doInBackground(Vocabulary... vocabularies) {
            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(getActivity().getApplicationContext());

                for (Vocabulary vocabulary : vocabularies) {
                    db.save(vocabulary);
                }

                Log.d(DEBUG_TAG, "Successfully saved " + vocabularies.length + " vocabulary/ies");
            } catch (SQLiteException e) {
                Log.e(DEBUG_TAG, "Failed to save vocabulary/ies", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            return null;
        }
    }
}
