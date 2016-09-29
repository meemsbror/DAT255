package gruppn.kasslr;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
public class ProfilePageFragment extends Fragment {
    public static final String EXTRA_USER_ID = "userId";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            //TextView usernameText = (TextView) getActivity().findViewById(R.id.user_profile_name);
            //usernameText.setText(getArguments().getString(EXTRA_USER_ID));
        }

        //Change profile background
        ImageView imageView = (ImageView) getView().findViewById(R.id.profile_layout_background);
        Picasso.with(getContext())
                .load(R.drawable.tempbackground)
                .fit()
                .into(imageView);

        //Change profile pic
        /*
        CircleImageView imageView = (CircleImageView) getView().findViewById(R.id.profile_image);
        Picasso.with(getContext())
                .load(R.drawable.hehecat)
                .fit()
                .into(imageView);
        */


    }
}
