package apps.incaclic.jamear;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;

import com.example.jamear.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Dialog epicDialog;
    RequestQueue requestQueue;
    ProgressDialog cargar;

    /////login
    Button sesion;
    TextInputEditText usuarioL, claveL;
    TextView ventanacrear, ventanarecuperar;
    RadioButton nocerrarsesion;

    boolean activadoRB;

    /////AlertDialog Crear
    ImageView cerrarC;
    TextInputEditText usuarioC, claveC, nombreC, dniC, apC, amC, direC, teleC, cRC;
    Button crearC;
    String validarusu;

    ////AlertDialog Recuperar
    ImageView cerrarR;
    TextInputEditText usuarioR, correoR;
    Button recuperarR;
    //TextView verfiV;

    /////Verificador
    ImageView cerrarV;
    TextInputEditText codigoV;
    Button cambiarconV;
    CheckBox radioV;
    TextInputEditText nvcontCP, rncontCP;

    Conexionbd bd = new Conexionbd();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        epicDialog = new Dialog(this);

        ///// mantener abierto app despues de guardar sesion con bd
        if (Preferences.obtenerEstado(this, Preferences.PREFERENCES_ESTADO_BUTTON_SESION)){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }


        sesion = (Button) findViewById(R.id.btnSesion);
        ventanacrear = (TextView) findViewById(R.id.tvCrear);
        usuarioL = (TextInputEditText) findViewById(R.id.tetUsuario);
        claveL = (TextInputEditText) findViewById(R.id.tetPass);
        nocerrarsesion = (RadioButton) findViewById(R.id.rbRecordar);
        ventanarecuperar = findViewById(R.id.tvRecuperar);

        activadoRB = nocerrarsesion.isChecked();


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar sesíon");
        //getSupportActionBar().setTitle(Html.fromHtml("<font color='#5816B7'>Iniciar sesión</font>"));



        sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarsesion(bd.getUrldatos()+"WS_login.php");
            }
        });

        nocerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activadoRB){
                    nocerrarsesion.setChecked(false);
                }
                activadoRB = nocerrarsesion.isChecked();
            }
        });

        ventanacrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ventana_Crear();
            }
        });

        ventanarecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ventana_Recuperar();
            }
        });

    }

    ////// Iniciar Sesion clase
    public void iniciarsesion(String URL){

        if (!validarLogin()) return;
        cargar = new ProgressDialog(this);
        cargar.setMessage("Iniciando Sesión...");
        cargar.setCancelable(false);
        cargar.show();

        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                cargar.cancel();
                if(response.contains("1")){

                    Preferences.estadocambiar(LoginActivity.this, nocerrarsesion.isChecked(),Preferences.PREFERENCES_ESTADO_BUTTON_SESION);
                    Preferences.estadocambiarString(LoginActivity.this, usuarioL.getText().toString(), Preferences.PREFERENCIA_USUARIO);

                    Intent miIntent = new Intent(getApplicationContext(), MainActivity.class);
                    MainActivity ma = new MainActivity();
                    miIntent.putExtra("usuL", usuarioL.getText().toString().trim());
                    miIntent.putExtra("pass", claveL.getText().toString().trim());
                    startActivity(miIntent);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),"Error en ingresar DNI o contraseña", Toast.LENGTH_SHORT).show();
                    usuarioL.setText("");
                    claveL.setText("");
                    usuarioL.requestFocus();
                }
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
                parametros.put("usuario", usuarioL.getText().toString().trim());
                parametros.put("clave", claveL.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private boolean validarLogin(){
        boolean valid = true;

        String Lusu = usuarioL.getText().toString();
        String Lpass = claveL.getText().toString();

        if (Lusu.trim().isEmpty()) {
            usuarioL.setError("Introduzca su DNI");
            valid = false;
        } else {
            usuarioL.setError(null);
        }

        if (Lpass.trim().isEmpty()) {
            claveL.setError("Introduzca su contraseña");
            valid = false;
        } else {

            claveL.setError(null);
        }

        return valid;
    }

    ////validar existencia usuario
    public void validarUsu(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        //nombreC.setText(jsonObject.getString("Usuario_cliente"));
                        validarusu = (jsonObject.getString("DNI"));

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

    ////// AlertDialog Crear usuario y clase para crear usuario
    public void Ventana_Crear(){
        final LayoutInflater inflater = getLayoutInflater();
        epicDialog.setContentView(R.layout.ven_crear);

        cerrarC = (ImageView) epicDialog.findViewById(R.id.imvCerrarC);
        crearC = (Button) epicDialog.findViewById(R.id.btnCrearC);

        nombreC = epicDialog.findViewById(R.id.tetNombreC);
        dniC = epicDialog.findViewById(R.id.tetDNIC);
        apC =  epicDialog.findViewById(R.id.tetAppC);
        amC =  epicDialog.findViewById(R.id.tetApmC);
        direC = epicDialog.findViewById(R.id.tetDireC);
        teleC = epicDialog.findViewById(R.id.tetTelC);
        claveC = epicDialog.findViewById(R.id.tetClaveC);
        cRC = epicDialog.findViewById(R.id.tetcorreoRC);

        dniC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usucomparar = dniC.getText().toString().trim();
                validarUsu(bd.getUrldatos()+"WS_validarExistencia.php?usuario="+usucomparar+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crearC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String datousu = validarusu;

                if (dniC.getText().toString().trim().equals(datousu)){
                    Toast.makeText(getApplicationContext(), "El usuario ya existe",Toast.LENGTH_SHORT).show();
                    dniC.setText("");
                }else{
                    if (!validarCrearusu()) return;
                    CrearUsuario(bd.getUrldatos()+"WS_agregarusuario.php");
                    epicDialog.dismiss();
                }

            }
        });

        cerrarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epicDialog.dismiss();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        epicDialog.setCancelable(false);
        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }

    public void CrearUsuario(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Cuenta Creada Satisfactoriamente", Toast.LENGTH_SHORT).show();
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
                parametros.put("nombre", nombreC.getText().toString().trim());
                parametros.put("ap_paterno", apC.getText().toString().trim());
                parametros.put("ap_materno", amC.getText().toString().trim());
                parametros.put("telefono", teleC.getText().toString().trim());
                parametros.put("DNI", dniC.getText().toString().trim());
                parametros.put("correo", cRC.getText().toString().trim());
                parametros.put("pass", claveC.getText().toString().trim());
                parametros.put("direccion", direC.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private boolean validarCrearusu(){

        boolean valid = true;

        String Cap = apC.getText().toString();
        String Cam = amC.getText().toString();
        String Cnom = nombreC.getText().toString();
        String Ctel = teleC.getText().toString();
        String Cdni = dniC.getText().toString();
        String Cpass = claveC.getText().toString();
        String Cco = cRC.getText().toString();

        if (Cnom.trim().isEmpty()) {
            nombreC.setError("Introduzca su nombre");
            valid = false;
        } else {
            nombreC.setError(null);
        }

        if (Cap.trim().isEmpty()) {
            apC.setError("Introduzca su apellido");
            valid = false;
        } else {
            apC.setError(null);
        }

        if (Cam.trim().isEmpty()) {
            amC.setError("Introduzca su apellido");
            valid = false;
        } else {
            amC.setError(null);
        }

        if (Cpass.trim().isEmpty()) {
            claveC.setError("Introduzca su contraseña");
            valid = false;
        } else {
            claveC.setError(null);
        }

        if (Cdni.trim().isEmpty() || Cdni.trim().length()<8) {
            dniC.setError("Introduzca su DNI");
            valid = false;
        } else {
            dniC.setError(null);
        }

        if (Ctel.trim().isEmpty() || Ctel.trim().length()<9) {
            teleC.setError("Introduzca su celular");
            valid = false;
        } else {
            teleC.setError(null);
        }

        if (Cco.trim().isEmpty()){
            cRC.setError("Introduzca un correo electrónico");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Cco).matches()) {
            cRC.setError("Introduzca un correo valido");
            valid = false;
        } else {
            cRC.setError(null);
        }

        return valid;

    }

    ////////// Ventana recuperacion
    public void Ventana_Recuperar(){
        final LayoutInflater inflater = getLayoutInflater();
        epicDialog.setContentView(R.layout.ven_recuperar);

        cerrarR = epicDialog.findViewById(R.id.imvCerrarR);
        recuperarR = epicDialog.findViewById(R.id.btnRecuperarC);
        usuarioR = epicDialog.findViewById(R.id.tetUsuRecuperar);

        recuperarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validarRecuusu()) return;
                RecuperarUsuario(bd.getUrldatos()+"WS_recuperarPass.php");
                Ventana_Verificador();
            }
        });

        cerrarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epicDialog.dismiss();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        epicDialog.setCancelable(false);
        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }

    public void RecuperarUsuario(String URL){

        if (!validarRecuusu()) return;

        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.contains("1")){
                    Toast.makeText(getApplicationContext(),"se envio un codigo de verificacion a su correo", Toast.LENGTH_SHORT).show();


                }else{
                    Toast.makeText(getApplicationContext(),"Error en Ingreso de datos", Toast.LENGTH_SHORT).show();
                    usuarioR.setText("");
                    usuarioR.requestFocus();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuarioR.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private boolean validarRecuusu() {

        boolean valid = true;

        String Rusu = usuarioR.getText().toString();

        if (Rusu.trim().isEmpty()) {
            usuarioR.setError("Introduzca su DNI");
            valid = false;
        } else {
            usuarioR.setError(null);
        }

        return valid;
    }

    ///////Verificador y clases
    public void Ventana_Verificador(){
        final LayoutInflater inflater = getLayoutInflater();
        epicDialog.setContentView(R.layout.ven_verificador);

        cerrarV = epicDialog.findViewById(R.id.imvCerrarV);
        cambiarconV = epicDialog.findViewById(R.id.btnRestablecerV);
        codigoV = epicDialog.findViewById(R.id.tetCodVerfiV);
        radioV = epicDialog.findViewById(R.id.rbVerificar);

        nvcontCP = epicDialog.findViewById(R.id.tetNcCP);
        rncontCP = epicDialog.findViewById(R.id.tetRncCP);

        radioV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioV.isChecked()) {
                    codigoV.setEnabled(false);
                    nvcontCP.setEnabled(true);
                    rncontCP.setEnabled(true);
                    cambiarconV.setEnabled(true);
                    ingresarCodigo(bd.getUrldatos()+"WS_ingresarcodigoVerficador.php");
                } else {
                    codigoV.setEnabled(true);
                    nvcontCP.setEnabled(false);
                    rncontCP.setEnabled(false);
                    cambiarconV.setEnabled(false);
                }
            }
        });

        cambiarconV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validarPass()) return;
                if (nvcontCP.getText().toString().equals(rncontCP.getText().toString())) {
                    cambiarPass(bd.getUrldatos()+"WS_actualizarPass.php");
                    epicDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(),"Su nueva contraseña no es igual",Toast.LENGTH_SHORT).show();
                    limpiarN();
                }
            }
        });

        cerrarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epicDialog.dismiss();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        epicDialog.setCancelable(false);
        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();

    }

    public void ingresarCodigo(String URL){
        if (!validarVeri()) return;
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.contains("1")) {
                    Toast.makeText(getApplicationContext(), "Cambio Autorizado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Error en ingreso su codigo verficador", Toast.LENGTH_SHORT).show();
                    codigoV.setText("");
                    nvcontCP.setText("");
                    rncontCP.setText("");
                    codigoV.setEnabled(true);
                    radioV.setChecked(false);
                    nvcontCP.setEnabled(false);
                    rncontCP.setEnabled(false);
                    cambiarconV.setEnabled(false);
                    codigoV.requestFocus();
                }
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
                parametros.put("codigoVeri", codigoV.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private boolean validarVeri(){

        boolean valid = true;

        String Vcod = codigoV.getText().toString();

        if (Vcod.trim().isEmpty()) {
            codigoV.setError("Introduzca su codigo");
            valid = false;
        } else {
            codigoV.setError(null);
        }

        return valid;

    }

    ///cambiar contraseña ventana y clase

    public void cambiarPass(String URL){
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("1")) {
                    usuarioR.setText("");
                    Toast.makeText(getApplicationContext(), "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("clave", rncontCP.getText().toString().trim());
                parametros.put("verificador", codigoV.getText().toString().trim());
                return parametros;
            }
        };
        requestQueue=Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void limpiarN(){
        rncontCP.setText("");
        rncontCP.requestFocus();
    }

    private boolean validarPass() {

        boolean valid = true;

        String contraN = nvcontCP.getText().toString();
        String contraRN = rncontCP.getText().toString();
        String Vcod = codigoV.getText().toString();

        if (contraN.trim().isEmpty()) {
            nvcontCP.setError("Ingrese su contraseña");
            valid = false;
        } else {
            nvcontCP.setError(null);
        }

        if (contraRN.trim().isEmpty()) {
            rncontCP.setError("Ingrese su contraseña");
            valid = false;
        } else {
            rncontCP.setError(null);
        }

        if (Vcod.trim().isEmpty()) {
            codigoV.setError("Introduzca su codigo");
            valid = false;
        } else {
            codigoV.setError(null);
        }

        return valid;
    }


}
