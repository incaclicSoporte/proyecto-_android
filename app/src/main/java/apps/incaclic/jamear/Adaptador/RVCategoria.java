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
import apps.incaclic.jamear.Entidades.Categoria;
import com.example.jamear.R;

import java.util.List;

public class RVCategoria extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;

    private Context context;
    private LayoutInflater inflater;
    List<Categoria> data;
    Categoria current;
    int currentPos=0;

    private String imagenphpcate;
    Conexionbd bd = new Conexionbd();

    public RVCategoria(Context context, List<Categoria> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.categoria_cardview , parent, false);
        RVCategoria.MyHolder holder = new RVCategoria.MyHolder(v);

        v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RVCategoria.MyHolder myHolder = (RVCategoria.MyHolder) holder;
        Categoria current=data.get(position);
        myHolder.nomC.setText(current.nomCategoria);
        imagenphpcate = current.imagenphpcate;

        //String URL = bd.getUrldatos()+"Imagenes/Categorias/"+current.codCategoria+".jpg";

        Glide.with(context).load(imagenphpcate)
                //.apply(RequestOptions.circleCropTransform())
                //.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.ic_img)
                .error(R.drawable.ic_small)
                .into(myHolder.imgCate);

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
        TextView nomC;
        ImageView imgCate;


        public MyHolder(View itemView) {
            super(itemView);

            nomC =  itemView.findViewById(R.id.edtCategoria);
            imgCate =  itemView.findViewById(R.id.imgcategoria);
        }
    }

}
