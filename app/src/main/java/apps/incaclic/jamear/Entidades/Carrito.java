package apps.incaclic.jamear.Entidades;

import java.io.Serializable;

public class Carrito implements Serializable {

    public int codigoC;
    public String nombrePoductoC;
    public String precioProductoC;
    public int idplatillo;
    public int codigotienda;
    public String nombreTiendaC;
    public String cantidadC;
    public String subtotal;
    public String descripcionC;
    public String imagenphpcar;

    public Carrito() {

    }

    public Carrito(int codigoC, String nombrePoductoC, String precioProductoC, int idplatillo, int codigotienda, String nombreTiendaC, String cantidadC, String subtotal, String descripcionC, String imagenphpcar) {
        this.codigoC = codigoC;
        this.nombrePoductoC = nombrePoductoC;
        this.precioProductoC = precioProductoC;
        this.idplatillo = idplatillo;
        this.codigotienda = codigotienda;
        this.nombreTiendaC = nombreTiendaC;
        this.cantidadC = cantidadC;
        this.subtotal = subtotal;
        this.descripcionC = descripcionC;
        this.imagenphpcar = imagenphpcar;
    }

    public int getCodigoC() {
        return codigoC;
    }

    public void setCodigoC(int codigoC) {
        this.codigoC = codigoC;
    }

    public String getNombrePoductoC() {
        return nombrePoductoC;
    }

    public void setNombrePoductoC(String nombrePoductoC) {
        this.nombrePoductoC = nombrePoductoC;
    }

    public String getPrecioProductoC() {
        return precioProductoC;
    }

    public void setPrecioProductoC(String precioProductoC) {
        this.precioProductoC = precioProductoC;
    }

    public int getIdplatillo() {
        return idplatillo;
    }

    public void setIdplatillo(int idplatillo) {
        this.idplatillo = idplatillo;
    }

    public int getCodigotienda() {
        return codigotienda;
    }

    public void setCodigotienda(int codigotienda) {
        this.codigotienda = codigotienda;
    }

    public String getNombreTiendaC() {
        return nombreTiendaC;
    }

    public void setNombreTiendaC(String nombreTiendaC) {
        this.nombreTiendaC = nombreTiendaC;
    }

    public String getCantidadC() {
        return cantidadC;
    }

    public void setCantidadC(String cantidadC) {
        this.cantidadC = cantidadC;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getDescripcionC() {
        return descripcionC;
    }

    public void setDescripcionC(String descripcionC) {
        this.descripcionC = descripcionC;
    }

    public String getImagenphpcar() {
        return imagenphpcar;
    }

    public void setImagenphpcar(String imagenphpcar) {
        this.imagenphpcar = imagenphpcar;
    }

    @Override
    public String toString(){
        return nombrePoductoC;
    }
}
