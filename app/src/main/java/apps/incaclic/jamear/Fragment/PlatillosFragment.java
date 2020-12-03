package apps.incaclic.jamear.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import apps.incaclic.jamear.Adaptador.RVPlatillos;
import apps.incaclic.jamear.CarritoActivity;
import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Platillos;
import apps.incaclic.jamear.Entidades.TipoMenu;
import apps.incaclic.jamear.LoginActivity;
import com.example.jamear.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PlatillosFragment extends Fragment {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView RVPlato;
    private RVPlatillos Adapter;

    RequestQueue requestQueue;
    AsyncHttpClient cliente;

    ImageView cerrar;
    TextView cantidad, nomPlaP, precioPl, descP;
    Button aPlatillo, mas, menos;
    Spinner menutipo;

    String codigoPlatillo;
    String codigoUsuario = "";
    String codigoTienda = "";
    String emisor = "";
    String Tienda, codTMenu;
    String tiendaveri;
    ImageView imagenplatillo;
    int codigodelatienda;

    SwipeRefreshLayout refreshplatillo;

    Conexionbd bd = new Conexionbd();

    ////////////////////////////////////////////

    TextView veri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_platillos, container, false);

        cliente = new AsyncHttpClient();
        menutipo = v.findViewById(R.id.spnMenuTipo);
        refreshplatillo = v.findViewById(R.id.srlplatillos);
        emisor = Preferences.obtenerEstadoString(getActivity(),Preferences.PREFERENCIA_USUARIO);

        Bundle datosrecuperados = getArguments();
        Tienda = datosrecuperados.getString("nomTienda");
        codigodelatienda = datosrecuperados.getInt("codigoTienda");

        refreshplatillo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mostrarMenu();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshplatillo.setRefreshing(false);
                    }
                },1000);
            }
        });

        datousu(bd.getUrldatos()+"WS_datoCliente.php?usuario="+emisor+"");
        tiendaverificacion (bd.getUrldatos()+"WS_mostrarCarrito.php?usuario="+emisor+"");

        mostrarMenu();

        return v;
    }

    public void datousu (String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        codigoUsuario =  (jsonObject.getString("Id_usuario_cliente"));
                    } catch (JSONException e) {
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void mostrarMenu() {
        String url = bd.getUrldatos()+"WS_menuTienda.php?codigo="+codigodelatienda+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarMenu(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarMenu(String respuesta){

        final ArrayList<TipoMenu> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                TipoMenu tm = new TipoMenu();

                tm.setCodigoMenu(jsonArray.getJSONObject(i).getInt("Id_tipomenu"));
                tm.setNombreMenu(jsonArray.getJSONObject(i).getString("Nombre_menu"));
                lista.add(tm);

                menutipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TipoMenu menu = lista.get(position);
                        StringBuffer sb = new StringBuffer();
                        sb.append(""+menu.getCodigoMenu());
                        codTMenu = sb.toString();
                        //Toast.makeText(getContext(),codTMenu,Toast.LENGTH_SHORT).show();
                        new platilloporMenu().execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            ArrayAdapter<TipoMenu> a = new ArrayAdapter<>(getActivity(),android.R.layout.simple_expandable_list_item_1, lista);
            menutipo.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class platilloporMenu extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getContext());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tCargando...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(bd.getUrldatos()+"WS_platillosTipomenu.php?codigo="+codTMenu+"");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String resultado) {

            pdLoading.dismiss();
            final List<Platillos> data=new ArrayList<>();

            pdLoading.dismiss();
            try {

                JSONArray jArray = new JSONArray(resultado);

                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    Platillos pla = new Platillos();

                    pla.codigoPla = json_data.getInt("Id_platillo");
                    pla.nomPlatillos = json_data.getString("Platillo");
                    pla.precio = json_data.getString("Precio");
                   //pla.stock = json_data.getString("Stock");
                    pla.Descripcion = json_data.getString("Descripcion");
                    codigoTienda = json_data.getString("Id_tienda");

                    pla.imagenphppla = json_data.getString("Imagen");

                    ((ArrayList) data).add(pla);

                }
                        RVPlato = (RecyclerView)getView().findViewById(R.id.rvPlatillos);
                        Adapter = new RVPlatillos(getContext(), data);

                        RVPlato.setAdapter(Adapter);
                        RVPlato.setLayoutManager(new GridLayoutManager(getContext(),1));

                Adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int codigo = data.get(RVPlato.getChildAdapterPosition(view)).getCodigoPla();
                        String nombre = data.get(RVPlato.getChildAdapterPosition(view)).getNomPlatillos();
                        String precioP = data.get(RVPlato.getChildAdapterPosition(view)).getPrecio();
                        String descripcion = data.get(RVPlato.getChildAdapterPosition(view)).getDescripcion();
                        String imagen = data.get(RVPlato.getChildAdapterPosition(view)).getImagenphppla();

                        Platillos pl = data.get(RVPlato.getChildAdapterPosition(view));
                        StringBuffer b = new StringBuffer();
                        b.append(""+pl.getCodigoPla());
                        codigoPlatillo = b.toString();

                        verificarTienda(bd.getUrldatos()+"WS_verificarduplicidadpedido.php?codigo="+codigoPlatillo+"");
                        ////////////////////

                        final AlertDialog.Builder builder = new AlertDialog.Builder(PlatillosFragment.this.getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.agregarcarrito,null);

                        cerrar = dialoglayout.findViewById(R.id.imvCerrarP);
                        cantidad = dialoglayout.findViewById(R.id.tvCantidad);
                        nomPlaP = dialoglayout.findViewById(R.id.edtNomPlaP);
                        descP = dialoglayout.findViewById(R.id.tvDespro);
                        precioPl = dialoglayout.findViewById(R.id.edtPrecioP);
                        aPlatillo = dialoglayout.findViewById(R.id.btnAgregar);
                        mas = dialoglayout.findViewById(R.id.btnmas);
                        menos = dialoglayout.findViewById(R.id.btnmenos);
                        imagenplatillo = dialoglayout.findViewById(R.id.imgplav);

                        veri = dialoglayout.findViewById(R.id.veri);

                        //String link = bd.getUrldatos()+"Imagenes/Platillos/"+codigo+".jpg";
                        Glide.with(PlatillosFragment.this)
                                .load(imagen)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .fitCenter()
                                .placeholder(R.drawable.ic_img)
                                .error(R.drawable.ic_small)
                                //.apply(RequestOptions.circleCropTransform())
                                .into(imagenplatillo);

                        nomPlaP.setText(nombre);
                        precioPl.setText("S/" + precioP);
                        descP.setText(descripcion);

                        mas.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                agregar(view);
                            }
                        });

                        menos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                restar(view);
                            }
                        });

                        builder.setView(dialoglayout);
                        final AlertDialog alertas = builder.create();
                        alertas.setCanceledOnTouchOutside(false);
                        alertas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        aPlatillo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (codigoUsuario.isEmpty()){
                                    alertas.dismiss();
                                    mensajedeiniciarsesion();
                                    //Toast.makeText(getContext(),"No hay sesion",Toast.LENGTH_SHORT).show();
                                    }else{
                                        //Toast.makeText(getContext(),"ya puedes comprar"+"\n"+"codigo usuario " + codigoUsuario ,Toast.LENGTH_SHORT).show();
                                            if (veri.getText().toString().equals(codigoUsuario)) {
                                                ventanaConfirmacion();
                                                alertas.dismiss();
                                            }else{
                                                //String numerotienda = "0";
                                                if (tiendaveri == null){
                                                    agregarCarrito(bd.getUrldatos()+"WS_agregarPedido.php");
                                                    alertas.dismiss();
                                                }else{
                                                    if (codigoTienda.equals(tiendaveri)){
                                                        agregarCarrito(bd.getUrldatos()+"WS_agregarPedido.php");
                                                        alertas.dismiss();
                                                    }else{
                                                        alertas.dismiss();
                                                        mensajedisculpa();
                                                    }
                                                }
                                            }
                                    }
                            }
                        });

                        cerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertas.dismiss();
                            }
                        });

                        alertas.show();
                        builder.setCancelable(false);
                    }
                });

            } catch (JSONException e) {
            }
        }
    }

    public void agregarCarrito(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "Se agrego al carrito", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idpla", codigoPlatillo);
                parametros.put("idusu", codigoUsuario);
                parametros.put("cantidad", cantidad.getText().toString());
                parametros.put("idtien", codigoTienda);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void agregar(View view){
        String valor = cantidad.getText().toString();
        int aux = Integer.parseInt(valor);
        cantidad.setText(""+(aux+1));
    }

    public void restar(View view){
        String valor = cantidad.getText().toString();
        int aux = Integer.parseInt(valor);
        if (aux == 1){
            cantidad.setText(""+1);
        }else {
            cantidad.setText("" + (aux - 1));
        }
    }

    public void verificarTienda(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        veri.setText(jsonObject.getString("Id_usuario_cliente"));

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
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void ventanaConfirmacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Producto ya existe en su carrito");
        builder.setMessage("¿Deseas ir a tu carrito para editarlo?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(PlatillosFragment.this.getContext(), CarritoActivity.class);
                startActivity(i);
                /*Fragment nuevoFragmento = new CarritoFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, nuevoFragmento);
                transaction.addToBackStack(null);
                ((FragmentTransaction) transaction).commit();*/
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void mensajedeiniciarsesion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Atencion");
        builder.setMessage("Debe iniciar sesion para poder comprar");
        builder.setCancelable(false);
        builder.setPositiveButton("Iniciar sesion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("cerrar",null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void mensajedisculpa(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Atención");
        builder.setMessage("Por el momento, solo se puede comprar una tienda a la vez");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void tiendaverificacion (String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        tiendaveri =  (jsonObject.getString("Id_tienda"));
                    } catch (JSONException e) {
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
}

