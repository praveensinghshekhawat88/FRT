package com.callmangement.ui.inventory

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.callmangement.R
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelDisputeParts
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelSEUsers
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventoryRepository(private val context: Context) {
    @JvmField
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val modelPartsMutableLiveData = MutableLiveData<ModelParts?>()
    private val modelSEUsersMutableLiveData = MutableLiveData<ModelSEUsers?>()
    private val submitDispatchParts = MutableLiveData<ModelSavePartsDispatchDetails?>()
    private val partsDispatchInvoiceList = MutableLiveData<ModelDispatchInvoice?>()
    private val dispatchInvoicePartsList = MutableLiveData<ModelDispatchInvoice?>()
    private val modelDisputePartsMutableLiveData = MutableLiveData<ModelDisputeParts?>()

    fun getModelPartsMutableLiveData(): MutableLiveData<ModelParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.partsList
        call.enqueue(object : Callback<ModelParts?> {
            override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelPartsMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.value = false
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelPartsMutableLiveData
    }

    fun getModelAvailableStockPartsMutableLiveData(
        user_id: String?,
        item_id: String?
    ): MutableLiveData<ModelParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getAvailableStockPartsList(user_id, item_id)
        call.enqueue(object : Callback<ModelParts?> {
            override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    val model = response.body()
                    if (model!!.status== "200") {
                        modelPartsMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.value = false
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelPartsMutableLiveData
    }

    fun getPartsCurrentStockList(user_id: String?, item_id: String?): MutableLiveData<ModelParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getPartsCurrentStockList(user_id, item_id)
        call.enqueue(object : Callback<ModelParts?> {
            override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    val model = response.body()
                    if (model!!.status== "200") {
                        modelPartsMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.value = false
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelPartsMutableLiveData
    }

    fun getSEAvailableStockListForManager(
        user_id: String?,
        item_id: String?
    ): MutableLiveData<ModelParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getAllSE_AvlStockPartsList(user_id, item_id)
        call.enqueue(object : Callback<ModelParts?> {
            override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status== "200") {
                        modelPartsMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelPartsMutableLiveData
    }

    fun getAvailableStockListForSE(
        user_id: String?,
        item_id: String?
    ): MutableLiveData<ModelParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getSE_AvlStockPartsList(user_id, item_id)
        call.enqueue(object : Callback<ModelParts?> {
            override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status== "200") {
                        modelPartsMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelPartsMutableLiveData
    }

    fun getModelSEUsersMutableLiveData(districtId: String?): MutableLiveData<ModelSEUsers?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getSEUserList(districtId)
        call.enqueue(object : Callback<ModelSEUsers?> {
            override fun onResponse(call: Call<ModelSEUsers?>, response: Response<ModelSEUsers?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status== "200") {
                        modelSEUsersMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(context, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelSEUsers?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelSEUsersMutableLiveData
    }

    fun getSubmitDispatchParts(
        UserId: String?,
        InvoiceId: String?,
        DispatcherRemarks: String?
    ): MutableLiveData<ModelSavePartsDispatchDetails?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.submitDispatchParts(UserId, InvoiceId, DispatcherRemarks)
        call.enqueue(object : Callback<ModelSavePartsDispatchDetails?> {
            override fun onResponse(
                call: Call<ModelSavePartsDispatchDetails?>,
                response: Response<ModelSavePartsDispatchDetails?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status== "200") {
                        submitDispatchParts.setValue(model)
                    } else {
                        Toast.makeText(context, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelSavePartsDispatchDetails?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return submitDispatchParts
    }

    fun getPartsDispatchInvoiceList(
        InvoiceId: String?,
        UserId: String?,
        DistrictId: String?,
        date1: String?,
        date2: String?
    ): MutableLiveData<ModelDispatchInvoice?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getPartsDispatcherInvoices(InvoiceId, UserId, DistrictId, date1, date2)
        call.enqueue(object : Callback<ModelDispatchInvoice?> {
            override fun onResponse(
                call: Call<ModelDispatchInvoice?>,
                response: Response<ModelDispatchInvoice?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    partsDispatchInvoiceList.setValue(model)

                    //                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
//                        partsDispatchInvoiceList.setValue(model);
//                    }else {
//                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelDispatchInvoice?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return partsDispatchInvoiceList
    }

    fun getDispatchInvoicePartsList(
        InvoiceId: String?,
        UserId: String?,
        DispatchId: String?
    ): MutableLiveData<ModelDispatchInvoice?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getDispatchInvoiceParts(InvoiceId, UserId, DispatchId)
        call.enqueue(object : Callback<ModelDispatchInvoice?> {
            override fun onResponse(
                call: Call<ModelDispatchInvoice?>,
                response: Response<ModelDispatchInvoice?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    dispatchInvoicePartsList.setValue(model)
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelDispatchInvoice?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return dispatchInvoicePartsList
    }

    fun getModelDisputePartsMutableLiveData(
        UserId: String?,
        InvoiceId: String?
    ): MutableLiveData<ModelDisputeParts?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getDisputePartsList(UserId, InvoiceId)
        call.enqueue(object : Callback<ModelDisputeParts?> {
            override fun onResponse(
                call: Call<ModelDisputeParts?>,
                response: Response<ModelDisputeParts?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    modelDisputePartsMutableLiveData.setValue(model)
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelDisputeParts?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelDisputePartsMutableLiveData
    }

    companion object {
        private const val TAG = "InventoryRepository"
    }
}
