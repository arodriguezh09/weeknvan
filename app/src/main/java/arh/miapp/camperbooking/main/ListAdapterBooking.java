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

public class ListAdapterBooking extends RecyclerView.Adapter<ListAdapterBooking.ViewHolder> implements View.OnClickListener{

    private List<Vehicle> vehicleList;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListAdapterBooking(List<Vehicle> vehicleList, Context context) {
        this.vehicleList = vehicleList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapterBooking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_vehicle, parent, false);
        view.setOnClickListener(this);
        return new ListAdapterBooking.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterBooking.ViewHolder holder, int position) {
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
        private TextView grandTotal;
        private TextView city;
        private TextView bookingDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.cvBookPhoto);
            brand = itemView.findViewById(R.id.cvBookBrand);
            model = itemView.findViewById(R.id.cvBookModel);
            grandTotal = itemView.findViewById(R.id.cvBookPrice);
            city = itemView.findViewById(R.id.cvBookCity);
            bookingDate = itemView.findViewById(R.id.cvBookDate);
        }
        public void bindData(final Vehicle vehicle){
            DecimalFormat df = new DecimalFormat("0.00");
            photo.setImageBitmap(vehicle.getBitmap());
            brand.setText(vehicle.getBrand());
            model.setText(vehicle.getModel());
            grandTotal.setText(String.valueOf(df.format(vehicle.getPricePerDay()))+"€/día");
            city.setText(vehicle.getCity());
            //TODO a ver como coloco la fecha
            //bookingDate.setText(vehicle.getCity());
        }
    }
}
