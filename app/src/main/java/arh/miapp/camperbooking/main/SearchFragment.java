package arh.miapp.camperbooking.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.listadapters.ListAdapterItemBottom;
import arh.miapp.camperbooking.listadapters.ListAdapterItemTop;
import arh.miapp.camperbooking.objects.ItemBanner;
import arh.miapp.camperbooking.objects.Vehicle;

public class SearchFragment extends Fragment {

    ArrayList<String> cities;
    TextInputLayout tilDDMenu;
    TextInputLayout tilRangeDate;
    AutoCompleteTextView etDDMenu;
    EditText etRangeDate;
    Button bSearch;
    Button rentYourCamper;
    String city;
    Date checkin, checkout;
    DatabaseReference dbRef;
    List<ItemBanner> itemsTopArray;
    List<ItemBanner> itemsBotArray;
    Fragment vehiclesFragment;
    Fragment vehiclesUploadFragment;

    ListAdapterItemTop laTop;
    RecyclerView itemsTop;

    ListAdapterItemBottom laBottom;
    RecyclerView itemsBottom;


    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cities = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        findViews(v);
        vehiclesFragment = new VehiclesFragment();
        // Adapter para el spinner
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.option_item, cities);
        etDDMenu.setAdapter(citiesAdapter);
        MaterialDatePicker dp = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds())).build();
        dbRef = FirebaseDatabase.getInstance().getReference("vehicles");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String city = dataSnapshot.getValue(Vehicle.class).getCity();
                    if (!cities.contains(city)) {
                        cities.add(city);
                    }
                }
                citiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_booking, Toast.LENGTH_SHORT).show();
            }
        });
        etRangeDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // creo que seteando el texto hardcode y bloqueando la entrada de texto manual lo solucionamos
                    dp.show(getActivity().getSupportFragmentManager(), "Material_Range");
                    dp.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            // Lo ponemos bonito para el edittext
                            etRangeDate.setText(dp.getHeaderText().toString());
                            // Sacamos la fecha formateada para el filtro del json
                            Pair selectedDates = (Pair) dp.getSelection();
                            final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                            checkin = rangeDate.first;
                            checkout = rangeDate.second;
                            // Formato de fecha en el JSON
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                        }
                    });
                    etRangeDate.clearFocus();
                }
            }
        });

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = etDDMenu.getText().toString();
                if (checkin == null && checkout == null) {
                    // Constructor 2 sin fecha
                    ((BottomNavigationActivity) getActivity()).search(city, new Date(), new Date());
                } else {
                    // Recuperar all
                    ((BottomNavigationActivity) getActivity()).search(city, checkin, checkout);
                }
                etDDMenu.setText("");
            }
        });

        rentYourCamper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationActivity) getActivity()).loadFragment(new VehiclesUploadFragment(), true);
            }
        });

        itemsTopArray = new ArrayList<>();
        itemsBotArray = new ArrayList<>();
        fillList();

        LinearLayoutManager layoutManagerTop
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerBot
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        laTop = new ListAdapterItemTop(itemsTopArray, getContext());
        itemsTop = (RecyclerView) v.findViewById(R.id.itemRvTop);
        itemsTop.setLayoutManager(layoutManagerTop);
        laTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemBanner ib = itemsTopArray.get(itemsTop.getChildAdapterPosition(view));
                vehiclesFragment = new VehiclesFragment(ib.getCity(), new Date(), new Date());
                ((BottomNavigationActivity) getActivity()).loadFragment(vehiclesFragment, true);
            }
        });
        itemsTop.setAdapter(laTop);

        laBottom = new ListAdapterItemBottom(itemsBotArray, getContext());
        itemsBottom = (RecyclerView) v.findViewById(R.id.itemRvBottom);
        itemsBottom.setLayoutManager(layoutManagerBot);
        laBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemBanner ib = itemsBotArray.get(itemsBottom.getChildAdapterPosition(view));

                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                String dateInString = ib.getCheckin();
                String dateOutString = ib.getCheckout();
                try {
                    Date dateIn = formatter1.parse(dateInString);
                    Date dateOut = formatter1.parse(dateOutString);
                    vehiclesFragment = new VehiclesFragment(ib.getCity(), dateIn, dateOut);
                    ((BottomNavigationActivity) getActivity()).loadFragment(vehiclesFragment, true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        itemsBottom.setAdapter(laBottom);

        return v;
    }

    // Método para buscar las views
    private void findViews(View v) {
        tilDDMenu = (TextInputLayout) v.findViewById(R.id.tilDDMenu);
        etDDMenu = (AutoCompleteTextView) v.findViewById(R.id.tvDDMenu);
        tilRangeDate = v.findViewById(R.id.tilRangeDate);
        etRangeDate = v.findViewById(R.id.etRangeDate);
        bSearch = v.findViewById(R.id.bSearch);
        rentYourCamper = v.findViewById(R.id.rentYourCamper);
    }


    private void fillList() {
        itemsTopArray.add(new ItemBanner(R.drawable.sevilla, "Conoce Sevilla", "Sevilla"));
        itemsTopArray.add(new ItemBanner(R.drawable.caceres, "Ven a Cáceres", "Cáceres"));
        itemsTopArray.add(new ItemBanner(R.drawable.barcelona, "Barcelona y alrededores", "Barcelona"));
        itemsTopArray.add(new ItemBanner(R.drawable.malaga, "Playas de Málaga", "Málaga"));

        itemsBotArray.add(new ItemBanner(R.drawable.malaga, "Málaga en verano", "Málaga", "21/06/2022", "23/09/2022"));
        itemsBotArray.add(new ItemBanner(R.drawable.verano, "Vacaciones de verano", "01/06/2022", "31/08/2022"));
        itemsBotArray.add(new ItemBanner(R.drawable.caceres, "Primavera en Cáceres", "Cáceres", "20/03/2022", "21/06/2022"));
        itemsBotArray.add(new ItemBanner(R.drawable.barcelona, "Pirineos desde Barcelona", "Barcelona", "21/12/2022", "20/03/2023"));
        itemsBotArray.add(new ItemBanner(R.drawable.malaga, "Navidad en Málaga", "Málaga", "21/12/2022", "07/01/2023"));
    }

}