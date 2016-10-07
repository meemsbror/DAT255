package gruppn.kasslr;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * The activity for viewing the profile.
 */
public class ProfilePageFragment extends Fragment{
    private Kasslr app;

    private ImageButton settings;

    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //add point
        app = (Kasslr) getActivity().getApplication();

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(recyclerLayoutManager);

        VocabularyAdapter va = new VocabularyAdapter(getActivity(), app.getShelf().getVocabularies());
        recyclerView.setAdapter(va);

        //TextView points = (TextView) getView().findViewById(R.id.score);
        //points.setText("" + app.getCurrentScore());

        //Change profile background
        /*
        ImageView imageView = (ImageView) getView().findViewById(R.id.profile_layout_background);
        Picasso.with(getContext())
                .load(R.mipmap.ic_launcher)
                .centerCrop()
                .fit()
                .into(imageView);

<<<<<<< 2b3dd62f294e0a3dd78d2561031f4154557038f5

        //Score

        TextView points = (TextView)getView().findViewById(R.id.points);
        points.setText("" + Player.getScore());
        */


        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "hehe", Snackbar.LENGTH_SHORT)
                        .setAction("Vad ska stå här då", null).show();
            }
        });

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

                        }else if(item.getTitle().equals("Byt bild")){
                            //Todo, do stuff
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method
    }

}
