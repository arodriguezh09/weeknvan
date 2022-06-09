package arh.miapp.camperbooking.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.Vehicle;

public class ListAdapterBooking extends RecyclerView.Adapter<ListAdapterBooking.ViewHolder> implements View.OnClickListener{

    private List<Vehicle> vehicleList;
    private List<Booking> bookingList;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListAdapterBooking(List<Vehicle> vehicleList, List<Booking> bookingList,Context context) {
        this.vehicleList = vehicleList;
        this.bookingList = bookingList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapterBooking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_booking, parent, false);
        view.setOnClickListener(this);
        return new ListAdapterBooking.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterBooking.ViewHolder holder, int position) {
        holder.bindData(bookingList.get(position), vehicleList);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
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
        public void bindData(final Booking booking, List<Vehicle> vehicleList){
            String plate = booking.getPlate();
            Vehicle vehicle = new Vehicle();
            for(Vehicle v:vehicleList){
                if (v.getPlate().equals(plate)){
                    vehicle=v;
                }
            }
            DecimalFormat df = new DecimalFormat("0.00");
            photo.setImageBitmap(vehicle.getBitmap());
            brand.setText(vehicle.getBrand());
            model.setText(vehicle.getModel());
            grandTotal.setText(String.valueOf(df.format(booking.getGrandTotal()))+"€");
            city.setText(vehicle.getCity());
            //TODO convertirFecha
            bookingDate.setText(booking.getCheckin() + " -> "+booking.getCheckout());
        }
    }
}
