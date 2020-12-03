package apps.incaclic.jamear.Entidades;

public class Tiendas {

    public int codigoTienda;
    public String direcionTc;
    public String telefonoTc;
    public String propietariosTc;
    public String RazonSocialTc;
    public String RUCTc;
    public String imagenphptienda;

    public Tiendas() {

    }

    public Tiendas(int codigoTienda, String direcionTc, String telefonoTc, String propietariosTc, String razonSocialTc, String RUCTc, String imagenphptienda) {
        this.codigoTienda = codigoTienda;
        this.direcionTc = direcionTc;
        this.telefonoTc = telefonoTc;
        this.propietariosTc = propietariosTc;
        RazonSocialTc = razonSocialTc;
        this.RUCTc = RUCTc;
        this.imagenphptienda = imagenphptienda;
    }

    public int getCodigoTienda() {
        return codigoTienda;
    }

    public void setCodigoTienda(int codigoTienda) {
        this.codigoTienda = codigoTienda;
    }

    public String getDirecionTc() {
        return direcionTc;
    }

    public void setDirecionTc(String direcionTc) {
        this.direcionTc = direcionTc;
    }

    public String getTelefonoTc() {
        return telefonoTc;
    }

    public void setTelefonoTc(String telefonoTc) {
        this.telefonoTc = telefonoTc;
    }

    public String getPropietariosTc() {
        return propietariosTc;
    }

    public void setPropietariosTc(String propietariosTc) {
        this.propietariosTc = propietariosTc;
    }

    public String getRazonSocialTc() {
        return RazonSocialTc;
    }

    public void setRazonSocialTc(String razonSocialTc) {
        RazonSocialTc = razonSocialTc;
    }

    public String getRUCTc() {
        return RUCTc;
    }

    public void setRUCTc(String RUCTc) {
        this.RUCTc = RUCTc;
    }

    public String getImagenphptienda() {
        return imagenphptienda;
    }

    public void setImagenphptienda(String imagenphptienda) {
        this.imagenphptienda = imagenphptienda;
    }
}
