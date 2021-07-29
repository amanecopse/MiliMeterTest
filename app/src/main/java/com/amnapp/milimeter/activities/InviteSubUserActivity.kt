package com.amnapp.milimeter.activities

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.R
import com.amnapp.milimeter.databinding.ActivityInviteSubUserBinding

class InviteSubUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityInviteSubUserBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mDialog.show()로 시작

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteSubUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog()

        binding.inviteBt.setOnClickListener {
            binding.inviteBt.isClickable = false
            mLoadingDialog.show()
            AccountManager().inviteSubUser(
                binding.subUserIdEt.text.toString(),
                binding.adminCb.isChecked
            ){resultMessage ->
                when(resultMessage){
                    AccountManager.ERROR_NOT_FOUND_ID->{
                        showDialogMessage("존재하지 않는 아이디", "해당 아이디가 존재하지 않습니다"){}
                    }
                    AccountManager.ERROR_GROUPED_ID->{
                        showDialogMessage("이미 그룹에 가입된 아이디", "해당 아이디가 이미 그룹에 가입되어 있습니다"){}
                    }
                    AccountManager.RESULT_SUCCESS->{
                        showDialogMessage("하위유저 가입성공", "하위유저가 그룹에 가입되었습니다"){}
                    }
                }
                mLoadingDialog.dismiss()
                binding.inviteBt.isClickable = true
            }
        }
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

    fun showDialogMessage(title: String, body: String, callBack: ()-> Unit) {//다이얼로그 메시지를 띄우는 함수
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
            callBack()
        }
        builder.show()
    }
}