package apps.incaclic.jamear.Adaptador;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import apps.incaclic.jamear.Conexion.Conexionbd;
import apps.incaclic.jamear.Entidades.Cateprincipal;
import com.example.jamear.R;

import java.util.List;

public class RVCateprincipal extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private View.OnClickListener listener;

    private Context context;
    private LayoutInflater inflater;
    List<Cateprincipal> data;
    Cateprincipal current;
    int currentPos=0;

    int posicionMarcada=0;

    private String imagenphpcategoriaprincipal;
    Conexionbd bd = new Conexionbd();

    public RVCateprincipal(Context context, List<Cateprincipal> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cdv_categoria_principal , parent, false);
        RVCateprincipal.MyHolder holder = new RVCateprincipal.MyHolder(v);

        v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RVCateprincipal.MyHolder myHolder = (RVCateprincipal.MyHolder) holder;
        Cateprincipal current=data.get(position);
        myHolder.nomcateP.setText(current.nomcatepri);
        imagenphpcategoriaprincipal = current.imagenphpcatepri;

        /////////////////////////////////////
        /*final int pos = position;

        myHolder.tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posicionMarcada = pos;
                notifyDataSetChanged();
            }
        });

        if (posicionMarcada==position){
            myHolder.linea.setBackgroundColor(ContextCompat.getColor(context,R.color.amarilloinca));
        }else{
            myHolder.linea.setBackgroundColor(ContextCompat.getColor(context,R.color.blanco));
        }*/
        /////////////////////////////////

        //String URL = bd.getUrldatos()+"Imagenes/Cate_principal/"+current.codigopri+".jpg";

        Glide.with(context).load(imagenphpcategoriaprincipal)
                //.apply(RequestOptions.circleCropTransform())
                //.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.ic_img)
                .error(R.drawable.ic_small)
                .into(myHolder.imgcateP);
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
        TextView nomcateP, linea;
        ImageView imgcateP;
        CardView tarjeta;


        public MyHolder(View itemView) {
            super(itemView);

            nomcateP =  itemView.findViewById(R.id.tvnomcatepri);
            //linea =  itemView.findViewById(R.id.tvlinea);
            tarjeta = itemView.findViewById(R.id.cdvctpri);
            imgcateP =  itemView.findViewById(R.id.imgcateprincipal);
        }
    }

}
