package gruppn.kasslr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import gruppn.kasslr.model.Vocabulary;

public class VocabularyInformationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_information);

        Kasslr app = (Kasslr) getApplication();

        Vocabulary vocabulary = app.getActiveVocabulary();

        TextView title = (TextView) findViewById(R.id.text_vocabulary_title);
        title.setText(vocabulary.getTitle());

        GalleryFragment galleryFragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable(GalleryFragment.EXTRA_MODE, GalleryFragment.Mode.VIEW);
        galleryFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_vocabulary_items, galleryFragment).commit();
    }
}
