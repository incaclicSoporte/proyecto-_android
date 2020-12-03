package apps.incaclic.jamear;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import apps.incaclic.jamear.ClasesLogin.Preferences;
import apps.incaclic.jamear.Conexion.Conexionbd;

import com.example.jamear.BuildConfig;
import com.example.jamear.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //////////actualizacion
    int version;
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            version = packageInfo.versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }

        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build());
        HashMap<String, Object> actualizazion = new HashMap<>();
        actualizazion.put("versioncode", version);
        Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, aVoid ->{
            remoteConfig.activateFetched();
            version(version);
        });
    }

    private void version(int version) {
        int nueva = (int) remoteConfig.getLong("versioncode");
        String web = remoteConfig.getString("web");
        String versionname = remoteConfig.getString("versionname");
        if (nueva > version){
            ventana(web, versionname);
        }
    }

    ////////////////////////////

    RequestQueue requestQueue;
    ///menu
    ImageView fotoPerfilM, salirSesion;
    TextView nombreM, correoM, versionM;
    String idusuario;


    String emisor = "";

    /////////////////////iniciar sesion
    TextView iniciarsesion;

    Conexionbd bd = new Conexionbd();


    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_historial, R.id.nav_pedido)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //////////consultas

        View v = navigationView.getHeaderView(0);

        emisor = Preferences.obtenerEstadoString(this,Preferences.PREFERENCIA_USUARIO);

        nombreM = v.findViewById(R.id.tvnomMenu);
        correoM = v.findViewById(R.id.tvcorreoM);
        iniciarsesion = v.findViewById(R.id.tviniciarsesion);
        salirSesion = v.findViewById(R.id.imgcerrarS);
        fotoPerfilM = v.findViewById(R.id.imgPerfil);
        versionM = v.findViewById(R.id.tvVersion);

        versionM.setText("V "+ BuildConfig.VERSION_NAME);

        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        if (emisor.isEmpty()){
            //Toast.makeText(getApplicationContext(), "no hay datos",Toast.LENGTH_SHORT).show();
            nombreM.setText("");
            correoM.setText("");
            nombreM.setVisibility(View.GONE);
            correoM.setVisibility(View.GONE);
            salirSesion.setVisibility(View.GONE);
            iniciarsesion.setVisibility(View.VISIBLE);
        }else{
            //Toast.makeText(getApplicationContext(), "si hay datos",Toast.LENGTH_SHORT).show();
            nombreM.setVisibility(View.VISIBLE);
            correoM.setVisibility(View.VISIBLE);
            iniciarsesion.setVisibility(View.GONE);
            salirSesion.setVisibility(View.VISIBLE);
        }

        //final String pass = getIntent().getExtras().getString("pass");

            CargarDatosMenu(bd.getUrldatos()+"WS_datoCliente.php?usuario="+emisor+"");

        salirSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Preferences.estadocambiar(MainActivity.this,false, Preferences.PREFERENCES_ESTADO_BUTTON_SESION);
                    Preferences.borrarDato(MainActivity.this, false, Preferences.PREFERENCIA_USUARIO);
                    Intent i = new Intent(MainActivity.this , MainActivity.class);
                    nombreM.setText("");
                    correoM.setText("");
                    nombreM.setVisibility(View.GONE);
                    correoM.setVisibility(View.GONE);
                    iniciarsesion.setVisibility(View.VISIBLE);
                    startActivity(i);
                    finish();
                }
            });

        fotoPerfilM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emisor.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe iniciar sesion",Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), "si hay datos",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
                    i.putExtra("usu", emisor);
                    i.putExtra("codigousu", idusuario);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_superior, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
            case R.id.carritosup:
                //Toast.makeText(getApplicationContext(),"hola has presionado carrito", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, CarritoActivity.class);
                startActivity(i);
                return true;
           default:
               return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void CargarDatosMenu(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i=0; i<response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nombreM.setText(jsonObject.getString("Nombre"));
                        correoM.setText(jsonObject.getString("Correo"));
                        idusuario = (jsonObject.getString("Id_usuario_cliente"));

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

    //////////////ventana de actualizar
    private void ventana(final String utl, String versionname){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setIcon(R.drawable.iso);
        builder.setTitle("Actualización");
        //builder.setMessage("Descargalo para que no te pierdas las nuevas funciones"+"\n"+"version "+ versionname);
        builder.setMessage("Descargalo para que no te pierdas las nuevas funciones"+"\n"+"version "+ versionname);
        builder.setCancelable(false);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent abrirurl = new Intent(Intent.ACTION_VIEW, Uri.parse(utl));
                startActivity(abrirurl);
                dialog.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
