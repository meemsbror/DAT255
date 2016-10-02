package gruppn.kasslr;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            System.out.println(Player.getScore());
            TextView usernameText = (TextView) getActivity().findViewById(R.id.usernameText);
            usernameText.setText(getArguments().getString(EXTRA_USER_ID));
            TextView points = (TextView) getActivity().findViewById(R.id.points);
            points.setText("" + Player.getScore());
        }
    }
}
