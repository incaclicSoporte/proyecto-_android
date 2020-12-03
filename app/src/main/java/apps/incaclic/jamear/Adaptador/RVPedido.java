package apps.incaclic.jamear.Adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import apps.incaclic.jamear.Entidades.Pedido;

import com.example.jamear.R;

import java.util.ArrayList;

public class RVPedido extends BaseAdapter {

    private Context context;
    private ArrayList<Pedido> listaPedidos;

    public RVPedido(Context context, ArrayList<Pedido> listaProductos){
        this.context = context;
        this.listaPedidos = listaProductos;
    }

    @Override
    public int getCount() {
        return listaPedidos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaPedidos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Pedido item = (Pedido) getItem(position);

        v = LayoutInflater.from(context).inflate(R.layout.item_pedido,null);
        TextView hora = (TextView) v.findViewById(R.id.tvhoraPe);
        TextView fecha = (TextView) v.findViewById(R.id.tvfechaPe);
        TextView numpedido = (TextView) v.findViewById(R.id.tvpedidoPe);
        TextView nombredelatienda = (TextView) v.findViewById(R.id.tvnomtienda);
        TextView estado = (TextView) v.findViewById(R.id.tvestadoPe);

        hora.setText("Hora "+item.getHora());
        fecha.setText("Fecha "+item.getFecha());
        numpedido.setText("Pedido N° "+item.getNumpedido());
        nombredelatienda.setText("Tienda - "+item.getNombredelatienda());

        if (item.getEstado().equals("Pendiente")) {
            estado.setText("Estado - " + item.getEstado());
            estado.setTextColor(Color.parseColor("#ECB404"));
        }else if (item.getEstado().equals("En Proceso")){
            estado.setText("Estado - " + item.getEstado());
            estado.setTextColor(Color.parseColor("#008000"));
        }else if (item.getEstado().equals("Cancelado")){
            estado.setText("Estado - " + item.getEstado());
            estado.setTextColor(Color.parseColor("#FF0000"));
        }else if (item.getEstado().equals("Concluido")){
            estado.setText("Estado - " + item.getEstado());
            estado.setTextColor(Color.parseColor("#0000FF"));
        }

        return v;
    }

}
