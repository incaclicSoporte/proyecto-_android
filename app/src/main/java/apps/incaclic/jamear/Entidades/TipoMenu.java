package apps.incaclic.jamear.Entidades;

public class TipoMenu {

    public int codigoMenu;
    public String nombreMenu;

    public TipoMenu() {
    }

    public TipoMenu(int codigoMenu, String nombreMenu) {
        this.codigoMenu = codigoMenu;
        this.nombreMenu = nombreMenu;
    }

    public int getCodigoMenu() {
        return codigoMenu;
    }

    public void setCodigoMenu(int codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public String getNombreMenu() {
        return nombreMenu;
    }

    public void setNombreMenu(String nombreMenu) {
        this.nombreMenu = nombreMenu;
    }

    @Override
    public String toString() {
        return nombreMenu;
    }
}
