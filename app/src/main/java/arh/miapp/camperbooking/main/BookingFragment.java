package arh.miapp.camperbooking.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.Vehicle;

public class BookingFragment extends Fragment {

    List<Booking> bookingList;

    DatabaseReference database2;
    TextView tvBookingBrand;
    TextView tvBookingModel;
    TextView tvBookingYear;
    TextView tvBookingPlate;
    TextView tvBookingTotal;
    Button bBookingValidate;
    TextInputLayout tilBookingDate;
    EditText etBookingDate;
    Date checkin, checkout;
    Vehicle vehicle;
    double grandTotal;

    public BookingFragment() {
    }

    public BookingFragment(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_booking, container, false);
        bookingList = new ArrayList<>();
        // Apuntamos a bookings y obtenemos
        database2 = FirebaseDatabase.getInstance().getReference("bookings");
        getBookings();
        viewBinding(v);
        showVehicle();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //TODO ver si puedo hacer highlighting de las fechas que quiero bloquear
        // https://developer.android.com/reference/com/google/android/material/datepicker/CalendarConstraints.DateValidator
        // No parece nada f??cil, aqui sale ejemplo https://stackoverflow.com/questions/68281693/how-to-highlight-dates-in-android-datepicker
        MaterialDatePicker dp = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds())).build();
        etBookingDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    dp.show(getActivity().getSupportFragmentManager(), "Material_Range");
                    dp.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            // Lo ponemos bonito para el edittext
                            etBookingDate.setText(dp.getHeaderText().toString());
                            // Sacamos la fecha formateada para el filtro del json
                            Pair selectedDates = (Pair) dp.getSelection();
                            final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                            checkin = rangeDate.first;
                            checkout = rangeDate.second;
                            // Formato de fecha en el JSON
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                            long diff = checkout.getTime() - checkin.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);
                            grandTotal = (vehicle.getPricePerDay() * diffDays);
                            DecimalFormat df = new DecimalFormat("0.00");
                            tvBookingTotal.setText("Total: " + df.format(grandTotal) + "???");
                        }
                    });
                    etBookingDate.clearFocus();
                }
            }
        });
        bBookingValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkin == checkout){
                    Toast.makeText(getActivity(), R.string.error_no_checkinout, Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean reserved = false;
                for (Booking booking : bookingList) {
                    if (booking.isReserved(checkin, checkout, vehicle.getPlate())) {
                        reserved = true;
                    }
                }
                if (!reserved) {
                    String key = database2.push().getKey();
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Map<String, Object> bookingMap = new HashMap<>();
                    bookingMap.put("checkin", simpleFormat.format(checkin));
                    bookingMap.put("checkout", simpleFormat.format(checkout));
                    bookingMap.put("idUser", currentFirebaseUser.getUid());
                    bookingMap.put("plate", vehicle.getPlate());
                    bookingMap.put("grandTotal", grandTotal);
                    database2.child(key).updateChildren(bookingMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), R.string.success_booking, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), R.string.try_later, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.error_date_disp, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private void getBookings() {
        database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    bookingList.add(booking);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_booking, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showVehicle() {
        tvBookingBrand.append(vehicle.getBrand());
        tvBookingModel.append(vehicle.getModel());
        tvBookingYear.append(String.valueOf(vehicle.getYear()));
        tvBookingPlate.append(vehicle.getPlate());
    }

    private void viewBinding(View v) {
        tvBookingBrand = v.findViewById(R.id.tvBookingBrand);
        tvBookingModel = v.findViewById(R.id.tvBookingModel);
        tvBookingYear = v.findViewById(R.id.tvBookingYear);
        tvBookingPlate = v.findViewById(R.id.tvBookingPlate);
        tvBookingTotal = v.findViewById(R.id.tvBookingTotal);
        bBookingValidate = v.findViewById(R.id.bBookingValidate);
        tilBookingDate = v.findViewById(R.id.tilBookingDate);
        etBookingDate = v.findViewById(R.id.etBookingDate);
    }
}