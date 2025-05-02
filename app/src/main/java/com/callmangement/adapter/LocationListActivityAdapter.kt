package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.R;
import com.callmangement.databinding.ItemLocationListActivityBinding;
import com.callmangement.model.attendance.ModelAddLocationData;
import com.callmangement.model.attendance.ModelAttendanceData;

import java.util.List;
import java.util.StringTokenizer;

public class LocationListActivityAdapter extends RecyclerView.Adapter<LocationListActivityAdapter.ViewHolder> {
    private final Context context;
    private List<ModelAddLocationData> modelAddLocationData;
    private final ModelAttendanceData modelAttendanceData;

    public LocationListActivityAdapter(Context context, ModelAttendanceData modelAttendanceData) {
        this.context = context;
        this.modelAttendanceData = modelAttendanceData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationListActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_location_list_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelAddLocationData model = modelAddLocationData.get(position);
        holder.binding.setData(model);
        holder.binding.textDate.setText(formattedFilterDate(model.getLocation_Date_Time()));
        holder.binding.textUsername.setText(modelAttendanceData.getUsername());
        holder.binding.textEmail.setText(modelAttendanceData.getEmail());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelAddLocationData> modelAddLocationData){
        this.modelAddLocationData = modelAddLocationData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (modelAddLocationData!=null){
            return modelAddLocationData.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemLocationListActivityBinding binding;
        public ViewHolder(@NonNull ItemLocationListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private String formattedFilterDate(String dateTimeStr){
        StringTokenizer tokenizer = new StringTokenizer(dateTimeStr," ");
        String date = tokenizer.nextToken();
        String time = tokenizer.nextToken();
        //String am_pm = tokenizer.nextToken();
        StringTokenizer tokenizerDate = new StringTokenizer(date,"-");
        String year = tokenizerDate.nextToken();
        String month = tokenizerDate.nextToken();
        String day = tokenizerDate.nextToken();
        return day+"-"+month+"-"+year+" "+time;
    }

}
