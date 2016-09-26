package gruppn.kasslr;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class AddWordFragment extends Fragment {
    public static final String EXTRA_IMAGE = "image";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_word, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btnAdd = (Button) getActivity().findViewById(R.id.btnAddWord);
        btnAdd.setOnClickListener(v -> {
            // TODO Add word + image to vocabulary
            // Perhaps use intent result?
        });

        if (getArguments() != null) {
            Uri image = (Uri) getArguments().get(EXTRA_IMAGE);
            if (image != null) {
                ImageView imgWord = (ImageView) getActivity().findViewById(R.id.imgWord);
                imgWord.setImageURI(image);
            }
        }
    }
}
