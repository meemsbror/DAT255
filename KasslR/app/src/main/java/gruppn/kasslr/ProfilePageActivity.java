package gruppn.kasslr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The acitvity for viewing the profile.
 */
public class ProfilePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_page);

        TextView t = (TextView)findViewById(R.id.usernameText);

        t.setText(getIntent().getExtras().getString("userId"));

    }
}
