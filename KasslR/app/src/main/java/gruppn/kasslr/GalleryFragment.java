package gruppn.kasslr;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;

public class GalleryFragment extends Fragment {
    private static final FileFilter FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".jpg");
        }
    };

    private Kasslr app;

    private GridView gridGallery;
    private int imageCount = 0;

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
        File dir = app.getImageDirectory();
        File[] files;
        if ((files = dir.listFiles(FILTER)) == null || files.length == imageCount) {
            // There are no (new) images
            return;
        }

        imageCount = files.length;
        final ImageAdapter adapter = new ImageAdapter(getActivity(), files);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                app.setSharedBitmap(((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap());

                File file = adapter.getItem(i);

                Intent intent = new Intent(getActivity(), AddWordActivity.class);
                intent.putExtra(AddWordActivity.EXTRA_IMAGE, Uri.fromFile(file));
                String transition = getString(R.string.transition_add_word);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(), view, transition);

                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });
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

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public File getItem(int position) {
            return mImages[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(mContext);
                imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.transition_add_word));
                }
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
