package apps.incaclic.jamear.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import apps.incaclic.jamear.Adaptador.RVCateprincipal;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Cateprincipal;
import com.example.jamear.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CategoriaPrincipalFragment extends Fragment {

    private RecyclerView RVCatepr;
    private RVCateprincipal Adapter;
    AsyncHttpClient cliente;
    RequestQueue requestQueue;
    String codigo;

    TextView pri;
    Conexionbd bd = new Conexionbd();

    FragmentTransaction transaction;
    Fragment fragmentcategoria;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_categoria_principal, container, false);

        pri = v.findViewById(R.id.codigopri);

        cliente = new AsyncHttpClient();
        mostrarcategoriaprincipal();
        cargarpri( bd.getUrldatos()+"WS_categoriapri_limit.php");

        pri.addTextChangedListener(mostrar);

        //fragmentcategoria = new CategoriaFragment();
        //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.contenedorFM, fragmentcategoria).commit();

        return v;
    }

    private TextWatcher mostrar = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            categoriaprimero();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void mostrarcategoriaprincipal(){
        String url = bd.getUrldatos()+"WS_categoria_principal.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarcategoriaprincipal(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarcategoriaprincipal(String respuesta){
        final ArrayList<Cateprincipal> lvdatoS = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = jsonArray.getJSONObject(i);
                Cateprincipal cp = new Cateprincipal();

                cp.codigopri = json_data.getInt("Id_categoria_principal");
                cp.nomcatepri = json_data.getString("Nombre_categoria");
                cp.imagenphpcatepri = json_data.getString("Imagen");

                lvdatoS.add(cp);
            }

            RVCatepr = (RecyclerView) getActivity().findViewById(R.id.rvCategoriaprincipal);
            Adapter = new RVCateprincipal(getContext(), lvdatoS);
            RVCatepr.setAdapter(Adapter);
            RVCatepr.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));

            Adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int codigoprincipal = lvdatoS.get(RVCatepr.getChildAdapterPosition(v)).getCodigopri();
                    String codpri = String.valueOf(codigoprincipal);
                    codigo = codpri;
                    abrir();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mandar(){
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorFM,fragmentcategoria).commit();
        transaction.addToBackStack(null);
    }

    public void abrir(){
        Bundle enviar = new Bundle();

        Fragment nuevoFragmento = new CategoriaFragment();
        enviar.putString("catepri", codigo);
        nuevoFragmento.setArguments(enviar);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorFM, nuevoFragmento);
        transaction.addToBackStack(null);
        ((FragmentTransaction) transaction).commit();
    }

    public void cargarpri(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        pri.setText(jsonObject.getString("Id_categoria_principal"));

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR DE CONEXION",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue= Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void categoriaprimero(){
        Bundle enviar = new Bundle();

        Fragment nuevoFragmento = new CategoriaFragment();
        enviar.putString("pri", pri.getText().toString().trim());
        nuevoFragmento.setArguments(enviar);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedorFM, nuevoFragmento);
        transaction.addToBackStack(null);
        ((FragmentTransaction) transaction).commit();
    }

}