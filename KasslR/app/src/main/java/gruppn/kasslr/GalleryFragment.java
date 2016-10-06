package gruppn.kasslr;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gruppn.kasslr.model.VocabularyItem;

public class GalleryFragment extends Fragment {
    public static final int REQUEST_EDIT_ITEM = 1;

    private static final String DEBUG_TAG = "GalleryFragment";
    private static final FileFilter FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".jpg");
        }
    };

    private Kasslr app;

    //private GridView gridGallery;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private int itemCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        /*
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
        */

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_gallery);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private int margin = getResources().getDimensionPixelSize(R.dimen.gallery_spacing);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(margin, margin, margin, margin);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter(new ArrayList<VocabularyItem>());
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(DEBUG_TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == REQUEST_EDIT_ITEM) {
            if (resultCode >= 0 && resultCode < itemCount) {
                // Get item
                VocabularyItem item = app.getShelf().getItems().get(resultCode);
                adapter.notifyItemChanged(item);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<VocabularyItem> mItems;

        public ItemAdapter(List<VocabularyItem> items) {
            mItems = items;
        }

        public void setItems(List<VocabularyItem> items) {
            mItems = items;
            notifyDataSetChanged();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vocabulary_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.setItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public VocabularyItem getItem(int position) {
            return mItems.get(position);
        }

        public void notifyItemChanged(VocabularyItem item) {
            int position = mItems.indexOf(item);
            if (position >= 0 && position < mItems.size()) {
                notifyItemChanged(position);
            }
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;

        public ItemViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtItem);
            image = (ImageView) view.findViewById(R.id.imgItem);

        }

        public void setItem(final VocabularyItem item) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.setSharedBitmap(((BitmapDrawable) image.getDrawable()).getBitmap());

                    Intent intent = new Intent(getActivity(), EditItemActivity.class);
                    intent.putExtra(EditItemActivity.EXTRA_ITEM_INDEX, app.getShelf().getItems().indexOf(item));

                    Pair<View, String>[] transitions;
                    if (item.getName().isEmpty()) {
                        transitions = new Pair[] {
                                Pair.create((View) image, getString(R.string.transition_edit_item_image)) };
                    } else {
                        transitions = new Pair[] {
                                Pair.create((View) image, getString(R.string.transition_edit_item_image)),
                                Pair.create((View) name, getString(R.string.transition_edit_item_name)) };
                    }

                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), transitions);

                    ActivityCompat.startActivityForResult(getActivity(), intent, REQUEST_EDIT_ITEM, options.toBundle());
                }
            };

            name.setOnClickListener(listener);
            image.setOnClickListener(listener);

            name.setVisibility(View.GONE);

            image.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getContext())
                            .load(app.getImageFile(item))
                            .resize(image.getMeasuredWidth(), image.getMeasuredWidth() * 4 / 3)
                            .into(image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (!item.getName().isEmpty()) {
                                        name.setText(item.getName());
                                        name.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    // Do nothing
                                }
                            });
                }
            });
        }
    }

    private class LoadItemsTask extends AsyncTask<List<VocabularyItem>, Void, List<VocabularyItem>> {
        @Override
        protected List<VocabularyItem> doInBackground(List<VocabularyItem>... lists) {
            if (lists.length == 0) {
                Log.e(DEBUG_TAG, "No list supplied to LoadItemsTask");
                return null;
            }

            // Get last modified date for each item
            final List<VocabularyItem> items = new ArrayList<>(lists[0]);
            for (VocabularyItem item : items) {
                item.setLastModified(app.getImageFile(item).lastModified());
            }

            // Sort items by date
            Log.d(DEBUG_TAG, "Start sorting items");
            Collections.sort(items, new Comparator<VocabularyItem>() {
                @Override
                public int compare(VocabularyItem x, VocabularyItem y) {
                    long a = x.getLastModified();
                    long b = y.getLastModified();

                    return a == b ? 0 : a > b ? 1 : -1;
                }
            });
            Log.d(DEBUG_TAG, "Finished sorting items");

            return items;
        }

        @Override
        protected void onPostExecute(List<VocabularyItem> items) {
            if (items != null) {
                adapter.setItems(items);
                itemCount = items.size();
            }
        }
    }
}
