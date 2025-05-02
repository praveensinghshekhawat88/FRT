package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.R;
import com.callmangement.databinding.ItemDispatchChallanListActivityBinding;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.ui.inventory.DispatchChallanListActivity;
import com.callmangement.ui.inventory.DispatchChallanPartsListDetailsActivity;
import java.util.List;

public class DispatchChallanListActivityAdapter extends RecyclerView.Adapter<DispatchChallanListActivityAdapter.ViewHolder> {

    private final Activity context;
    private final List<ModelPartsDispatchInvoiceList> list;

    public DispatchChallanListActivityAdapter(Activity context, List<ModelPartsDispatchInvoiceList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDispatchChallanListActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_dispatch_challan_list_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelPartsDispatchInvoiceList model = list.get(position);

        holder.binding.textInvoiceNumber.setText(model.getInvoiceId());
        holder.binding.textDispatchFrom.setText(model.getDispatcherName());
        holder.binding.textDispatchTo.setText(model.getDistrictNameEng());
        holder.binding.textUsername.setText(model.getReciverName());
        holder.binding.textInvoiceDate.setText(model.getDispatchDateStr());
        if (!model.getCourierName().isEmpty()) {
            holder.binding.layoutCourierName.setVisibility(View.VISIBLE);
            holder.binding.textCourierName.setText(model.getCourierName());
        } else {
            holder.binding.layoutCourierName.setVisibility(View.GONE);
        }
        if (!model.getCourierTrackingNo().isEmpty()) {
            holder.binding.layoutCourierTrackingNumber.setVisibility(View.VISIBLE);
            holder.binding.textCourierTrackingNumber.setText(model.getCourierTrackingNo());
        } else {
            holder.binding.layoutCourierTrackingNumber.setVisibility(View.GONE);
        }

        if (model.getIsReceived().equalsIgnoreCase("true")){
            holder.binding.layoutReceivedStatus.setVisibility(View.VISIBLE);
            holder.binding.layoutReceivedDate.setVisibility(View.VISIBLE);
            holder.binding.textReceivedStatus.setText("Received");
            holder.binding.textReceivedStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
            holder.binding.textReceivedDate.setText(model.getReceivedDateStr());
        }else {
            holder.binding.layoutReceivedStatus.setVisibility(View.VISIBLE);
            holder.binding.layoutReceivedDate.setVisibility(View.GONE);
            holder.binding.textReceivedStatus.setText("Not Received");
            holder.binding.textReceivedStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }

        if (model.getIsSubmitted().equalsIgnoreCase("true")){
            holder.binding.ivDelete.setVisibility(View.GONE);
            holder.binding.buttonDispatch.setVisibility(View.GONE);
            holder.binding.buttonView.setVisibility(View.VISIBLE);
            holder.binding.textInvoiceStatus.setText("Dispatched");
            holder.binding.textInvoiceStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }else {
            holder.binding.ivDelete.setVisibility(View.VISIBLE);
            holder.binding.buttonDispatch.setVisibility(View.VISIBLE);
            holder.binding.buttonView.setVisibility(View.GONE);
            holder.binding.textInvoiceStatus.setText("Saved");
            holder.binding.textInvoiceStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }

        /*if (model.getItemStockStatusId().equals("2")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        } else if (model.getItemStockStatusId().equals("7")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else if (model.getItemStockStatusId().equals("8")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else if (model.getItemStockStatusId().equals("9")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }else {
            holder.binding.layoutDisputeStatus.setVisibility(View.GONE);
        }*/

        holder.binding.buttonView.setOnClickListener(view -> context.startActivity(new Intent(context, DispatchChallanPartsListDetailsActivity.class).putExtra("param", model)));

//        holder.binding.buttonDispatch.setOnClickListener(view -> context.startActivity(new Intent(context, DispatchChallanPartsListDetailsActivity.class).putExtra("param", model)));
        /*holder.binding.ivDelete.setOnClickListener(view -> {
            if (context instanceof DispatchChallanListActivity) {
                ((DispatchChallanListActivity)context).dialogDeleteInvoice(model.getInvoiceId());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemDispatchChallanListActivityBinding binding;
        public ViewHolder(@NonNull ItemDispatchChallanListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

