package gruppn.kasslr;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class AddVocabularyFragment extends Fragment {
    private static final String DEBUG_TAG = "AddVocabularyFragment";

    private Kasslr app;

    private EditText txtTitle;
    private GalleryFragment galleryFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_vocabulary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        txtTitle = (EditText) getActivity().findViewById(R.id.newVocName);
        Button btnSave = (Button) getActivity().findViewById(R.id.submitVoc);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createVocabulary();
            }
        });

        galleryFragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putBoolean(GalleryFragment.EXTRA_SELECTABLE, true);
        galleryFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_vocabulary_items, galleryFragment).commit();
    }

    public void createVocabulary() {
        String title = txtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), R.string.vocabulary_missing_title, Toast.LENGTH_LONG).show();
            return;
        }

        List<VocabularyItem> items = new ArrayList<>(galleryFragment.getSelectedItems());
        if (items.size() < 3) {
            Toast.makeText(getContext(), R.string.vocabulary_missing_items, Toast.LENGTH_LONG).show();
            return;
        }

        Vocabulary vocabulary = new Vocabulary(app.getUserId(), title);
        vocabulary.setItems(items);

        app.getShelf().addVocabulary(vocabulary);
        new SaveVocabulariesTask().execute(vocabulary);

        getActivity().getSupportFragmentManager().popBackStack();
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
