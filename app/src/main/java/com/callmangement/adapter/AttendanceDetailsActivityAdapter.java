package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.R;
import com.callmangement.databinding.ItemAttedanceDetailsActivityBinding;
import com.callmangement.model.attendance.ModelAttendanceData;
import com.callmangement.ui.attendance.LocationListActivity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringTokenizer;

public class AttendanceDetailsActivityAdapter extends RecyclerView.Adapter<AttendanceDetailsActivityAdapter.ViewHolder> {
    private List<ModelAttendanceData> modelAttendanceData;
    private final Context context;

    public AttendanceDetailsActivityAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAttedanceDetailsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_attedance_details_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelAttendanceData model = modelAttendanceData.get(position);
        holder.binding.setData(model);
        holder.binding.textDate.setText(formattedFilterDate(model.getPunch_In_Date()));
         //holder.binding.textAddress.setText(formattedFilterDate(model.getAddress()));

        String address = model.getAddress();
        //  binding.tvAddressIn.setText(address_in);


     //   Log.d("tvAddress"," "+address);


        //  decodeFromUTF8(address_out);

        try {
            String decodedAddressIn = decodeUtf8Hex(address);
          //  Log.d("tvAddress"," "+decodedAddressIn);
           holder. binding.textAddress.setText(decodedAddressIn);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            holder. binding.textAddress.setText(address);

        }

        holder.binding.itemList.setOnClickListener(view -> context.startActivity(new Intent(context, LocationListActivity.class).putExtra("param", model)));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelAttendanceData> modelAttendanceData){
        this.modelAttendanceData = modelAttendanceData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (modelAttendanceData!=null){
            return modelAttendanceData.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemAttedanceDetailsActivityBinding binding;
        public ViewHolder(@NonNull ItemAttedanceDetailsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private String formattedFilterDate(String dateStr){
        StringTokenizer tokenizer = new StringTokenizer(dateStr,"-");
        String year = tokenizer.nextToken();
        String month = tokenizer.nextToken();
        String date = tokenizer.nextToken();
        return date+"-"+month+"-"+year;
    }

    public static String decodeUtf8Hex(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        // Remove \x from the string and ensure length is even
        String sanitizedEncoded = encoded.replace("\\x", "").toUpperCase();

        if (sanitizedEncoded.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string has an odd length, which is invalid.");
        }

        try {
            // Convert hex string to byte array
            byte[] bytes = hexStringToByteArray(sanitizedEncoded);
            // Convert byte array to UTF-8 string
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to decode hex string: " + e.getMessage());
            throw e; // Rethrow to handle elsewhere if needed
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character at position " + i);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }
}
