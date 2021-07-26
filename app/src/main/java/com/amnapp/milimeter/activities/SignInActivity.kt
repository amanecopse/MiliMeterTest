package com.amnapp.milimeter.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.R
import com.google.android.material.R as MaterialR
import com.amnapp.milimeter.databinding.ActivityLoginBinding
import com.amnapp.milimeter.databinding.ActivitySignInBinding
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mDialog.show()로 시작

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog()

        binding.checkUniqueBt.setOnClickListener {
            if (binding.idEt.text.isNullOrEmpty()
                ||binding.idEt.text!!.length < 5
                ||binding.idEt.text!!.length > 20) {
                showDialogMessage("올바르지 않은 입력입니다", "5~20자리 글자로 작성해주세요")
                return@setOnClickListener
            }
            mLoadingDialog.show()//로딩시작
            AccountManager().findUserDataById(binding.idEt.text.toString()){ resultMessage: String, querySnapshot: QuerySnapshot ->
                if(resultMessage == AccountManager.ERROR_DUPLICATE_ID){
                    showDialogMessage("아이디 중복", "이미 가입된 아이디입니다")
                }
                else{
                    showDialogMessage("사용가능 아이디", "이 아이디로 가입하실 수 있습니다")
                    binding.checkUniqueBt.text = "사용가능"
                }
                mLoadingDialog.dismiss()//로딩완료
            }
        }
        binding.idEt.addTextChangedListener {
            binding.checkUniqueBt.text = "중복체크"
            if (binding.idEt.text.isNullOrEmpty()
                ||binding.idEt.text!!.length < 5
                ||binding.idEt.text!!.length > 20)
                binding.idEt.error = "5~20자리 글자로 작성해주세요"
            else binding.idEt.error = null
        }

        binding.pwEt.addTextChangedListener {
            if (binding.pwEt.text.isNullOrEmpty()
                ||binding.pwEt.text!!.length < 5
                ||binding.pwEt.text!!.length > 20)
                binding.pwEt.setError("5~20자리 글자로 작성해주세요",
                    ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            else binding.pwEt.error = null

            if (binding.pwEt.text.toString() != binding.pwCheckEt.text.toString()){
                binding.pwCheckEt.setError("비밀번호 확인이 일치하지 않습니다",
                    ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            }
            else binding.pwCheckEt.error = null
        }
        binding.pwCheckEt.addTextChangedListener {
            if (binding.pwCheckEt.text.isNullOrEmpty()
                ||binding.pwCheckEt.text!!.length < 5
                ||binding.pwCheckEt.text!!.length > 20)
                binding.pwCheckEt.setError("5~20자리 글자로 작성해주세요",
                    ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            else if (binding.pwEt.text.toString() != binding.pwCheckEt.text.toString()){
                binding.pwCheckEt.setError("비밀번호 확인이 일치하지 않습니다",
                    ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            }
            else binding.pwCheckEt.error = null
        }

        binding.birthDateLl.setOnClickListener {
            showDatePickerDialog()
        }
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

    private fun showDatePickerDialog() {
        val callBack = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.birthDateTv.text = ""+year+"."+ String.format("%02d", month)+"."+String.format("%02d", dayOfMonth)
        }
        val year = SimpleDateFormat("yyyy").format(Date()).toInt()
        val month = SimpleDateFormat("MM").format(Date()).toInt()
        val day = SimpleDateFormat("dd").format(Date()).toInt()
        DatePickerDialog(this, callBack,year-20,month,day).show()
    }
}