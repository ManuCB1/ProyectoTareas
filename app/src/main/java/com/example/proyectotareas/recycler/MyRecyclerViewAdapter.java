package com.example.proyectotareas.recycler;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectotareas.R;
import com.example.proyectotareas.model.Tarea;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Tarea> mData;
    private LayoutInflater mInflater;
    private int layout_items;
    private ItemClickListener mClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public MyRecyclerViewAdapter(Context contexto, List<Tarea> data, int layout_items) {
        this.mInflater = LayoutInflater.from(contexto);
        this.mData = data;
        this.layout_items = layout_items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View parentView = mInflater.inflate(layout_items, parent, false);

        return new ViewHolder(parentView);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tarea tarea = mData.get(position);
        holder.textNombreR.setText(tarea.getNombre());
        holder.textContenidoR.setText(tarea.getContenido());
        if (holder.textFechaR != null){
            holder.textFechaR.setText(tarea.getFecha());
        }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }
    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    TextView textNombreR;
    TextView textContenidoR;
    TextView textFechaR;

    ViewHolder(View itemView) {
        super(itemView);
        textNombreR = itemView.findViewById(R.id.textNombreR);
        textContenidoR = itemView.findViewById(R.id.textContenidoR);
        textFechaR = itemView.findViewById(R.id.textFechaR);
        itemView.setOnCreateContextMenuListener(this);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("--Selecciona una opción--");
        Context context = v.getContext();
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_contextual, menu);
    }

}

    public Tarea getItem(int id){
        return mData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View activista, int position);
    }
}

