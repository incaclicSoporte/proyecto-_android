package apps.incaclic.jamear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import apps.incaclic.jamear.Adaptador.RVCarrito;
import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Carrito;
import apps.incaclic.jamear.Entidades.Tipopagos;

import com.example.jamear.R;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class CarritoActivity extends AppCompatActivity {

    private RVCarrito aCarrito;

    AsyncHttpClient cliente;
    ListView listaCompra;
    Button Comprar;
    TextView totalCompra;
    String emisor = "";
    String fecha, preciofinal, codigoUsuario, nombreusuario;
    String detallecompra = "";
    String nombretienda = "";
    String tipometodo;
    SwipeRefreshLayout refreshcarrito;

    ///////////////// ventana editar
    ImageView cerrar;
    TextView nomP, nomT, prceditar, cant, desc;
    Button btnedi, btnret, mascnt, menoscnt;
    StringBuffer codigoPro;
    String cantidad;
    StringBuffer codproEC;
    ImageView imgplaedi;
    int cntd;

    /////////// ventana pago
    TextInputEditText monto;
    Button guardar;

    ///////////////////boleta
    String cbody = "";
    String tipob;

    //////////////ventana metodopago
    ListView listapagos;
    String nomM, codM;
    ArrayList datos;
    int codigodelatienda;

    RequestQueue requestQueue;

    Conexionbd bd = new Conexionbd();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Carrito de compras");

        cliente = new AsyncHttpClient();

        listaCompra =  findViewById(R.id.lvCarrito);
        Comprar = findViewById(R.id.btnComprar);
        totalCompra =  findViewById(R.id.tvTotal);
        refreshcarrito = findViewById(R.id.srlcarrito);

        refreshcarrito.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mostrarProductos();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshcarrito.setRefreshing(false);
                    }
                },1000);
            }
        });

        emisor = Preferences.obtenerEstadoString(this,Preferences.PREFERENCIA_USUARIO);
        mostrarProductos();
        datousu(bd.getUrldatos()+"WS_datoCliente.php?usuario="+emisor+"");

        fechacompra();

        Comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validarPago()) return;
                metodoPedido();
                //Toast.makeText(getApplicationContext(),"codigo tienda "+codigodelatienda,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fechacompra(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        String fechas = dateFormat.format(date);
        fecha = fechas;
    }

    public void datousu (String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        codigoUsuario = (jsonObject.getString("Id_usuario_cliente"));
                        nombreusuario = (jsonObject.getString("Nombre"));
                    } catch (JSONException e) {
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(getApplication().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void mostrarProductos() {
        String url = bd.getUrldatos()+"WS_mostrarCarrito.php?usuario="+emisor+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarLista(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargarLista(String rpt){

        double acum = 0;

        final ArrayList<Carrito> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(rpt);

            cbody = cbody + "<html>";
            cbody = cbody + "<head>";
            cbody = cbody + "</head>";
            cbody = cbody + "<body>";
            cbody = cbody + "<table width='100%'>";
            cbody = cbody + "<tr style= 'background: #c0c0c0'>";
            cbody = cbody + "<th colspan='3'>Nombre Producto</th>";
            cbody = cbody + "<th colspan='3'>Tienda</th>";
            cbody = cbody + "<th>Cantidad</th>";
            cbody = cbody + "<th>Precio Uni.</th>";
            cbody = cbody + "<th>Subtotal</th>";
            cbody = cbody + "</tr>";

            for (int i = 0; i < jsonArray.length(); i++){
                cbody = cbody + "<tr>";
                JSONObject json_data = jsonArray.getJSONObject(i);
                Carrito c = new Carrito();
                c.setCodigoC(jsonArray.getJSONObject(i).getInt("Id_pedido"));
                c.nombrePoductoC = json_data.getString("Platillo");
                c.idplatillo = json_data.getInt("Id_platillo");

                codigodelatienda = (jsonArray.getJSONObject(i).getInt("Id_tienda"));

                c.nombreTiendaC = json_data.getString("nombre_tienda");
                c.precioProductoC = json_data.getString("Precio");
                c.setDescripcionC(jsonArray.getJSONObject(i).getString("Descripcion"));
                c.cantidadC = json_data.getString("Pe_Cantidad");
                c.imagenphpcar = json_data.getString("Imagen");

                final double pre = Double.parseDouble(c.precioProductoC);
                final int canti = Integer.parseInt(c.cantidadC);
                double pmonto = pre*canti;

                String nomPro = (c.nombrePoductoC);
                String nomTienda = (c.nombreTiendaC);
                acum = acum + ( pre * canti );
                detallecompra = detallecompra + nomPro +" "+"("+canti+")"+" - "+ nomTienda +"\n";
                nombretienda = nomTienda;

                cbody = cbody + "<td style="+"font-family: arial"+""+"font-size: 13px"+" colspan='3'>"+nomPro+"</td>";
                cbody = cbody + "<td style="+"font-family: arial"+""+"font-size: 13px"+""+"text-align: center"+" colspan='3'>"+nomTienda+"</td>";
                cbody = cbody + "<td style="+"font-family: arial"+""+"font-size: 13px"+""+"text-align: center"+">"+canti+"</td>";
                cbody = cbody + "<td style="+"font-family: arial"+""+"font-size: 13px"+""+"text-align: center"+">"+"S/ "+String.format("%.2f", pre)+"</td>";
                cbody = cbody + "<td style="+"font-family: arial"+""+"font-size: 13px"+""+"text-align: center"+">"+"S/ "+String.format("%.2f",pmonto)+"</td>";
                cbody = cbody + "</tr>";

                lista.add(c);

                listaCompra.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        ///////////////////////////////////////////
                        Carrito c = lista.get(position);
                        StringBuffer idpla = new StringBuffer();
                        StringBuffer np = new StringBuffer();
                        StringBuffer cnt = new StringBuffer();
                        StringBuffer prc = new StringBuffer();
                        StringBuffer tnd = new StringBuffer();
                        StringBuffer dsc = new StringBuffer();
                        StringBuffer img = new StringBuffer();

                        idpla.append(""+c.getIdplatillo());
                        np.append(""+c.getNombrePoductoC());
                        cnt.append(""+c.getCantidadC());
                        prc.append(""+c.getPrecioProductoC());
                        tnd.append(""+c.getNombreTiendaC());
                        dsc.append(""+c.getDescripcionC());
                        img.append(""+c.getImagenphpcar());

                        final AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.item_editarcarrito,null);

                        cerrar = dialoglayout.findViewById(R.id.imvCerrarEC);
                        cant = dialoglayout.findViewById(R.id.tvCantidadEC);
                        nomP = dialoglayout.findViewById(R.id.edtNomPlaEC);
                        nomT = dialoglayout.findViewById(R.id.tvTiendaEC);
                        desc = dialoglayout.findViewById(R.id.tvDesproEC);
                        prceditar = dialoglayout.findViewById(R.id.tvPrecioEC);
                        btnedi = dialoglayout.findViewById(R.id.btnEditarEC);
                        btnret = dialoglayout.findViewById(R.id.btnEliminarEC);
                        mascnt = dialoglayout.findViewById(R.id.btnmasEC);
                        menoscnt = dialoglayout.findViewById(R.id.btnmenosEC);
                        imgplaedi = dialoglayout.findViewById(R.id.imgplavedi);

                        String link = ""+img;
                        Glide.with(CarritoActivity.this)
                                .load(link)
                                //.skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .fitCenter()
                                .placeholder(R.drawable.ic_img)
                                .error(R.drawable.ic_small)
                                //.apply(RequestOptions.circleCropTransform())
                                .into(imgplaedi);

                        nomP.setText(np);
                        prceditar.setText("S/." + prc);
                        int ct = Integer.parseInt(cnt.toString());
                        cntd = ct;
                        cant.setText(cnt);
                        desc.setText(dsc);
                        nomT.setText("Tienda - "+tnd);

                        mascnt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                agregar(view);
                            }
                        });

                        menoscnt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                restar(view);
                            }
                        });

                        builder.setView(dialoglayout);
                        final AlertDialog alertas = builder.create();
                        alertas.setCanceledOnTouchOutside(false);
                        alertas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        btnedi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //Toast.makeText(getContext(), "operativo",Toast.LENGTH_SHORT).show();

                                Carrito c = lista.get(position);
                                StringBuffer codec = new StringBuffer();
                                codproEC = codec.append(c.getCodigoC());

                                editarProducto(bd.getUrldatos()+"WS_editarCarrito.php");
                                detallecompra = "";
                                cbody = "";
                                try {
                                    Thread.sleep(1000);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                mostrarProductos();
                                alertas.cancel();
                            }
                        });

                        btnret.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Carrito c = lista.get(position);
                                StringBuffer cod = new StringBuffer();
                                codigoPro = cod.append(c.getCodigoC());

                                if (lista.size()>1){
                                    retirarProducto(bd.getUrldatos()+"WS_retirardelCarrito.php");
                                    detallecompra = "";
                                    cbody = "";
                                    try {
                                        Thread.sleep(1000);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                    mostrarProductos();

                                }else if(lista.size()==1){
                                    retirarProducto(bd.getUrldatos()+"WS_retirardelCarrito.php");
                                    detallecompra = "";
                                    cbody = "";

                                    try {
                                        Thread.sleep(1000);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                    mostrarProductos();
                                    regresarInicio();
                                }
                                alertas.dismiss();
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
                        /////////////////////////////////////////
                    }
                });

                listaCompra = findViewById(R.id.lvCarrito);
                aCarrito = new RVCarrito(getApplicationContext(),lista);
                String T = "S/ "+String.format("%.2f", acum);
                preciofinal = (String.format("%.2f", acum));;
                totalCompra.setText(T);
                listaCompra.setAdapter(aCarrito);

            }

            cbody = cbody + "</table>";
            cbody = cbody + "<table width='100%'>";
            cbody = cbody +	"<tr style= 'background: #c0c0c0'>";
            cbody = cbody +	"<th colspan='8'>Total a pagar</th>";
            cbody = cbody +	"<th>"+"S/ "+String.format("%.2f", acum)+"</th>";
            cbody = cbody +	"</tr>";
            cbody = cbody + "</table>";
            cbody = cbody + "</body>";
            cbody = cbody + "</html>";

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void retirarProducto(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Producto retirado", Toast.LENGTH_SHORT).show();
                totalCompra.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("codpla", codigoPro.toString());

                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void editarProducto(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Pedido Actualizado", Toast.LENGTH_SHORT).show();
                totalCompra.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("codpe", codproEC.toString());
                parametros.put("cantidad", cantidad);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void agregar(View view){
        String valor = cant.getText().toString();
        int aux = Integer.parseInt(valor);
        cant.setText(""+(aux+1));
        cantidad = (""+(aux+1));

        int nc = Integer.parseInt(cantidad);
        if (nc > cntd){
            btnedi.setVisibility(View.VISIBLE);
        }else if (nc == cntd){
            btnedi.setVisibility(View.INVISIBLE);
        }
    }

    public void restar(View view){
        String valor = cant.getText().toString();
        int aux = Integer.parseInt(valor);
        if (aux == 1){
            cant.setText(""+1);
            cantidad = (""+1);
        }else {
            cant.setText("" + (aux - 1));
            cantidad = ("" + (aux - 1));
        }

        int nc = Integer.parseInt(cantidad);
        if (nc < cntd){
            btnedi.setVisibility(View.VISIBLE);
        }else if (nc == cntd){
            btnedi.setVisibility(View.INVISIBLE);
        }
    }

    public void agregarCompra(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Compra Realizada", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de Red", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("descripcion", detallecompra);
                parametros.put("prefinal", preciofinal);
                parametros.put("idusu", codigoUsuario);
                parametros.put("fecha", fecha);
                parametros.put("metodo", tipometodo);
                parametros.put("nomtienda", nombretienda);
                //parametros.put("monto", monto.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void limpiarCarrito(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getContext(), "Producto Retirado Correctamente", Toast.LENGTH_SHORT).show();
                totalCompra.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("codusu", codigoUsuario.trim());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //se cambio a activity de fragment transcation a intent
    public void regresarInicio(){
        Intent i = new Intent(CarritoActivity.this, MainActivity.class);
        startActivity(i);
        /*Fragment nuevoFragmento = new CategoriaFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, nuevoFragmento);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    public void metodoPedido(){
        final String delivery = "Delivery";
        final String tienda = "Recojer en tienda";

        final CharSequence[] items = { delivery, tienda };

        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        builder.setTitle("¿Como recibirá su pedido?");
        builder.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(delivery)){
                    //tipoPago();
                    mostrarventanatipopago();

                }else if (items[item].equals(tienda)){
                    String t = "4";
                    tipometodo = t;
                    String tt = "Ninguno - Recojo en tienda";
                    tipob = tt;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                }

            }
        }).show();
    }

    public void ventanaEfectivo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.item_efectivo,null);

        cerrar = dialoglayout.findViewById(R.id.imvCerrarPago);
        monto = dialoglayout.findViewById(R.id.tetMonto);
        guardar = dialoglayout.findViewById(R.id.btnEfectivo);

        builder.setView(dialoglayout);
        final AlertDialog alertas = builder.create();
        alertas.setCanceledOnTouchOutside(false);
        alertas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validarMonto())return;

                double mn = Double.parseDouble(monto.getText().toString());
                double pf = Double.parseDouble(preciofinal);

                if (mn > 0){
                    if (mn >= pf){
                        if (mn <= 250.00){
                            String m = "3";
                            tipometodo = m;
                            String tef = "Efectivo - "+ "S/ "+ monto.getText().toString();
                            tipob = tef;
                            agregarCompraEfectivo(bd.getUrldatos()+"WS_agregarCompra2.php");
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                                mostrarProductos();
                                //regresarInicio();
                            }
                            limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                            factura(bd.getUrldatos()+"WS_boleta.php");
                            MensajeFinalEfectivo();

                            cerrarTeclado();
                            alertas.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "monto superior a S/250", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "El monto ingresado es menor a la compra", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //Toast.makeText(getContext(), "monto menor a S/2.00", Toast.LENGTH_SHORT).show();
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

    public void agregarCompraEfectivo(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Compra realizada", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("descripcion", detallecompra);
                parametros.put("prefinal", preciofinal);
                parametros.put("idusu", codigoUsuario);
                parametros.put("fecha", fecha);
                parametros.put("metodo", tipometodo);
                parametros.put("nomtienda", nombretienda);
                parametros.put("monto",monto.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private boolean validarMonto() {

        boolean valid = true;

        String Pmonto = monto.getText().toString();

        if (Pmonto.trim().isEmpty()) {
            monto.setError("Introduzca el monto");
            valid = false;
        } else {
            monto.setError(null);
        }

        return valid;
    }

    public  void MensajeFinalEfectivo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        builder.setTitle("Felicidades "+ nombreusuario + "\n"+"has finalizado tu compra");
        builder.setMessage("Se le envío un correo con el detalle del pedido y podrá ver el estado del pedido en el menú estado pedido");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regresarInicio();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public  void MensajeFinalTarjeta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        builder.setTitle("Felicidades "+ nombreusuario + "\n"+"has finalizado tu compra");
        builder.setMessage("Se le envío un correo con el detalle del pedido y podrá ver el estado del pedido en el menú estado pedido");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regresarInicio();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public  void MensajeFinalRecojo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        builder.setTitle("Felicidades "+ nombreusuario + "\n"+"has finalizado tu compra");
        builder.setMessage("Se le envío un correo con el detalle del pedido y podrá ver el estado del pedido en el menú estado pedido");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regresarInicio();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validarPago(){

        boolean valid = true;

        String Vcompra = totalCompra.getText().toString();

        if (Vcompra.isEmpty()) {
            //totalCompra.setError("Introduzca su Codigo");
            valid = false;
        } else {
            totalCompra.setError(null);
        }

        return valid;

    }

    public void cerrarTeclado(){
        View v = getCurrentFocus();
        if (v != null){
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

    public void factura(String URL){
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();

                parametros.put("idusuario", codigoUsuario.trim());
                parametros.put("descripcionboleta", cbody.trim());
                parametros.put("metodopago", tipob.trim());
                parametros.put("cod_tienda", ""+codigodelatienda);
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    /////////metodo de pago dinamico

    public void mostrartipopago(){
        String url = bd.getUrldatos()+"WS_tipodepagos.php?idtienda="+codigodelatienda+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargartipopago(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cargartipopago(String respuesta){
        final ArrayList<Tipopagos> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                Tipopagos tp = new Tipopagos();

                tp.setCodigometodo(jsonArray.getJSONObject(i).getInt("Id_metodo"));
                tp.setMetodo(jsonArray.getJSONObject(i).getString("metodo"));

                lista.add(tp);
                datos = lista;

            }
            ArrayAdapter<Tipopagos> a = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, lista);
            listapagos.setAdapter(a);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mostrarventanatipopago(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("¿Cual es su método de pago?");
        builder.setPositiveButton("salir", null);
        View dialoglayout = inflater.inflate(R.layout.item_metodopago,null);

        listapagos = dialoglayout.findViewById(R.id.lvpagos);

        builder.setView(dialoglayout);
        final AlertDialog alertas = builder.create();
        alertas.setCanceledOnTouchOutside(false);

        listapagos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tipopagos menu = (Tipopagos) datos.get(position);
                StringBuffer sb = new StringBuffer();
                StringBuffer sbc = new StringBuffer();
                sb.append(""+menu.getMetodo());
                sbc.append(""+menu.getCodigometodo());
                nomM = sb.toString();
                codM = sbc.toString();
                if (nomM.equals("MasterCard")){
                    String mc = codM;
                    tipometodo = mc;
                    String tmc = nomM;
                    tipob = tmc;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalTarjeta();
                    //Toast.makeText(getApplicationContext(), nomM + " mc" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();

                }else if(nomM.equals("Visa")){
                    String vs = codM;
                    tipometodo = vs;
                    String tvs = nomM;
                    tipob = tvs;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalTarjeta();
                    //Toast.makeText(getApplicationContext(), nomM + " vs" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if(nomM.equals("Efectivo")){
                    ventanaEfectivo();
                    //Toast.makeText(getActivity(), nomM + " efe" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if (nomM.equals("Yape")){
                    String yp = codM;
                    tipometodo = yp;
                    String typ = nomM;
                    tipob = typ;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " efe" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if (nomM.equals("Tunki")){
                    String tk = codM;
                    tipometodo = tk;
                    String ttk = nomM;
                    tipob = ttk;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " efe" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if(nomM.equals("Lukita - Plin")){
                    String lk = codM;
                    tipometodo = lk;
                    String tlk = nomM;
                    tipob = tlk;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " lukita" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if(nomM.equals("Interbank - Plin")){
                    String ipl = codM;
                    tipometodo = ipl;
                    String tipl = nomM;
                    tipob = tipl;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " Plin" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if (nomM.equals("Scotiabank - Plin")){
                    String spl = codM;
                    tipometodo = spl;
                    String tspl = nomM;
                    tipob = tspl;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " Plin" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }else if(nomM.equals("Plin")){
                    String pl = codM;
                    tipometodo = pl;
                    String tpl = nomM;
                    tipob = tpl;

                    agregarCompra(bd.getUrldatos()+"WS_agregarCompra.php");
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                        mostrarProductos();
                        //regresarInicio();
                    }
                    limpiarCarrito(bd.getUrldatos()+"WS_limpiarCarrito.php");
                    factura(bd.getUrldatos()+"WS_boleta.php");
                    MensajeFinalRecojo();
                    //Toast.makeText(getApplicationContext(), nomM + " Plin" + "\n" + "codigo " + codM,Toast.LENGTH_SHORT).show();
                    alertas.dismiss();
                }
            }
        });

        mostrartipopago();

        alertas.show();
        builder.setCancelable(false);
    }

    public void ventana(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Titulo");
        builder.setMessage("idusuario: "+codigoUsuario+"\n"+
                            "metodo: "+tipob+"\n"+
                            "cod tienda: "+codigodelatienda+"\n"+
                            "descripcion: "+cbody);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}