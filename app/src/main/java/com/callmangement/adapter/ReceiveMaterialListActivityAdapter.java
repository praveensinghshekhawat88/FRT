package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemComplaintResolveListBinding;
import com.callmangement.databinding.ItemReceiveMaterialListBinding;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.model.inventrory.ModelReceiveMaterialListData;
import com.callmangement.ui.inventory.ReceiveMaterialListActivity;
import com.callmangement.ui.inventory.ReceiveMaterialListDetailsActivity;

import java.io.Serializable;
import java.util.List;

public class ReceiveMaterialListActivityAdapter extends RecyclerView.Adapter<ReceiveMaterialListActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelPartsDispatchInvoiceList> list;

    public ReceiveMaterialListActivityAdapter(Context context, List<ModelPartsDispatchInvoiceList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReceiveMaterialListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_receive_material_list, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] separator = list.get(position).getDispatchDateStr().split(" ");
        String dateStr = separator[0];
        holder.binding.textInvoiceNumber.setText(list.get(position).getInvoiceId());
        holder.binding.textInvoiceDate.setText(list.get(position).getDispatchDateStr());
        holder.binding.tvSenderName.setText("Send by "+list.get(position).getDispatcherName());
        holder.binding.textRemarkStr.setText(list.get(position).getDispatcherName()+" "+context.getResources().getString(R.string.remark));
//        holder.binding.textRemark.setText(""+list.get(position).getDispatcherRemarks());
        holder.binding.textCourierName.setText(list.get(position).getCourierName());
        holder.binding.textCourierTrackingNumber.setText(list.get(position).getCourierTrackingNo());

        if (!list.get(position).getCourierName().isEmpty()) {
            holder.binding.layoutCourierName.setVisibility(View.VISIBLE);
            holder.binding.textCourierName.setText(list.get(position).getCourierName());
        } else {
            holder.binding.layoutCourierName.setVisibility(View.GONE);
        }

        if (!list.get(position).getCourierTrackingNo().isEmpty()) {
            holder.binding.layoutCourierTrackingNumber.setVisibility(View.VISIBLE);
            holder.binding.textCourierTrackingNumber.setText(list.get(position).getCourierTrackingNo());
        } else {
            holder.binding.layoutCourierTrackingNumber.setVisibility(View.GONE);
        }

        /*if (list.get(position).getItemStockStatusId().equals("7")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeStatus.setText(list.get(position).getItemStockStatus());
            holder.binding.textDisputeStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else {
            holder.binding.layoutDisputeStatus.setVisibility(View.GONE);
        }*/

        if (list.get(position).getIsReceived().equalsIgnoreCase("true")){
            holder.binding.layoutReceivedDate.setVisibility(View.VISIBLE);
            holder.binding.textReceivedDate.setText(list.get(position).getReceivedDateStr());
            holder.binding.textInvoiceStatus.setText("Received");
            holder.binding.textInvoiceStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));

            holder.binding.buttonReceive.setVisibility(View.GONE);
            holder.binding.buttonView.setVisibility(View.VISIBLE);

        } else if (list.get(position).getIsReceived().equalsIgnoreCase("false")){
            holder.binding.layoutReceivedDate.setVisibility(View.GONE);
            holder.binding.textInvoiceStatus.setText("Not Received");
            holder.binding.textInvoiceStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));

            holder.binding.buttonReceive.setVisibility(View.VISIBLE);
            holder.binding.buttonView.setVisibility(View.GONE);
        }

        holder.binding.buttonReceive.setOnClickListener(view -> {
            Intent intent = new Intent(context,ReceiveMaterialListDetailsActivity.class);
            intent.putExtra("invoice_id",list.get(position).getInvoiceId());
            intent.putExtra("dispatch_id",list.get(position).getDispatchId());
            intent.putExtra("date",dateStr);
            intent.putExtra("param", list.get(position));
            context.startActivity(intent);
        });

        holder.binding.buttonView.setOnClickListener(view -> {
            Intent intent = new Intent(context,ReceiveMaterialListDetailsActivity.class);
            intent.putExtra("invoice_id",list.get(position).getInvoiceId());
            intent.putExtra("dispatch_id",list.get(position).getDispatchId());
            intent.putExtra("date",dateStr);
            intent.putExtra("param", list.get(position));
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemReceiveMaterialListBinding binding;
        public ViewHolder(@NonNull ItemReceiveMaterialListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
