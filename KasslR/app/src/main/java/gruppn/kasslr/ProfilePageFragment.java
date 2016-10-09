package gruppn.kasslr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The activity for viewing the profile.
 */
public class ProfilePageFragment extends Fragment{
    private Kasslr app;

    private ImageButton settings;


    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    public final int PICK_IMAGE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //add point
        app = (Kasslr) getActivity().getApplication();

        final EditText name = (EditText)getView().findViewById(R.id.user_profile_name);

        name.setText(app.getUserName());
        name.setSelectAllOnFocus(true);


        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView.setLayoutManager(recyclerLayoutManager);


        VocabularyAdapter va = new VocabularyAdapter(getActivity(), app.getShelf().getVocabularies());
        recyclerView.setAdapter(va);

        TextView points = (TextView) getView().findViewById(R.id.score);
        points.setText("" + app.getScore());

        //Change profile background
        final ImageView imageView = (ImageView) getView().findViewById(R.id.profile_layout_background);
        Picasso.with(getContext())
                .load(R.drawable.tempbackground)
                .fit()
                .into(imageView);

        //Change profile picture
        changeProfilePicture(app.getProfilePicture());

        //SettingsButton
        settings = (ImageButton) getView().findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), settings);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.settings_popup, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Byt namn")){
                            if(name.requestFocus()){
                                name.selectAll();
                                showKeyboard();
                            }
                        }else if(item.getTitle().equals("Byt bild")){
                            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getIntent.setType("image/*");

                            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickIntent.setType("image/*");

                            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                            startActivityForResult(chooserIntent, PICK_IMAGE);
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method



        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                app.setUserName(name.getText().toString());
            }
        });
    }

    public void changeProfilePicture(Uri uri){
        app.setProfilePicture(uri);
        final ImageView imageView = (ImageView) getView().findViewById(R.id.profile_picture);
        imageView.setImageURI(uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE){
            if(data != null)
            {
                changeProfilePicture(data.getData());
            }
        }
    }

    private void showKeyboard(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

}
