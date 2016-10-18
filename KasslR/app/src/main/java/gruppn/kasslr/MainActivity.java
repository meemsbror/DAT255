package gruppn.kasslr;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import gruppn.kasslr.game.LaneGame;
import gruppn.kasslr.model.ProfileInformation;
import gruppn.kasslr.model.Vocabulary;


public class MainActivity extends AppCompatActivity {

    private Kasslr app;

    private BottomBar bottomBar;
    private int selectedTab = -1;
    public enum Direction {
        DOWN(R.anim.enter_from_top, R.anim.exit_to_bottom),
        LEFT(R.anim.enter_from_right, R.anim.exit_to_left),
        UP(R.anim.enter_from_bottom, R.anim.exit_to_top),
        RIGHT(R.anim.enter_from_left, R.anim.exit_to_right);

        private @AnimRes int enter;
        private @AnimRes int exit;

        Direction(int enter, int exit) {
            this.enter = enter;
            this.exit = exit;
        }

        public Direction getOpposite() {
            return values()[(ordinal() + 2) % 4];
        }

        public int getEnter() {
            return enter;
        }


        public int getExit() {
            return exit;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        app = (Kasslr) getApplication();
        app.initUserData(this);
        setContentView(R.layout.activity_main);
        requestCameraPermission();
        initiateBottomBar();

        // we need this for image loading apparently
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(this.getIntent().getAction().equals("gallery")){
            showFragment(new GalleryFragment());
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
        slideToFragment(new AddVocabularyWizardFragmentOne(), Direction.DOWN, true);
    }

    public void showFeed() {
        showFragment(new FeedFragment());
    }

    public void showSearch() {
        showFragment(new SearchFragment());
    }

    public void showFavorite() {
        showFragment(new FavoriteFragment());
    }

    public void showProfile() {
        showFragment(new ProfilePageFragment());
    }

    public void showCamera() {
        startActivity(new Intent(this, CameraActivity.class));
    }


    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    public void slideToFragment(Fragment fragment, Direction direction, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Direction opposite = direction.getOpposite();
        transaction.setCustomAnimations(direction.getEnter(), direction.getExit(), opposite.getEnter(), opposite.getExit());
        transaction.replace(R.id.main_frame, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void changeToGame(View view, Vocabulary vocabulary){
        System.out.println("Starting game for vocabulary " + vocabulary.getTitle());
        app.setActiveVocabulary(vocabulary);
        Intent intent = new Intent(this,LaneGame.class);
        startActivity(intent);
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

    private void initiateBottomBar() {

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                // Clear entire backstack
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                int previous = selectedTab;
                if (tabId != R.id.tab_camera) {
                    selectedTab = tabId;
                }
                Direction directions = Direction.LEFT;
                if (previous > tabId) {
                    directions = Direction.RIGHT;
                }

                if (tabId == R.id.tab_feed) {
                    // Show the feed
                    if (previous == -1) {
                        showFeed();
                    } else {
                        slideToFragment(new FeedFragment(), directions, false);
                    }
                } else if (tabId == R.id.tab_search) {
                    //Show the search page
                    slideToFragment(new SearchFragment(), directions, false);
                } else if (tabId == R.id.tab_camera) {
                    // Show the camera
                    showCamera();
                } else if (tabId == R.id.tab_favorite) {
                    slideToFragment(new FavoriteFragment(), directions, false);
                } else if (tabId == R.id.tab_profile) {
                    slideToFragment(new ProfilePageFragment(), directions, false);
                } else {
                    //When can this happen?
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                // Clear entire backstack
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        for (int i = 0; i < bottomBar.getTabCount(); i++) {
            bottomBar.getTabAtPosition(i).setGravity(Gravity.CENTER_VERTICAL);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
