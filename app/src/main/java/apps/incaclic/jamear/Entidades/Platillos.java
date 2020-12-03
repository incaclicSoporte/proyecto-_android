package apps.incaclic.jamear.Entidades;

import java.io.Serializable;

public class Platillos implements Serializable {

    public int codigoPla;
    public String nomPlatillos;
    public String precio;
    public String stock;
    public String Descripcion;
    public String imagenphppla;

    public Platillos() {
    }

    public Platillos(int codigoPla, String nomPlatillos, String precio, String stock, String descripcion, String imagenphppla) {
        this.codigoPla = codigoPla;
        this.nomPlatillos = nomPlatillos;
        this.precio = precio;
        this.stock = stock;
        Descripcion = descripcion;
        this.imagenphppla = imagenphppla;
    }

    public int getCodigoPla() {
        return codigoPla;
    }

    public void setCodigoPla(int codigoPla) {
        this.codigoPla = codigoPla;
    }

    public String getNomPlatillos() {
        return nomPlatillos;
    }

    public void setNomPlatillos(String nomPlatillos) {
        this.nomPlatillos = nomPlatillos;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getImagenphppla() {
        return imagenphppla;
    }

    public void setImagenphppla(String imagenphppla) {
        this.imagenphppla = imagenphppla;
    }
}
