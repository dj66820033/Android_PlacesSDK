package com.dry.a2map.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dry.a2map.R;
import com.dry.a2map.beans.ItemBean;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private final List<ItemBean> listdata;

    public ListAdapter(List<ItemBean> listdata){
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.infoview,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.setData(listdata.get(position));
    }

    @Override
    public int getItemCount() {
        if (listdata != null){
            return listdata.size();
        }
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView view_name,view_txt_category,view_txt_tel;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            view_name = itemView.findViewById(R.id.name);
            view_txt_category = itemView.findViewById(R.id.txt_category);
            view_txt_tel = itemView.findViewById(R.id.txt_tel);
        }

        public void setData(ItemBean itemBean){
            view_name.setText(itemBean.name);
            view_txt_category.setText(itemBean.category);
            view_txt_tel.setText(itemBean.tel);
        }
    }
}
