package apps.incaclic.jamear.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Platillos;
import com.example.jamear.R;

import java.util.List;

public class RVPlatillos extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;
    Conexionbd bd = new Conexionbd();

    private Context context;
    private LayoutInflater inflater;
    List<Platillos> data;
    Platillos current;
    private  String codigopla;
    int currentPos=0;

    public RVPlatillos(Context context, List<Platillos> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.platillos_cardview, parent, false);
        RVPlatillos.MyHolder holder = new RVPlatillos.MyHolder(v);

        v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RVPlatillos.MyHolder myHolder = (RVPlatillos.MyHolder) holder;
        Platillos current=data.get(position);
        myHolder.nomPlato.setText(current.nomPlatillos);
        myHolder.precioP.setText("S/ " + current.precio);
        codigopla = current.imagenphppla;
        //myHolder.stockP.setText("Stock " + current.stock);

        //String link = bd.getUrldatos()+"Imagenes/Platillos/"+current.codigoPla+".jpg";

        Glide.with(context).load(codigopla)
                //.apply(RequestOptions.circleCropTransform())
                //.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.ic_img)
                .error(R.drawable.ic_small)
                .into(myHolder.imgplatos);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView nomPlato;
        TextView precioP;
        //TextView stockP;
        ImageView imgplatos;


        public MyHolder(View itemView) {
            super(itemView);

            nomPlato = itemView.findViewById(R.id.edtPlatillos);
            precioP = itemView.findViewById(R.id.edtPrecioP);
            //stockP = itemView.findViewById(R.id.tvStockP);
            imgplatos = itemView.findViewById(R.id.imgPlatillos);
            //imgplatoAD = itemView.findViewById(R.id.imgplato);
        }
    }

}
