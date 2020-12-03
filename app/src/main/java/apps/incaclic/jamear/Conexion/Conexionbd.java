package apps.incaclic.jamear.Conexion;

public class Conexionbd {

    public String urldatos = "http://161.132.98.19/WS_Jamear/";

    public Conexionbd() {
    }

    public Conexionbd(String urldatos) {
        this.urldatos = urldatos;
    }

    public String getUrldatos() {
        return urldatos;
    }

    public void setUrldatos(String urldatos) {
        this.urldatos = urldatos;
    }
}
