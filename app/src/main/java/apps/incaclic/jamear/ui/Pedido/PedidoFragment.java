package apps.incaclic.jamear.ui.Pedido;

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

import apps.incaclic.jamear.Adaptador.RVPedido;
import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Pedido;
import com.example.jamear.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class PedidoFragment extends Fragment {

    Conexionbd bd = new Conexionbd();

    private RVPedido rvPedido;
    TextView hora, fecha, estado, numpedido;
    ListView lvpedido;
    String emisor = "";
    AsyncHttpClient cliente;

    TextView fechacambiar;
    Button cambiarfecha;
    private int dia, anio;
    private int mes;

    SwipeRefreshLayout refresacarestadope;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pedido, container, false);
        hora = v.findViewById(R.id.tvhoraPe);
        fecha = v.findViewById(R.id.tvfechaPe);
        estado = v.findViewById(R.id.tvestadoPe);
        numpedido = v.findViewById(R.id.tvpedidoPe);
        lvpedido = v.findViewById(R.id.lvPedido);
        emisor = Preferences.obtenerEstadoString(getActivity(),Preferences.PREFERENCIA_USUARIO);
        cliente = new AsyncHttpClient();

        fechacambiar = v.findViewById(R.id.tvfechacambiar);
        cambiarfecha = v.findViewById(R.id.btncambiarfecha);

        cambiarfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarfecha();
            }
        });

        fechacompra();

        context();
        mostrarestado();

        fechacambiar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                context();
            }
        });

        return v;
    }

    public void fechacompra(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fechas = dateFormat.format(date);
        fechacambiar.setText(fechas);
    }

    public void mostrarestado(){
        String url = bd.getUrldatos()+"WS_estadopedido.php?usuario="+emisor+"&fecha="+fechacambiar.getText()+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarestado(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarestado(String respuesta){
        final ArrayList<Pedido> lvpe = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                Pedido pe = new Pedido();
                pe.setFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                pe.setHora(jsonArray.getJSONObject(i).getString("Hora"));
                pe.setNumpedido(jsonArray.getJSONObject(i).getString("num_compra"));
                pe.setNombredelatienda(jsonArray.getJSONObject(i).getString("Nom_tienda"));
                pe.setEstado(jsonArray.getJSONObject(i).getString("estado"));
                lvpe.add(pe);
            }
            lvpedido = getView().findViewById(R.id.lvPedido);
            rvPedido = new RVPedido(getContext(), lvpe);
            lvpedido.setAdapter(rvPedido);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void context(){
        mostrarestado();
        refrescar(180000);
    }

    private void refrescar (int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                context();
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    public void cambiarfecha(){
        final Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fechacambiar.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        }, anio,mes,dia);

        datePickerDialog.show();
    }

}
