package apps.incaclic.jamear.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import apps.incaclic.jamear.Entidades.Historial;
import com.example.jamear.R;

import java.util.ArrayList;

public class RVHistorial extends BaseAdapter {

    private Context context;
    private ArrayList<Historial> HistorialCompra;

    public RVHistorial(Context context, ArrayList<Historial> listaHistorial){
        this.context = context;
        this.HistorialCompra = listaHistorial;
    }

    @Override
    public int getCount() {
        return HistorialCompra.size();
    }

    @Override
    public Object getItem(int position) {
        return HistorialCompra.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Historial item = (Historial) getItem(position);

        v = LayoutInflater.from(context).inflate(R.layout.historial_cardview,null);
        TextView Hnumpedido = v.findViewById(R.id.tvnumpedido);
        TextView Hfecha = v.findViewById(R.id.tvFechaH);
        TextView Htotal = v.findViewById(R.id.tvTotalH);
        TextView Hdescripcion = v.findViewById(R.id.tvDatoHistorial);

        Hnumpedido.setText("Pedido N° "+ item.getCodigoH());
        Hfecha.setText("Fecha " + item.getFechaH());
        Htotal.setText("S/ " + item.getTotalH());
        Hdescripcion.setText(item.getDetalleH());

        return v;
    }

}
