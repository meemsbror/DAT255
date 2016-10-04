package gruppn.kasslr;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;




public class VocabularyFeedViewHolder extends RecyclerView.ViewHolder{

    protected TextView name, owner;
    private ImageView image1, image2, image3;


    public VocabularyFeedViewHolder(View v){
        super(v);
        name = (TextView) v.findViewById(R.id.vocabulary_name);
        image1 = (ImageView) v.findViewById(R.id.imageView1);
        image2 = (ImageView) v.findViewById(R.id.imageView2);
        image3 = (ImageView) v.findViewById(R.id.imageView3);
    }

    public void setPictures(Bitmap [] bitmaps){
        this.image1.setImageBitmap(bitmaps[0]);

        if(bitmaps[1]!=null){
            this.image2.setImageBitmap(bitmaps[1]);
        }
        if(bitmaps[2]!=null){
            this.image3.setImageBitmap(bitmaps[3]);
        }
    }

    public void setName(String name){
        this.name.setText(name);
    }
}

