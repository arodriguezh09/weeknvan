package arh.miapp.camperbooking.main;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arh.miapp.camperbooking.R;

public class SearchFragment extends Fragment {

    ArrayList<String> cities;
    TextInputLayout tilDDMenu;
    TextInputLayout tilRangeDate;
    AutoCompleteTextView etDDMenu;
    EditText etRangeDate;
    Button bSearch;
    String city;
    Date checkin, checkout;
    CardView cvSevilla;
    CardView cvCaceres;


    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        cities = new ArrayList<>();
        fillList();
        cvSevilla = v.findViewById(R.id.cvSevilla);
        cvCaceres = v.findViewById(R.id.cvCaceres);
        tilDDMenu = (TextInputLayout) v.findViewById(R.id.tilDDMenu);
        etDDMenu = (AutoCompleteTextView) v.findViewById(R.id.tvDDMenu);
        tilRangeDate = v.findViewById(R.id.tilRangeDate);
        etRangeDate = v.findViewById(R.id.etRangeDate);
        bSearch = v.findViewById(R.id.bSearch);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.option_item, cities);
        // TODO extra: si elijo ciudad, cambio de fragment y vuelvo, solo me cargará esa ciudad. Por que? buena pregunta xd
        etDDMenu.setAdapter(citiesAdapter);

        MaterialDatePicker dp = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds())).build();

        etRangeDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // TODO extra: intentar abrir el fragment de otra manera para que no ocupe toda la pantalla
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
            }
        });
        cvSevilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationActivity) getActivity()).search("Sevilla", new Date(), new Date());
            }
        });
        cvCaceres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationActivity) getActivity()).search("Mérida", new Date(), new Date());
            }
        });
        return v;
    }

    private void fillList() {
        // TODO extra: llenar el arrayList con lo que recupere de la BBDD
        // Si no también está bien, ya que nos muestra posibilidades en las que no existirían campers disponibles y mostramos el mensaje
        cities.add("Mérida");
        cities.add("Sevilla");
        cities.add("Cáceres");
        cities.add("Madrid");
        cities.add("Málaga");
        cities.add("Barcelona");
    }

}