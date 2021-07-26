package com.amnapp.milimeter.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.amnapp.milimeter.PreferenceManager
import com.amnapp.milimeter.R
import com.amnapp.milimeter.UserData
import com.amnapp.milimeter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mDialog.show()로 시작

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog() // 로딩다이얼로그 세팅

        renewLoginUI()//로그인이 되었는 지 아닌지에 따라 화면 결정

        binding.loginBt.setOnClickListener {
            binding.afterLoginLl.visibility = View.VISIBLE
            binding.beforeLoginLl.visibility = View.GONE
            binding.signInCv.visibility = View.GONE
        }
        binding.logoutBt.setOnClickListener {

        }
        binding.signInCv.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun renewLoginUI() {
        if (UserData.getInstance().login) {//이미 로그인한 상태
            binding.afterLoginLl.visibility = View.VISIBLE
            binding.beforeLoginLl.visibility = View.GONE
            binding.signInCv.visibility = View.GONE
        } else {//아직 안 한 상태
            binding.afterLoginLl.visibility = View.GONE
            binding.beforeLoginLl.visibility = View.VISIBLE
            binding.signInCv.visibility = View.VISIBLE

            loadLoginData()
        }
    }

    fun loadLoginData() {
        val pm = PreferenceManager()
        val loginDataArray = pm.getLoginData(this)// 로그인 데이터를 로컬저장소에서 로드함
        binding.idEt.setText(loginDataArray[0])
        binding.pwEt.setText(loginDataArray[1])
    }

    fun showDialogMessage(title: String, body: String) {//다이얼로그 메시지를 띄우는 함수
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int -> }
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