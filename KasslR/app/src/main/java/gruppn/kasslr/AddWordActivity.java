package gruppn.kasslr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddWordActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_IMAGE_DESCRIPTION = "image_description";
    public static final String EXTRA_FINISH_ON_BACK = "finish_back";

    private boolean finishOnBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_add_word);

        Button btnAdd = (Button) findViewById(R.id.btnAddWord);
        btnAdd.setOnClickListener(v -> returnWord());


        Uri image = (Uri) getIntent().getExtras().get(EXTRA_IMAGE);
        if (image != null) {
            ImageView imgWord = (ImageView) findViewById(R.id.imgWord);
            // TODO Load with picasso? (would break shared element transition)
            Bitmap myImg = BitmapFactory.decodeFile(image.getPath());
            imgWord.setImageBitmap(Bitmap.createScaledBitmap(myImg, 480, 480, false));
        }

        finishOnBack = getIntent().getExtras().getBoolean(EXTRA_FINISH_ON_BACK, false);
    }

    @Override
    public void onBackPressed() {
        if (finishOnBack) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void returnWord() {
        TextView txtWord = (TextView) findViewById(R.id.txtWord);
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_DESCRIPTION, txtWord.getText().toString());
        setResult(txtWord.getText().length() > 0 ? RESULT_OK : RESULT_CANCELED, data);
        finish();
    }
}
