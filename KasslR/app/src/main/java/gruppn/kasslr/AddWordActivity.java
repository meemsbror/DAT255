package gruppn.kasslr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AddWordActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_IMAGE_DESCRIPTION = "image_description";
    public static final String EXTRA_FINISH_ON_BACK = "finish_back";

    private Kasslr app;

    private boolean finishOnBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_add_word);

        app = (Kasslr) getApplication();

        ImageView imgWord = (ImageView) findViewById(R.id.imgWord);

        Uri image = (Uri) getIntent().getExtras().get(EXTRA_IMAGE);

        Bitmap bitmap = app.getSharedBitmap();
        if (bitmap == null && image != null) {
            bitmap = BitmapFactory.decodeFile(image.getPath());
        }

        if (bitmap != null) {
            imgWord.setImageBitmap(Bitmap.createScaledBitmap(bitmap, getImageWidth(), getImageHeight(), false));
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

    public void returnWord(View view) {
        TextView txtWord = (TextView) findViewById(R.id.txtWord);
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_DESCRIPTION, txtWord.getText().toString());
        setResult(txtWord.getText().length() > 0 ? RESULT_OK : RESULT_CANCELED, data);
        finish();
    }

    private int getImageWidth() {
        return getResources().getDisplayMetrics().widthPixels * 3 / 4;
}

    private int getImageHeight() {
        return getResources().getDisplayMetrics().widthPixels;
    }
}
