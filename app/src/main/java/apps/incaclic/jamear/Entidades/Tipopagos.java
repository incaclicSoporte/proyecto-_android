package apps.incaclic.jamear.Entidades;

public class Tipopagos {

    public int codigometodo;
    public String metodo;

    public Tipopagos() {
    }

    public Tipopagos(int codigometodo, String metodo) {
        this.codigometodo = codigometodo;
        this.metodo = metodo;
    }

    public int getCodigometodo() {
        return codigometodo;
    }

    public void setCodigometodo(int codigometodo) {
        this.codigometodo = codigometodo;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    @Override
    public String toString() {
        return metodo;
    }

}
