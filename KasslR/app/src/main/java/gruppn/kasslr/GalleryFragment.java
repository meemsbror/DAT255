package gruppn.kasslr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;

public class GalleryFragment extends Fragment {
    private static final FileFilter FILTER = file -> file.getName().toLowerCase().endsWith(".jpg");

    private Kasslr app;

    private GridView gridGallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        gridGallery = (GridView) getActivity().findViewById(R.id.grid_gallery_photos);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Populate gallery
        loadPictures();
    }

    private void loadPictures() {
        File dir = app.getPictureDirectory();
        File[] files;
        if ((files = dir.listFiles(FILTER)) == null || files.length == 0) {
            // There are no images
            return;
        }

        gridGallery.setAdapter(new ImageAdapter(getActivity(), files));
    }

    private class ImageAdapter extends BaseAdapter {
        private static final int COLUMNS = 2;
        private static final int PADDING = 8;

        private Context mContext;
        private File[] mImages;

        public ImageAdapter(Context c, File[] images) {
            mContext = c;
            mImages = images;
        }

        public int getCount() {
            return mImages.length;
        }

        public Object getItem(int position) {
            return mImages[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View view, ViewGroup parent) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(mContext);
                imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            } else {
                imageView = (ImageView) view;
            }

            int width = mContext.getResources().getDisplayMetrics().widthPixels / COLUMNS - PADDING * 2;
            int height = width * 4 / 3 - PADDING * 2;

            Picasso.with(mContext).load(mImages[position]).resize(width, height).into(imageView);
            return imageView;
        }
    }
}
