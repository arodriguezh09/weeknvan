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
import arh.miapp.camperbooking.objects.ItemBanner;

public class ListAdapterItemTop extends RecyclerView.Adapter<ListAdapterItemTop.ViewHolder> implements View.OnClickListener{

    private List<ItemBanner> items;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListAdapterItemTop(List<ItemBanner> items, Context context) {
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapterItemTop.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_search_element, parent, false);
        view.setOnClickListener(this);
        return new ListAdapterItemTop.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterItemTop.ViewHolder holder, int position) {
        holder.bindData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
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
        public void bindData(final ItemBanner item){
            photo.setImageResource(item.getImage());
            brand.setText(item.getText());
        }
    }
}
