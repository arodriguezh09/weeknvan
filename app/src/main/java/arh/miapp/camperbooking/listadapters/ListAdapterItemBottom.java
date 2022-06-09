package arh.miapp.camperbooking.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import arh.miapp.camperbooking.R;

public class ListAdapterItemBottom extends RecyclerView.Adapter<ListAdapterItemBottom.ViewHolder> implements View.OnClickListener{

    private List<String> titles;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListAdapterItemBottom(List<String> titles, Context context) {
        this.titles = titles;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapterItemBottom.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_search_element, parent, false);
        view.setOnClickListener(this);
        return new ListAdapterItemBottom.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterItemBottom.ViewHolder holder, int position) {
        holder.bindData(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener!=null) {
            listener.onClick(view);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView photo;
        private TextView brand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.elementImage);
            brand = itemView.findViewById(R.id.elementTitle);
        }
        public void bindData(final String titles){
            /*photo.setImageBitmap(vehicle.getBitmap());
            brand.setText(vehicle.getBrand());*/

            photo.setImageResource(R.drawable.caceres);
            brand.setText("Hola Cáceres");
        }
    }
}
