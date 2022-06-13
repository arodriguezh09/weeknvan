package arh.miapp.camperbooking.listadapters;

import android.graphics.Bitmap;
import android.print.PrintDocumentAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.ablanco.zoomy.Zoomy;
import com.google.android.material.slider.Slider;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import arh.miapp.camperbooking.R;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> implements View.OnClickListener{

    ArrayList<Bitmap> images;
    private View.OnClickListener listener;


    public SliderAdapter(ArrayList<Bitmap> images) {
        this.images = images;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        view.setOnClickListener(this);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.imageView.setImageBitmap(images.get(position));
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void onClick(View view) {
        if (listener!=null) {
            listener.onClick(view);
        }
    }

    public class Holder extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public Holder(View v) {
            super(v);
            imageView = v.findViewById(R.id.sliderImageView);
        }
    }

}
