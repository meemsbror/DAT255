package gruppn.kasslr;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The activity for viewing the profile.
 */
public class ProfilePageFragment extends Fragment{
    private Kasslr app;

    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //add point
        app = (Kasslr) getActivity().getApplication();

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView.setLayoutManager(recyclerLayoutManager);


        VocabularyAdapter va = new VocabularyAdapter(getActivity(), app.getShelf().getVocabularies());
        recyclerView.setAdapter(va);

        TextView points = (TextView) getView().findViewById(R.id.score);
        points.setText("" + app.getCurrentScore());

        //Change profile background
        ImageView imageView = (ImageView) getView().findViewById(R.id.profile_layout_background);
        Picasso.with(getContext())
                .load(R.drawable.tempbackground)
                .fit()
                .into(imageView);

        //Score
        /*
        TextView points = (TextView)getView().findViewById(R.id.points);
        points.setText("" + Player.getScore());
        */
    }

}
