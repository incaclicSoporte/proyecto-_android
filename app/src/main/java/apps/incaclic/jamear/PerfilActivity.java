package apps.incaclic.jamear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import apps.incaclic.jamear.Conexion.Conexionbd;

import com.example.jamear.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    //////activity Perfil
    Toolbar toolbar;
    ImageView fotoPerfil;
    ImageButton editarP, cambiarPassP;
    FloatingActionButton fotocambiar;
    TextView nomP, apP, amP, tlfP, dirP, dniP, usuP, corE;

    ////ventana editar
    ImageView cerrarE;
    TextInputEditText nomE, apE, amE, tlfE, dirE, dniE, correoE;
    Button confirmarE;

    Conexionbd bd = new Conexionbd();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbar = findViewById(R.id.toolbar);
        fotoPerfil = findViewById(R.id.imgPerfilP);
        editarP = findViewById(R.id.imbEditarP);

        nomP = findViewById(R.id.tvNomP);
        apP = findViewById(R.id.tvApP);
        amP = findViewById(R.id.tvAmP);
        tlfP = findViewById(R.id.tvTlfP);
        dirP = findViewById(R.id.tvDireP);
        dniP = findViewById(R.id.tvDniP);
        corE = findViewById(R.id.tvCorreoP);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#5816B7"));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Perfil");



        editarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String idusuario = getIntent().getExtras().getString("codigousu");
                cargarAlerDialog(bd.getUrldatos()+"WS_datoclientePerfil.php?usuario="+idusuario+"");
                VentanaEditar();
            }
        });

        final String idusuario = getIntent().getExtras().getString("codigousu");
        CargarDatosPerfil(bd.getUrldatos()+"WS_datoclientePerfil.php?usuario="+idusuario+"");

    }

    ////cargar datos perfil
    public void CargarDatosPerfil(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nomP.setText(jsonObject.getString("Nombre"));
                        apP.setText(jsonObject.getString("Apellido_paterno"));
                        amP.setText(jsonObject.getString("Apellido_materno"));
                        dirP.setText(jsonObject.getString("Direccion"));
                        dniP.setText(jsonObject.getString("DNI"));
                        tlfP.setText(jsonObject.getString("Telefono"));
                        corE.setText(jsonObject.getString("Correo"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue= Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    ///mostrar ventana de Perfil a editar y clases
    public void VentanaEditar(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dl = inflater.inflate(R.layout.ven_editarperfil,null);

        cerrarE =  dl.findViewById(R.id.imvCerrarEd);
        nomE = dl.findViewById(R.id.tetNomE);
        apE = dl.findViewById(R.id.tetApPE);
        amE = dl.findViewById(R.id.tetApME);
        tlfE = dl.findViewById(R.id.tetTlfE);
        dirE = dl.findViewById(R.id.tetDirE);
        dniE = dl.findViewById(R.id.tetDniE);
        confirmarE = dl.findViewById(R.id.btnEditarE);
        correoE = dl.findViewById(R.id.tetCorE);

        builder.setView(dl);
        final AlertDialog alertas = builder.create();
        alertas.setCanceledOnTouchOutside(false);
        alertas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (!validarPerfil()) return;
                    Editar(bd.getUrldatos()+"WS_editarPerfil.php");
                    alertas.dismiss();
            }
        });

        cerrarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertas.dismiss();
            }
        });
        alertas.show();
        builder.setCancelable(false);
    }

    public void cargarAlerDialog(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nomE.setText(jsonObject.getString("Nombre"));
                        apE.setText(jsonObject.getString("Apellido_paterno"));
                        amE.setText(jsonObject.getString("Apellido_materno"));
                        dirE.setText(jsonObject.getString("Direccion"));
                        dniE.setText(jsonObject.getString("DNI"));
                        tlfE.setText(jsonObject.getString("Telefono"));
                        correoE.setText(jsonObject.getString("Correo"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue= Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void Editar(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Editado Correctamente", Toast.LENGTH_SHORT).show();
                nomP.setText("");
                apP.setText("");
                amP.setText("");
                dirP.setText("");
                dniP.setText("");
                tlfP.setText("");
                corE.setText("");
                final String idusuario = getIntent().getExtras().getString("codigousu");
                CargarDatosPerfil(bd.getUrldatos()+"WS_datoclientePerfil.php?usuario="+idusuario+"");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();

                final String idusuario = getIntent().getExtras().getString("codigousu");
                parametros.put("nombre", nomE.getText().toString().trim());
                parametros.put("apellido_P", apE.getText().toString().trim());
                parametros.put("apellido_M", amE.getText().toString().trim());
                parametros.put("dni", dniE.getText().toString().trim());
                parametros.put("direccion", dirE.getText().toString().trim());
                parametros.put("telefono", tlfE.getText().toString().trim());
                parametros.put("correo", correoE.getText().toString().trim());
                parametros.put("codigo", idusuario);

                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean validarPerfil(){

        boolean valido = true;

        String Enom = nomE.getText().toString();
        String Eapp = apE.getText().toString();
        String Eapm = amE.getText().toString();
        String Edni = dniE.getText().toString();
        String Edire = dirE.getText().toString();
        String Etele = tlfE.getText().toString();
        String EcorrE = correoE.getText().toString();

        if (Enom.trim().isEmpty()) {
            nomE.setError("Introduzca su nombre");
            valido = false;
        } else {
            nomE.setError(null);
        }

        if (Eapp.trim().isEmpty()) {
            apE.setError("Introduzca su apellido paterno");
            valido = false;
        } else {
            apE.setError(null);
        }

        if (Eapm.trim().isEmpty()) {
            amE.setError("Introduzca su apellido materno");
            valido = false;
        } else {
            amE.setError(null);
        }

        if (Edni.trim().isEmpty() || Edni.trim().length() <8) {
            dniE.setError("Introduzca su DNI");
            valido = false;
        } else {
            dniE.setError(null);
        }

        if (Etele.trim().isEmpty() || Etele.trim().length() <9) {
            tlfE.setError("Introduzca su celular");
            valido = false;
        } else {
            tlfE.setError(null);
        }

        if (Edire.trim().isEmpty()) {
            dirE.setError("Introduzca su dirección");
            valido = false;
        } else {
            dirE.setError(null);
        }

        if (EcorrE.trim().isEmpty()) {
            correoE.setError("Introduzca su correo electrónico");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(EcorrE).matches()) {
            correoE.setError("Introduzca un correo valido");
            valido = false;
        }else {
            correoE.setError(null);
        }

        return valido;
    }

}
