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
import apps.incaclic.jamear.Entidades.Tiendas;
import com.example.jamear.R;

import java.util.List;

public class RVTiendas extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;

    private Context context;
    private LayoutInflater inflater;
    List<Tiendas> data;
    Tiendas current;
    int currentPos=0;

    private String imagenphptiendas;
    Conexionbd bd = new Conexionbd();

    public RVTiendas(Context context, List<Tiendas> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.tiendas_cardview, parent, false);
        RVTiendas.MyHolder holder = new RVTiendas.MyHolder(v);
        v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RVTiendas.MyHolder myHolder = (RVTiendas.MyHolder) holder;
        Tiendas current=data.get(position);
        myHolder.nomTienda.setText(current.RazonSocialTc);
        imagenphptiendas = current.imagenphptienda;

        //String link = bd.getUrldatos()+"Imagenes/Tiendas/"+current.codigoTienda+".jpg";

        Glide.with(context).load(imagenphptiendas)
                //.apply(RequestOptions.circleCropTransform())
                //.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.ic_img)
                .error(R.drawable.ic_small)
                .into(myHolder.imgTienda);

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
        TextView nomTienda;
        ImageView imgTienda;


        public MyHolder(View itemView) {
            super(itemView);

            nomTienda = itemView.findViewById(R.id.edtTienda);
            imgTienda = itemView.findViewById(R.id.imgtienda);
        }
    }

}
