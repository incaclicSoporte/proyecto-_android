package apps.incaclic.jamear.ui.Historial;

import com.android.volley.RequestQueue;

import apps.incaclic.jamear.Adaptador.RVHistorial;
import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Historial;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jamear.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HistorialFragment extends Fragment {

    Conexionbd bd = new Conexionbd();

    private RVHistorial aHistorial;
    AsyncHttpClient cliente;
    String emisor = "";
    ListView historialL;
    RequestQueue requestQueue;

    TextView tvmostrarfechaH, mesf;
    Button btnfechaH;
    private int dia, anio;
    private int mes;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_historial, container, false);

        cliente = new AsyncHttpClient();
        emisor = Preferences.obtenerEstadoString(getActivity(),Preferences.PREFERENCIA_USUARIO);
        historialL = v.findViewById(R.id.lvHistorial);

        tvmostrarfechaH = v.findViewById(R.id.tvmostrarfechaH);
        mesf = v.findViewById(R.id.tvfechames);
        btnfechaH = v.findViewById(R.id.btnfechaH);
        btnfechaH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarfecha();
            }
        });

        fechahistorial();
        fechacompra();
        mostrarProductos();

        mesf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mostrarProductos();
            }
        });

        return v;
    }

    public void fechacompra(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date date = new Date();

        String fechas = dateFormat.format(date);
        mesf.setText(fechas);
    }

    public void fechahistorial(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fechas = dateFormat.format(date);
        tvmostrarfechaH.setText(fechas);
    }

    public void mostrarProductos() {
        String url = bd.getUrldatos()+"WS_mostrarHistorial.php?fecha="+mesf.getText().toString().trim()+"&usuario="+emisor+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarHistorial(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarHistorial(String respuesta){

        final ArrayList<Historial> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = jsonArray.getJSONObject(i);
                Historial ht = new Historial();

                ht.codigoH = json_data.getInt("Id_compra");
                ht.fechaH = json_data.getString("Fecha");
                ht.totalH = json_data.getString("total");
                ht.detalleH = json_data.getString("Descripcion");

                lista.add(ht);

                historialL = getView().findViewById(R.id.lvHistorial);
                aHistorial = new RVHistorial(getContext(), lista);
                historialL.setAdapter(aHistorial);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cambiarfecha(){
        final Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvmostrarfechaH.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                mesf.setText(""+(month+1)+"");
            }
        }, anio,mes,dia);

        datePickerDialog.show();
    }

}
