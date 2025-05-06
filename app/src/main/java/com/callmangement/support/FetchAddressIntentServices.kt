package com.callmangement.support

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.widget.Toast
import java.util.Locale

class FetchAddressIntentServices : IntentService("FetchAddressIntentServices") {
    var resultReceiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            var errormessgae = ""
            resultReceiver = intent.getParcelableExtra(Constants.RECEVIER)
            val location =
                intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)
                    ?: return
            val geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address>? = null

            try {
                addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
            } catch (ioException: Exception) {
                // Log.e("", "Error in getting address for the location");
            }

            if (addresses == null || addresses.size == 0) {
                errormessgae = "No address found for the location"
                Toast.makeText(this, errormessgae, Toast.LENGTH_SHORT).show()
            } else {
                val address = addresses[0]
                val str_postcode = address.postalCode
                val str_Country = address.countryName
                val str_state = address.adminArea
                val str_district = address.subAdminArea
                val str_locality = address.locality
                val str_address = address.featureName
                devliverResultToRecevier(
                    Constants.SUCCESS_RESULT,
                    str_address,
                    str_locality,
                    str_district,
                    str_state,
                    str_Country,
                    str_postcode
                )
            }
        }
    }

    private fun devliverResultToRecevier(
        resultcode: Int,
        address: String,
        locality: String,
        district: String,
        state: String,
        country: String,
        postcode: String
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.ADDRESS, address)
        bundle.putString(Constants.LOCAITY, locality)
        bundle.putString(Constants.DISTRICT, district)
        bundle.putString(Constants.STATE, state)
        bundle.putString(Constants.COUNTRY, country)
        bundle.putString(Constants.POST_CODE, postcode)
        resultReceiver!!.send(resultcode, bundle)
    }
} /* List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (Exception exception) {
                errormessgae = exception.getMessage();
            }
            ArrayList<String> addressFragments = null;
            if (addresses == null || addresses.isEmpty()) {
                devliverResultToRecevier(Constants.FAILURE_RESULT, errormessgae);
            } else {
                Address address = addresses.get(0);

                addressFragments = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                    addressFragments.add(address.getAddressLine(i));
            }
            devliverResultToRecevier(Constants.SUCCESS_RESULT, TextUtils.join(
                    Objects.requireNonNull(System.getProperty("line.separator")), addressFragments));*/

