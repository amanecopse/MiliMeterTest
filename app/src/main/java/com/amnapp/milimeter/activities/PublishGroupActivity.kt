package com.amnapp.milimeter.activities

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.databinding.ActivityPublishGroupBinding

class PublishGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityPublishGroupBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mDialog.show()로 시작

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublishGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog()
        binding.publishGroupBt.setOnClickListener {
            val groupCode = binding.newGroupCodeEt.text.toString()
            if(groupCode.isEmpty())
                return@setOnClickListener
            binding.publishGroupBt.isClickable = false
            mLoadingDialog.show()
            AccountManager().publishGroup(this,groupCode){resultMessage ->
                if(resultMessage == AccountManager.ERROR_NETWORK_NOT_CONNECTED)
                    showDialogMessage("네트워크 오류", "네트워크 연결을 확인해주세요"){}
                else if(resultMessage == AccountManager.RESULT_SUCCESS){
                    showDialogMessage("그룹개설", "새로운 그룹을 개설하였습니다"){
                        finish()
                    }
                }
                mLoadingDialog.dismiss()
                binding.publishGroupBt.isClickable = true
            }
        }
    }
    fun showDialogMessage(title: String, body: String, callBack: ()-> Unit) {//다이얼로그 메시지를 띄우는 함수
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
            callBack()
        }
        builder.show()
    }

    fun setProgressDialog() {
        val llPadding = 30
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        val progressBar = ProgressBar(this)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(this)
        tvText.text = "Loading ..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(ll)
        mLoadingDialog = builder.create()
    }
}