package apps.incaclic.jamear.Entidades;

import java.io.Serializable;

public class Cateprincipal implements Serializable {

    public int codigopri;
    public String nomcatepri;
    public String imagenphpcatepri;

    public Cateprincipal() {
    }

    public Cateprincipal(int codigopri, String nomcatepri, String imagenphpcatepri) {
        this.codigopri = codigopri;
        this.nomcatepri = nomcatepri;
        this.imagenphpcatepri = imagenphpcatepri;
    }

    public int getCodigopri() {
        return codigopri;
    }

    public void setCodigopri(int codigopri) {
        this.codigopri = codigopri;
    }

    public String getNomcatepri() {
        return nomcatepri;
    }

    public void setNomcatepri(String nomcatepri) {
        this.nomcatepri = nomcatepri;
    }

    public String getImagenphpcatepri() {
        return imagenphpcatepri;
    }

    public void setImagenphpcatepri(String imagenphpcatepri) {
        this.imagenphpcatepri = imagenphpcatepri;
    }
}
