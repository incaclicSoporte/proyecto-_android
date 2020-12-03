package apps.incaclic.jamear.Entidades;

public class Categoria {

    public int codCategoria;
    public String nomCategoria;
    public String imagenphpcate;

    public Categoria() {
    }

    public Categoria(int codCategoria, String nomCategoria, String imagenphpcate) {
        this.codCategoria = codCategoria;
        this.nomCategoria = nomCategoria;
        this.imagenphpcate = imagenphpcate;
    }

    public int getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(int codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    public String getImagenphpcate() {
        return imagenphpcate;
    }

    public void setImagenphpcate(String imagenphpcate) {
        this.imagenphpcate = imagenphpcate;
    }
}
