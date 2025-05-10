package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemAttedanceDetailsActivityBinding
import com.callmangement.model.attendance.ModelAttendanceData
import com.callmangement.ui.attendance.LocationListActivity
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.StringTokenizer

class AttendanceDetailsActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<AttendanceDetailsActivityAdapter.ViewHolder>() {
    private var modelAttendanceData: List<ModelAttendanceData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAttedanceDetailsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_attedance_details_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelAttendanceData!![position]
        holder.binding.data = model
        holder.binding.textDate.text = formattedFilterDate(model.punch_In_Date)

        //holder.binding.textAddress.setText(formattedFilterDate(model.getAddress()));
        val address = model.address


        //  binding.tvAddressIn.setText(address_in);


        //   Log.d("tvAddress"," "+address);


        //  decodeFromUTF8(address_out);
        try {
            val decodedAddressIn = decodeUtf8Hex(address)
            //  Log.d("tvAddress"," "+decodedAddressIn);
            holder.binding.textAddress.text = decodedAddressIn
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            holder.binding.textAddress.text = address
        }

        holder.binding.itemList.setOnClickListener { view: View? ->
            context.startActivity(
                Intent(
                    context,
                    LocationListActivity::class.java
                ).putExtra("param", model)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(modelAttendanceData: List<ModelAttendanceData>?) {
        this.modelAttendanceData = modelAttendanceData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (modelAttendanceData != null) {
            modelAttendanceData!!.size
        } else {
            0
        }
    }

    inner class ViewHolder(val binding: ItemAttedanceDetailsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun formattedFilterDate(dateStr: String?): String {
        val tokenizer = StringTokenizer(dateStr, "-")
        val year = tokenizer.nextToken()
        val month = tokenizer.nextToken()
        val date = tokenizer.nextToken()
        return "$date-$month-$year"
    }

    companion object {
        fun decodeUtf8Hex(encoded: String?): String {
            if (encoded == null || encoded.isEmpty()) {
                return ""
            }

            // Remove \x from the string and ensure length is even
            val sanitizedEncoded = encoded.replace("\\x", "").uppercase(Locale.getDefault())

            require(sanitizedEncoded.length % 2 == 0) { "Hex string has an odd length, which is invalid." }

            try {
                // Convert hex string to byte array
                val bytes = hexStringToByteArray(sanitizedEncoded)
                // Convert byte array to UTF-8 string
                return String(bytes, StandardCharsets.UTF_8)
            } catch (e: IllegalArgumentException) {
                System.err.println("Failed to decode hex string: " + e.message)
                throw e // Rethrow to handle elsewhere if needed
            }
        }

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                val high = s[i].digitToIntOrNull(16) ?: -1
                val low = s[i + 1].digitToIntOrNull(16) ?: -1
                require(!(high == -1 || low == -1)) { "Invalid hex character at position $i" }
                data[i / 2] = ((high shl 4) + low).toByte()
                i += 2
            }
            return data
        }
    }
}
