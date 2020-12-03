package apps.incaclic.jamear.Entidades;

public class Pedido {

    public int codigoPedido;
    public String hora;
    public String fecha;
    public String numpedido;
    public String estado;
    public String nombredelatienda;

    public Pedido() {
    }

    public Pedido(int codigoPedido, String hora, String fecha, String numpedido, String estado, String nombredelatienda) {
        this.codigoPedido = codigoPedido;
        this.hora = hora;
        this.fecha = fecha;
        this.numpedido = numpedido;
        this.estado = estado;
        this.nombredelatienda = nombredelatienda;
    }

    public int getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(int codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumpedido() {
        return numpedido;
    }

    public void setNumpedido(String numpedido) {
        this.numpedido = numpedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombredelatienda() {
        return nombredelatienda;
    }

    public void setNombredelatienda(String nombredelatienda) {
        this.nombredelatienda = nombredelatienda;
    }

    @Override
    public String toString(){
        return estado;
    }
}
