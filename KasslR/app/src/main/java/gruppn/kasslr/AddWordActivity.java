package gruppn.kasslr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddWordActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_IMAGE_DESCRIPTION = "image_description";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_add_word);

        Button btnAdd = (Button) findViewById(R.id.btnAddWord);
        btnAdd.setOnClickListener(v -> returnWord());


        Uri image = (Uri) getIntent().getExtras().get(EXTRA_IMAGE);
        if (image != null) {
            ImageView imgWord = (ImageView) findViewById(R.id.imgWord);
            imgWord.setImageURI(image);
        }
    }

    private void returnWord() {
        TextView txtWord = (TextView) findViewById(R.id.txtWord);
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_DESCRIPTION, txtWord.getText());
        setResult(txtWord.getText().length() > 0 ? RESULT_OK : RESULT_CANCELED, data);
        finish();
    }
}
