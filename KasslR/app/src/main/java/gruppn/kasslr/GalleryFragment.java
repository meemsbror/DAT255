package gruppn.kasslr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import gruppn.kasslr.model.VocabularyItem;

public class GalleryFragment extends Fragment {
    public static final int REQUEST_EDIT_ITEM = 1;
    public static final String EXTRA_SELECT_MODE = "selectMode";

    private static final String DEBUG_TAG = "GalleryFragment";

    private Kasslr app;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    private boolean selectMode = false;
    private List<VocabularyItem> selectedItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (Kasslr) getActivity().getApplication();

        if (getArguments() != null) {
            selectMode = getArguments().getBoolean(EXTRA_SELECT_MODE, false);
        }

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
        adapter = new ItemAdapter(new ArrayList<VocabularyItem>(), selectMode);
        recyclerView.setAdapter(adapter);

        new LoadItemsTask().execute(app.getShelf().getItems());
    }

    public List<VocabularyItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_ITEM && resultCode == Activity.RESULT_OK) {
            int index = data.getIntExtra(EditItemActivity.RESULT_ITEM_INDEX, -1);
            if (index == -1) {
                return;
            }

            VocabularyItem item = app.getShelf().getItems().get(index);

            int position = adapter.getPosition(item);
            int action = data.getIntExtra(EditItemActivity.RESULT_ACTION, EditItemActivity.ACTION_EDIT);

            if (action == EditItemActivity.ACTION_REMOVE) {
                adapter.notifyItemRemoved(position);
            } else {
                adapter.notifyItemChanged(position);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<VocabularyItem> mItems;
        private boolean mSelectMode;

        public ItemAdapter(List<VocabularyItem> items, boolean selectMode) {
            setItems(items);
            mSelectMode = selectMode;
        }

        public void setItems(List<VocabularyItem> items) {
            mItems = items;
            if (mSelectMode) {
                removeUnnamedItems();
            }
        }

        private void removeUnnamedItems() {
            for (Iterator<VocabularyItem> it = mItems.iterator(); it.hasNext(); ) {
                VocabularyItem item = it.next();
                if (item.getName().isEmpty()) {
                    it.remove();
                }
            }
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vocabulary_item, parent, false);
            return new ItemViewHolder(view, mSelectMode);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.setItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public int getPosition(VocabularyItem item) {
            return mItems.indexOf(item);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView name;
        private ImageView image;
        private ImageView gradient;
        private ImageView checkbox;

        private boolean mSelectMode;

        public ItemViewHolder(final View view, boolean selectMode) {
            super(view);
            card = (CardView) view;
            name = (TextView) view.findViewById(R.id.txtItem);
            image = (ImageView) view.findViewById(R.id.imgItem);
            gradient = (ImageView) view.findViewById(R.id.vocabulary_item_gradient);
            checkbox = (ImageView) view.findViewById(R.id.imgCheckbox);
            mSelectMode = selectMode;
            if (!selectMode) {
                card.removeView(gradient);
                card.removeView(checkbox);
            } else {
                gradient.setImageResource(R.drawable.cardgradient);
            }
        }

        public void setItem(final VocabularyItem item) {
            if (mSelectMode) {
                card.setOnClickListener(new CardView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!item.getName().isEmpty()) {
                            setSelected(item, !getSelectedItems().contains(item));
                        }
                    }
                });
            } else {
                card.setOnClickListener(new CardView.OnClickListener() {
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
                });
            }

            image.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getContext())
                            .load(app.getImageFile(item))
                            .resize(image.getMeasuredWidth(), image.getMeasuredWidth() * 4 / 3)
                            .into(image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    name.setText(item.getName());
                                    name.setVisibility(item.getName().isEmpty() ? View.GONE : View.VISIBLE);

                                    if (mSelectMode) {
                                        setSelected(item, getSelectedItems().contains(item) && !item.getName().isEmpty());
                                    }

                                    card.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    // Do nothing
                                }
                            });
                }
            });
        }

        private void setSelected(VocabularyItem item, boolean selected) {
            Picasso.with(getContext())
                    .load(selected ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked)
                    .into(checkbox, new Callback() {
                        @Override
                        public void onSuccess() {
                            Drawable drawable = DrawableCompat.wrap(checkbox.getDrawable());
                            DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));
                        }

                        @Override
                        public void onError() {
                            // Ignored
                        }
                    });

            if (selected) {
                if (!getSelectedItems().contains(item)) {
                    getSelectedItems().add(item);
                }
            } else {
                getSelectedItems().remove(item);
            }
        }
    }

    private class LoadItemsTask extends AsyncTask<List<VocabularyItem>, Void, List<VocabularyItem>> {
        @Override
        protected List<VocabularyItem> doInBackground(List<VocabularyItem>... lists) {
            if (lists.length == 0) {
                Log.e(DEBUG_TAG, "No list supplied to LoadItemsTask");
                return null;
            }

            final List<VocabularyItem> items = new ArrayList<>(lists[0]);

            for (Iterator<VocabularyItem> it = items.iterator(); it.hasNext(); ) {
                VocabularyItem item = it.next();

                if (!item.isMine()) {
                    // Filter out items from downloaded vocabularies
                    it.remove();
                } else {
                    // Get last modified date
                    item.setLastModified(app.getImageFile(item).lastModified());
                }
            }

            // Sort items by date, descending
            Collections.sort(items, new Comparator<VocabularyItem>() {
                @Override
                public int compare(VocabularyItem x, VocabularyItem y) {
                    long a = x.getLastModified();
                    long b = y.getLastModified();

                    return a == b ? 0 : a < b ? 1 : -1;
                }
            });

            return items;
        }

        @Override
        protected void onPostExecute(List<VocabularyItem> items) {
            if (items != null) {
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        }
    }
}
