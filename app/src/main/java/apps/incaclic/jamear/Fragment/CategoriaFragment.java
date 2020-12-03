package apps.incaclic.jamear.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import apps.incaclic.jamear.Adaptador.RVCategoria;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Categoria;

import com.example.jamear.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoriaFragment extends Fragment {

    private RecyclerView RVCate;
    private RVCategoria Adapter;
    TextView lat, lng, ayuda;

    AsyncHttpClient cliente;
    double latitud, longitud;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categoria, container, false);
        lat = v.findViewById(R.id.tvlat);
        lng = v.findViewById(R.id.tvlng);
        ayuda = v.findViewById(R.id.tvayuda);
        cliente = new AsyncHttpClient();

        /////////////////////////////////////////////
        /*if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 500);
        } else {
            locationStart();
        }*/

        /////////////////////////////////////////////
        mostrarCategorias();

        return v;
    }

    ////////////////////////////////////////////////
    /*private void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setCategoriaFragment(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 500);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 500) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public class Localizacion implements LocationListener {
        CategoriaFragment categoriaFragment;
        public CategoriaFragment getCategoriaFragment() {
            return categoriaFragment;
        }
        public void setCategoriaFragment(CategoriaFragment categoriaFragment) {
            this.categoriaFragment = categoriaFragment;

        }
        @Override
        public void onLocationChanged(Location loc) {

            latitud = loc.getLatitude();
            longitud = loc.getLongitude();
            lat.setText(""+loc.getLatitude());
            lng.setText(""+loc.getLongitude());

            //context();
            //mostrarCategorias();

        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }*/

    ////////////////////////////////////////////////////////////////

    public void mostrarCategorias(){
        Conexionbd bd = new Conexionbd();

        Bundle datos = getArguments();
        String codigo = datos.getString("pri");
        String codpri = datos.getString("catepri");
        if (codigo != null) {
            ayuda.setText(codigo);
        }else{
            ayuda.setText(codpri);
        }

        String url = bd.getUrldatos()+"WS_mostrarCategoriaT.php?catepri="+ayuda.getText().toString()+"";
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
        final ArrayList<Categoria> lvdatoS = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = jsonArray.getJSONObject(i);
                Categoria cate = new Categoria();

                cate.codCategoria = json_data.getInt("Id_categoria_tienda");
                cate.nomCategoria = json_data.getString("Categoria_tienda");
                cate.imagenphpcate = json_data.getString("Imagen");
                lvdatoS.add(cate);
            }
            RVCate = (RecyclerView) getActivity().findViewById(R.id.rvCategoria);
            Adapter = new RVCategoria(getContext(), lvdatoS);
            RVCate.setAdapter(Adapter);
            RVCate.setLayoutManager(new GridLayoutManager(getContext(),1));

            Adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nombre = lvdatoS.get(RVCate.getChildAdapterPosition(v)).getCodCategoria();
                    String mensaje = String.valueOf(nombre);

                    Bundle enviar = new Bundle();

                    Fragment nuevoFragmento = new TiendasFragment();
                    enviar.putString("Categoria", mensaje);
                    /*enviar.putString("lat", lat.getText().toString());
                    enviar.putString("lng", lng.getText().toString());*/
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

    /*public void context(){
        refrescar(2000);
    }

    public void refrescar (int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                context();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }*/
}
