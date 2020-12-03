package apps.incaclic.jamear.Entidades;

public class Historial {

    public int codigoH;
    public String detalleH;
    public String fechaH;
    public String totalH;

    public Historial() {

    }

    public Historial(int codigoH, String detalleH, String fechaH, String totalH) {
        this.codigoH = codigoH;
        this.detalleH = detalleH;
        this.fechaH = fechaH;
        this.totalH = totalH;
    }

    public int getCodigoH() {
        return codigoH;
    }

    public void setCodigoH(int codigoH) {
        this.codigoH = codigoH;
    }

    public String getDetalleH() {
        return detalleH;
    }

    public void setDetalleH(String detalleH) {
        this.detalleH = detalleH;
    }

    public String getFechaH() {
        return fechaH;
    }

    public void setFechaH(String fechaH) {
        this.fechaH = fechaH;
    }

    public String getTotalH() {
        return totalH;
    }

    public void setTotalH(String totalH) {
        this.totalH = totalH;
    }

    @Override
    public String toString(){
        return detalleH;
    }
}
