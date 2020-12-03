package apps.incaclic.jamear.ClasesLogin;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String STRING_PREFERENCES = "usuario";
    public static final String PREFERENCES_ESTADO_BUTTON_SESION = "sesion";
    public static final String PREFERENCIA_USUARIO = "correo@mail";

    ///no cerrar sesion
    public static boolean obtenerEstado(Context c, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key,false);
    }

    public static String obtenerEstadoString(Context c, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getString(key,"");
    }

    ////si cerrar sesion
    public static void estadocambiar(Context c, boolean b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, b).apply();
    }

    public static void estadocambiarString(Context c, String b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putString(key, b).apply();
    }

    ////borrra shared preferences
    public static void borrarDato(Context c, boolean b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, b).apply();
        preferences.edit().clear().apply();
        preferences.edit().commit();
    }

}
