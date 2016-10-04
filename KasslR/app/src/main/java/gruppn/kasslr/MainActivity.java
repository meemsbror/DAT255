package gruppn.kasslr;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import gruppn.kasslr.game.LaneGame;
import gruppn.kasslr.model.Vocabulary;

public class MainActivity extends AppCompatActivity {

    private Kasslr app;

    private BottomBar bottomBar;
    private int selectedTab = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        app = (Kasslr) getApplication();
        setContentView(R.layout.activity_main);
        requestCameraPermission();

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                int previous = selectedTab;
                if (tabId != R.id.tab_camera) {
                    selectedTab = tabId;
                }

                boolean slideToRight = previous > tabId;
                if (tabId == R.id.tab_feed) {
                    // Show the feed
                    if (previous == -1) {
                        showFeed();
                    } else {
                        slideToFragment(new FeedFragment(), slideToRight);
                    }
                } else if (tabId == R.id.tab_search) {
                    //Show the search page
                    slideToFragment(new SearchFragment(), slideToRight);
                } else if (tabId == R.id.tab_camera) {
                    // Show the camera
                    showCamera();
                } else if (tabId == R.id.tab_favorite) {
                    // TODO Show the saved vocabularies
                    // This is temporary until we decide how to reach the gallery
                    slideToFragment(new GalleryFragment(), slideToRight);
                } else if (tabId == R.id.tab_profile) {
                    slideToFragment(new ProfilePageFragment(), slideToRight);
                } else {
                    //When can this happen?
                }
            }
        });
        for (int i = 0; i < bottomBar.getTabCount(); i++) {
            bottomBar.getTabAtPosition(i).setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Return to previous tab when closing the camera
        if (bottomBar.getCurrentTabId() == R.id.tab_camera) {
            bottomBar.selectTabWithId(selectedTab);
        }
    }

    public void openAddVocabulary(View view) {
        showFragment(new AddVocabularyFragment());
    }

    public void showFeed() {
        showFragment(new FeedFragment());
    }

    public void showSearch() {
        showFragment(new SearchFragment());
    }

    public void showProfile() {
        showFragment(new ProfilePageFragment());
    }

    public void showCamera() {
        startActivity(new Intent(this, CameraActivity.class));
    }


    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    public void slideToFragment(Fragment fragment, boolean slideRight) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (slideRight) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }

    public void changeToGame(View view){
        Intent myIntent = new Intent(this,LaneGame.class);
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
        app.increaseScore(Player.CompletedAction.CREATE_VOCABULARY);
        EditText vocName = (EditText)findViewById(R.id.newVocName);
        Vocabulary voc = new Vocabulary(app.getUserId(), vocName.getText().toString().trim());
        app.getShelf().addVocabulary(voc);

        System.out.println(app.getShelf().toString());
        Toast.makeText(this, "Added vocabulary", Toast.LENGTH_SHORT).show();
        showFeed();
    }
}
