package arh.miapp.camperbooking.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Vehicle;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements View.OnClickListener{

    private List<Vehicle> vehicleList;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListAdapter(List<Vehicle> vehicleList, Context context) {
        this.vehicleList = vehicleList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_vehicle, parent, false);
        view.setOnClickListener(this);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.bindData(vehicleList.get(position));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
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
        private TextView model;
        private TextView year;
        private TextView passengers;
        private TextView beds;
        private TextView pricePerDay;
        private RatingBar rating;
        private TextView city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.cvPhoto);
            brand = itemView.findViewById(R.id.cvBrand);
            model = itemView.findViewById(R.id.cvModel);
            year = itemView.findViewById(R.id.cvYear);
            passengers = itemView.findViewById(R.id.cvSeats);
            beds = itemView.findViewById(R.id.cvBeds);
            pricePerDay = itemView.findViewById(R.id.cvPrice);
            rating = itemView.findViewById(R.id.cvRating);
            city = itemView.findViewById(R.id.cvCity);
        }
        public void bindData(final Vehicle vehicle){
            DecimalFormat df = new DecimalFormat("0.00");
            photo.setImageBitmap(vehicle.getBitmap());
            brand.setText(vehicle.getBrand());
            model.setText(vehicle.getModel());
            year.setText("Año: "+String.valueOf(vehicle.getYear()));
            passengers.setText("x"+String.valueOf(vehicle.getPassengers()));
            beds.setText("x"+String.valueOf(vehicle.getBeds()));
            pricePerDay.setText(String.valueOf(df.format(vehicle.getPricePerDay()))+"€/día");
            rating.setRating((float)vehicle.getRating());
            city.setText(vehicle.getCity());
        }
    }
}
