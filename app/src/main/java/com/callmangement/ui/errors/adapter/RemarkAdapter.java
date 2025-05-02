package com.callmangement.ui.errors.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemRemarkBinding;
import com.callmangement.ui.errors.model.GetRemarkDatum;

import java.util.ArrayList;

public class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<GetRemarkDatum> getRemarkDatumArrayList;

    public RemarkAdapter(Context context, ArrayList<GetRemarkDatum> getRemarkDatumArrayList) {
        this.context = context;
        this.getRemarkDatumArrayList = getRemarkDatumArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRemarkBinding binding = ItemRemarkBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }
    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(context.getApplicationContext(), string, Toast.LENGTH_SHORT).show();

    }


   /* public void showProgress(String message) {
        hideProgress();
        mDialog = new ProgressDialog(context.getApplicationContext());
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void showProgress() {
        hideProgress();
        mDialog = new ProgressDialog(context.getApplicationContext());
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void hideProgress() {
        try {
            if (mDialog!=null) mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetRemarkDatum model = getRemarkDatumArrayList.get(position);
        holder.binding.remark.setText(model.getRemark());
  //    holder.binding.createdby.setText(model.getCreatedBy().toString());
        String createdon = String.valueOf(model.getRemarkDate());
        if (createdon!=null)
        {
            holder.binding.createdon.setText(createdon);
            // holder.binding.createdby.setText(createdby);
        }
        else
        {

        }

      String createdby = String.valueOf(model.getUserType());
        if (createdby!=null)
        {
            holder.binding.createdby.setText(createdby);
            // holder.binding.createdby.setText(createdby);
        }
        else
        {

        }
        holder.binding.status.setText(model.getErrorStatus());


       /* else if (model.getErrorStatusId() == 3) {
            holder.binding.errortype.setText(model.getErrorStatus());
            holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }*/



    /*    holder.binding.rlItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, ErrorposDetail.class);
            intent.putExtra("param",  model);
            context.startActivity(intent);
        });*/


      //  holder.binding.t

    }

    @Override
    public int getItemCount() {
        return getRemarkDatumArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRemarkBinding binding;
        public ViewHolder(ItemRemarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
