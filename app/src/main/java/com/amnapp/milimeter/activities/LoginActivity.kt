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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.amnapp.milimeter.AccountManager
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

        //TODO(병합할 때 Home에서 자동로그인 문제생길 것임)
    }

    private fun initUI() {
        setProgressDialog() // 로딩다이얼로그 세팅

        renewLoginUI()//로그인이 되었는 지 아닌지에 따라 화면 결정

        binding.loginBt.setOnClickListener {
            binding.loginBt.isClickable = false//연타방지
            mLoadingDialog.show()//로딩시작
            val id = binding.idEt.text.toString()
            val pw = binding.pwEt.text.toString()
            val autoLoginEnable = binding.autoLoginCb.isChecked
            setLoginData(id, pw, autoLoginEnable)
            AccountManager().login(id, pw){resultMessage ->
                when(resultMessage){
                    AccountManager.ERROR_NOT_FOUND_ID ->{
                        showDialogMessage("존재하지 않는 아이디", "다시 입력해주세요")
                    }
                    AccountManager.ERROR_WRONG_PASSWORD ->{
                        showDialogMessage("비밀번호 불일치", "다시 입력해주세요")
                    }
                    AccountManager.RESULT_SUCCESS ->{
                        Toast.makeText(this, "로그인", Toast.LENGTH_LONG).show()
                        renewLoginUI()
                    }
                }
                mLoadingDialog.dismiss()//로딩해제
                binding.loginBt.isClickable = true//연타방지해제
            }
        }
        binding.logoutBt.setOnClickListener {
            AccountManager().logout()
            renewLoginUI()
        }
        binding.signInCv.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.backIb.setOnClickListener {
            finish()
        }
        binding.cancelIb.setOnClickListener {
            finish()
        }
    }

    private fun setLoginData(id: String, pw: String, autoLoginEnable: Boolean) {
        PreferenceManager().setLoginData(this, id, pw, autoLoginEnable)
    }

    private fun renewLoginUI() {
        val userData = UserData.getInstance()
        if (userData.login) {//이미 로그인한 상태
            binding.afterLoginLl.visibility = View.VISIBLE
            binding.beforeLoginLl.visibility = View.GONE
            binding.signInCv.visibility = View.GONE
            if(userData.indexHashCode == null){// null이면 그룹 미가입자라는 뜻
                binding.inviteSubUserTv.visibility = View.GONE
                binding.adminPageTv.visibility = View.GONE
                binding.joinGroupTv.visibility = View.VISIBLE
                binding.leaveGroupTv.visibility = View.GONE
            }
            else{
                binding.inviteSubUserTv.visibility = View.VISIBLE
                binding.adminPageTv.visibility = if(userData.isAdmin) View.VISIBLE else View.GONE
                binding.joinGroupTv.visibility = View.GONE
                binding.leaveGroupTv.visibility = View.VISIBLE
            }
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
        binding.autoLoginCb.isChecked = pm.isAutoLoginEnable(this)
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