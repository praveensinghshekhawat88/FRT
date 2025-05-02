package com.callmangement.ui.pos_help.activity

import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityFaqListBinding
import com.callmangement.ui.pos_help.adapter.FAQListAdapter
import com.callmangement.ui.pos_help.model.ModelFAQList

class FAQListActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityFaqListBinding? = null
    private val modelFAQList: MutableList<ModelFAQList> = ArrayList()
    var isUp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        setUpActionBar()
        setUpOnClickListener()
        setUpAdapter()
    }

    private fun setUpActionBar() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.faq_list)
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private fun setUpAdapter() {
        val model1 = ModelFAQList()
        model1.id = "1"
        model1.question = "RD issue."
        modelFAQList.add(model1)

        val model2 = ModelFAQList()
        model2.id = "2"
        model2.question = "Fingerprint not working."
        modelFAQList.add(model2)

        val model3 = ModelFAQList()
        model3.id = "3"
        model3.question = "Fingerprint capturing failed."
        modelFAQList.add(model3)

        binding!!.rvFaqList.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvFaqList.adapter = FAQListAdapter(mContext, modelFAQList)
    }

    // slide the view from below itself to the current position
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun onSlideViewButtonClick(view: View) {
        if (isUp) {
            slideDown(view)
        } else {
            slideUp(view)
        }
        isUp = !isUp
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}