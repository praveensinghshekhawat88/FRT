package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.R;
import com.callmangement.databinding.ItemTrainingScheduleFormActivityBinding;
import com.callmangement.model.training_schedule.ModelTrainingScheduleFormAddItem;
import java.util.List;

public class TrainingScheduleFormActivityAdapter extends RecyclerView.Adapter<TrainingScheduleFormActivityAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelTrainingScheduleFormAddItem> list;

    public TrainingScheduleFormActivityAdapter(Activity context, List<ModelTrainingScheduleFormAddItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrainingScheduleFormActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_training_schedule_form_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        context.getCurrentFocus();

        InputMethodManager imm1 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.hideSoftInputFromWindow(holder.binding.inputName.getWindowToken(), 0);
        holder.binding.inputName.requestFocus();

        InputMethodManager imm2 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(holder.binding.inputFpsCode.getWindowToken(), 0);
        //holder.binding.inputFpsCode.requestFocus();

        InputMethodManager imm3 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm3.hideSoftInputFromWindow(holder.binding.inputPhone.getWindowToken(), 0);
        //holder.binding.inputPhone.requestFocus();

        holder.binding.inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (list.get(position).getName().equals("")) {
                    if (!s.toString().equals("")) {
                        list.get(position).setName(s.toString().trim());
                    } else {
                        if (list.get(position).getName().equals(""));
                    }
                }else {
                    if (!s.toString().equals("")) {
                        list.get(position).setName(s.toString().trim());
                    }
                }
            }
        });

        holder.binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (list.get(position).getFpsCode().equals("")) {
                    if (!s.toString().equals("")) {
                        list.get(position).setFpsCode(s.toString().trim());
                    } else {
                        if (list.get(position).getFpsCode().equals(""));
                    }
                } else {
                    if (!s.toString().equals("")) {
                        list.get(position).setFpsCode(s.toString().trim());
                    }
                }
            }
        });

        holder.binding.inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (list.get(position).getPhone().equals("")) {
                    if (!s.toString().equals("")) {
                        list.get(position).setPhone(s.toString().trim());
                    } else {
                        if (list.get(position).getPhone().equals(""));
                    }
                }else {
                    if (!s.toString().equals("")) {
                        list.get(position).setPhone(s.toString().trim());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemTrainingScheduleFormActivityBinding binding;
        public ViewHolder(@NonNull ItemTrainingScheduleFormActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
