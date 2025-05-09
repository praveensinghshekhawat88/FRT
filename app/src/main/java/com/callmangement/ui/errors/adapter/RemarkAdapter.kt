package com.callmangement.ui.errors.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemRemarkBinding
import com.callmangement.ui.errors.model.GetRemarkDatum

class RemarkAdapter(
    private val context: Context,
    private val getRemarkDatumArrayList: ArrayList<GetRemarkDatum>
) :
    RecyclerView.Adapter<RemarkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRemarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(context.applicationContext, string, Toast.LENGTH_SHORT).show()
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
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getRemarkDatumArrayList[position]
        holder.binding.remark.text = model.remark
        //    holder.binding.createdby.setText(model.getCreatedBy().toString());
        val createdon = model.remarkDate.toString()
        if (createdon != null) {
            holder.binding.createdon.text = createdon
            // holder.binding.createdby.setText(createdby);
        } else {
        }

        val createdby = model.userType.toString()
        if (createdby != null) {
            holder.binding.createdby.text = createdby
            // holder.binding.createdby.setText(createdby);
        } else {
        }
        holder.binding.status.text = model.errorStatus


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

    override fun getItemCount(): Int {
        return getRemarkDatumArrayList.size
    }

    class ViewHolder(val binding: ItemRemarkBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
