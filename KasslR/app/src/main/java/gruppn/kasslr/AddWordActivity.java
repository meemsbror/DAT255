package gruppn.kasslr;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class AddWordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        Button btnAdd = (Button) findViewById(R.id.btnAddWord);
        btnAdd.setOnClickListener(v -> {
            // TODO Add word + image to vocabulary
            // Perhaps use intent result?
        });

        // TODO does CameraActivity return a Bitmap image or a Uri to an image?
        Uri image = (Uri) getIntent().getExtras().get("image");
        if (image != null) {
            ImageView imgWord = (ImageView) findViewById(R.id.imgWord);
            imgWord.setImageURI(image);
        }
    }
}
