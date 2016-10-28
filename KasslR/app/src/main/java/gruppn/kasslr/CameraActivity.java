package gruppn.kasslr;

import com.google.android.cameraview.CameraView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import gruppn.kasslr.model.VocabularyItem;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private final int NAME_IMAGE = 420;

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };
/*
    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int mCurrentFlash;
*/
    private Kasslr app;

    private CameraView mCameraView;

    private Handler mBackgroundHandler;

    private ImageView galleryImage;
    private ImageView tempAnim;
    ImageButton takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        app = (Kasslr) getApplication();
        mCameraView = (CameraView) findViewById(R.id.camera);
        mCameraView.addCallback(mCallback);
        mCameraView.setFlash(CameraView.FLASH_OFF); // please

        this.takePicture = (ImageButton) findViewById(R.id.snap);
        this.galleryImage = (ImageView) findViewById(R.id.shelf_button);
        this.tempAnim = (ImageView) findViewById(R.id.temp_animation);


        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.takePicture();
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        */
    }
    private Bitmap getLastImage(){
        Bitmap bitmap = app.getSharedBitmap();

        List<VocabularyItem> items = app.getShelf().getItems();
        VocabularyItem item = items.get(items.size()-1);

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(app.getImageFile(item).getAbsolutePath());

        }
        return bitmap;
    }

    private VocabularyItem getLastItem() {
        List<VocabularyItem> items = app.getShelf().getItems();

        if (!items.isEmpty()) {

            for (int i = items.size() -1; i >= 0; i--) {
                if (items.get(i).isMine() && !items.get(i).isRemoved()) {
                    return items.get(i);
                }
            }
        }

        return null;
    }

    private void setGalleryImage(){
        VocabularyItem item = getLastItem();
        if(item != null) {
            Bitmap bitmap = app.getSharedBitmap();

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(app.getImageFile(item).getAbsolutePath());
            }
            this.galleryImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 58, 58, false));
        }
    }

    private void startAnimation(Bitmap bitmap) {
        this.tempAnim.setImageBitmap(bitmap);
        Animation anim= new TranslateAnimation(0, 1080, 0, 1800);
        anim.setStartOffset(250);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);

        tempAnim.startAnimation(anim);

        setGalleryImage();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
       setGalleryImage();
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION ) {

            if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
            }

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please give camera permission",
                        Toast.LENGTH_SHORT).show();
            }
            // No need to start camera here; it is handled by onResume
        }
    }

    public void onShelfClick(View view){
        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
        intent.setAction("gallery");
        intent.putExtra("EXTRA_MODE", "mode");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        switch (item.getItemId()) {
            case R.id.switch_flash:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                break;
            case R.id.switch_camera:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                break;
        }
        */
        return false;
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(final CameraView cameraView, final byte[] data) {
            takePicture.startAnimation(AnimationUtils.loadAnimation(app, R.anim.button_feedback));
            Log.d(TAG, "onPictureTaken " + data.length);

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    final File file = getNewPictureFile();

                    OutputStream os = null;
                    try {
                        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdir()) {
                            throw new IOException("Failed to create picture directory");
                        }
                        os = new FileOutputStream(file);
                        os.write(data);
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                        return;
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CameraActivity.this, EditItemActivity.class);
                            intent.putExtra(EditItemActivity.EXTRA_IMAGE_URI, Uri.fromFile(file));
                            intent.putExtra(EditItemActivity.EXTRA_EXIT_TRANSITION, false);

                            String transition = getString(R.string.transition_edit_item_image);
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(CameraActivity.this, cameraView, transition);

                            ActivityCompat.startActivityForResult(CameraActivity.this, intent, NAME_IMAGE, options.toBundle());
                        }
                    });
                }
            });
        }
    };

    private File getNewPictureFile() {
        File dir = app.getImageDirectory();
        return new File(dir, UUID.randomUUID().toString() + ".jpg");
    }

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode,
                                                             @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                            if (permissions == null) {
                                throw new IllegalArgumentException();
                            }
                            ActivityCompat.requestPermissions(getActivity(), permissions,
                                    args.getInt(ARG_REQUEST_CODE));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(), args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).create();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NAME_IMAGE){
            startAnimation(getLastImage());
        }
    }
}