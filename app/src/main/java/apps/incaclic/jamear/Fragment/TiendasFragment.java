package apps.incaclic.jamear.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import apps.incaclic.jamear.Adaptador.RVTiendas;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Tiendas;
import com.example.jamear.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


public class TiendasFragment extends Fragment {

    double latitud, longitud;
    AsyncHttpClient cliente;
    TextView lat1, lng1;
    TextView nohaydatos;
    RecyclerView rvtiendas;

    private RecyclerView RVTienda;
    private RVTiendas Adapter;

    Conexionbd bd = new Conexionbd();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tiendas, container, false);
        lat1 = v.findViewById(R.id.tvlat1);
        lng1 = v.findViewById(R.id.tvlng1);
        rvtiendas = v.findViewById(R.id.rvTienda);

        Bundle datosrecuperados = getArguments();
        /*String lng = datosrecuperados.getString("lng");
        String lat = datosrecuperados.getString("lat");

        lat1.setText(lat);
        lng1.setText(lng);*/

        cliente = new AsyncHttpClient();
        mostrarTiendas();

        return v;
    }

    public void mostrarTiendas(){
        double la = latitud;
        double ln = longitud;

        Bundle datosrecuperados = getArguments();
        String cate = datosrecuperados.getString("Categoria");
        /*String lng = datosrecuperados.getString("lng");
        String lat = datosrecuperados.getString("lat");*/
        //String url = "http://161.132.98.19/WS_Jamear/WS_mostrarTiendas.php?lat="+la+"&lng="+ln+"";
        String url = bd.getUrldatos()+"WS_mostrarTiendas.php?categoria="+cate+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarDatoS(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarDatoS(String respuesta){
        final ArrayList<Tiendas> lvdatoS = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = jsonArray.getJSONObject(i);
                Tiendas t = new Tiendas();
                t.codigoTienda = json_data.getInt("Id_tienda");
                t.RazonSocialTc= json_data.getString("nombre_tienda");
                t.imagenphptienda = json_data.getString("Imagen");
                lvdatoS.add(t);
            }
            RVTienda = (RecyclerView) getActivity().findViewById(R.id.rvTienda);
            Adapter = new RVTiendas(getContext(), lvdatoS);
            RVTienda.setAdapter(Adapter);
            RVTienda.setLayoutManager(new GridLayoutManager(getContext(),1));

            Adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = lvdatoS.get(RVTienda.getChildAdapterPosition(v)).getRazonSocialTc();
                    int codigo = lvdatoS.get(RVTienda.getChildAdapterPosition(v)).getCodigoTienda();

                    Bundle enviar = new Bundle();

                    Fragment nuevoFragmento = new PlatillosFragment();
                    enviar.putInt("codigoTienda", codigo);
                    enviar.putString("nomTienda",nombre);
                    nuevoFragmento.setArguments(enviar);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, nuevoFragmento);
                    transaction.addToBackStack(null);
                    ((FragmentTransaction) transaction).commit();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
