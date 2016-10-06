package gruppn.kasslr;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import gruppn.kasslr.model.VocabularyItem;

public class GalleryFragment extends Fragment {
    private static final String DEBUG_TAG = "GalleryFragment";
    private static final FileFilter FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".jpg");
        }
    };

    private Kasslr app;

    private GridView gridGallery;
    private int itemCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        gridGallery = (GridView) getActivity().findViewById(R.id.grid_gallery_photos);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VocabularyItem item = (VocabularyItem) adapterView.getItemAtPosition(i);
                app.setSharedBitmap(((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap());

                Intent intent = new Intent(getActivity(), EditItemActivity.class);
                intent.putExtra(EditItemActivity.EXTRA_ITEM_INDEX, app.getShelf().getItems().indexOf(item));
                String transition = getString(R.string.transition_add_word);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(), view, transition);

                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Populate gallery
        loadItems();
    }

    private void loadItems() {
        if (itemCount != app.getShelf().getItems().size()) {
            new LoadItemsTask().execute(app.getShelf().getItems());
        }
    }

    private class ItemAdapter extends BaseAdapter {
        private static final int SPACING_DP = 4;

        private Context mContext;
        private VocabularyItem[] mItems;
        private int spacingPixels;

        public ItemAdapter(Context c, VocabularyItem[] items) {
            mContext = c;
            mItems = items;
            spacingPixels = (int) (c.getResources().getDisplayMetrics().density * SPACING_DP);
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public VocabularyItem getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            int width;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                width = ((GridView) parent).getColumnWidth();
            } else {
                width = parent.getWidth() / ((GridView) parent).getNumColumns() - spacingPixels;
            }
            int height = width * 4 / 3;

            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(width, height));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.transition_add_word));
                }
            } else {
                imageView = (ImageView) view;
            }

            File imageFile = app.getImageFile(mItems[position]);
            Picasso.with(mContext).load(imageFile).fit().into(imageView);
            return imageView;
        }
    }

    private class LoadItemsTask extends AsyncTask<List<VocabularyItem>, Void, ItemAdapter> {
        @Override
        protected ItemAdapter doInBackground(List<VocabularyItem>... lists) {
            if (lists.length == 0) {
                Log.e(DEBUG_TAG, "No list supplied to LoadItemsTask");
                return null;
            }

            VocabularyItem[] items = lists[0].toArray(new VocabularyItem[lists[0].size()]);

            Log.d(DEBUG_TAG, "Start sorting items");
            Arrays.sort(items, new Comparator<VocabularyItem>() {
                @Override
                public int compare(VocabularyItem x, VocabularyItem y) {
                    long a = app.getImageFile(x).lastModified();
                    long b = app.getImageFile(y).lastModified();

                    return a == b ? 0 : a > b ? 1 : -1;
                }
            });
            Log.d(DEBUG_TAG, "Finished sorting items");

            return new ItemAdapter(getActivity(), items);
        }

        @Override
        protected void onPostExecute(ItemAdapter itemAdapter) {
            if (itemAdapter != null) {
                gridGallery.setAdapter(itemAdapter);
                itemCount = itemAdapter.getCount();
            }
        }
    }
}
