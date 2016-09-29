package gruppn.kasslr;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;

public class MainActivity extends AppCompatActivity {

    private Kasslr app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        app = (Kasslr) getApplication();
        setContentView(R.layout.activity_main);
        requestCameraPermission();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(tabId -> {
            if (tabId == R.id.tab_feed) {
                // Show the feed
                showFeed();
            } else if (tabId == R.id.tab_search) {
                //Show the search page
            } else if (tabId == R.id.tab_camera) {
                // Show the camera
                showCamera();
            } else if (tabId == R.id.tab_favorite) {
                //Show the saved vocabularies
            } else if (tabId == R.id.tab_profile) {
                showProfile();
            } else {
                //When can this happen?
            }
        });
        for (int i = 0; i < bottomBar.getTabCount(); i++) {
            bottomBar.getTabAtPosition(i).setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    public void openAddVocabulary(View view) {
        showFragment(new AddVocabularyFragment());
    }

    public void showFeed() {
        showFragment(new FeedFragment());
    }

    public void showProfile() {
        Fragment fragment = new ProfilePageFragment();
        Bundle extras = new Bundle();
        extras.putString(ProfilePageFragment.EXTRA_USER_ID, app.getUserId());
        fragment.setArguments(extras);
        showFragment(fragment);
    }

    public void showCamera() {
        startActivity(new Intent(this, CameraActivity.class));
    }


    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    public void changeToGame(View view){
        Intent myIntent = new Intent(this,Game.class);
        startActivity(myIntent);
    }

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private final int MY_PERMISSIONS_REQUEST_STORAGE = 2;

    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // TODO
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // TODO
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void createVocabulary(View view) {
        System.out.println("creating vocabulary");
        EditText vocName = (EditText)findViewById(R.id.newVocName);
        Vocabulary voc = new Vocabulary(app.getUserId(), vocName.getText().toString().trim());
        app.getShelf().addVocabulary(voc);

        System.out.println(app.getShelf().toString());
        Toast.makeText(this, "Added vocabulary", Toast.LENGTH_SHORT).show();
        showFeed();
    }
}
