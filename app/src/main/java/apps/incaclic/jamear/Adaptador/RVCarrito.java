package apps.incaclic.jamear.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import apps.incaclic.jamear.Entidades.Carrito;
import com.example.jamear.R;

import java.util.ArrayList;

public class RVCarrito extends BaseAdapter {

    private Context context;
    private ArrayList<Carrito> listaProductos;

    public RVCarrito(Context context, ArrayList<Carrito> listaProductos){
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @Override
    public int getCount() {
        return listaProductos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaProductos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Carrito item = (Carrito) getItem(position);

        v = LayoutInflater.from(context).inflate(R.layout.item_carrito,null);
        TextView CnomP = (TextView) v.findViewById(R.id.tvNomProC);
        TextView CcantidadP = (TextView) v.findViewById(R.id.tvCantidadC);
        TextView CpreuniP = (TextView) v.findViewById(R.id.tvPrecioC);
        TextView CsubtotalP = (TextView) v.findViewById(R.id.tvSubToC);
        TextView CnomtiendaC = (TextView) v.findViewById(R.id.tvNomTienC);

        CnomP.setText(item.getNombrePoductoC());
        CnomtiendaC.setText(item.getNombreTiendaC());
        CcantidadP.setText(item.getCantidadC());
        CpreuniP.setText("S/ "+ item.getPrecioProductoC());

        double pr = Double.parseDouble(item.getPrecioProductoC());
        int cant = Integer.parseInt(item.getCantidadC());
        double subt = (pr * cant);
        CsubtotalP.setText("S/ "+String.format("%.2f",subt));

        return v;
    }
}
