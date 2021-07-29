package com.amnapp.milimeter.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.UserData
import com.amnapp.milimeter.databinding.ActivitySignInBinding
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import com.google.android.material.R as MaterialR

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
                showDialogMessage("올바르지 않은 입력입니다", "5~20자리 글자로 작성해주세요"){}
                return@setOnClickListener
            }
            binding.checkUniqueBt.isClickable = false//연타방지
            mLoadingDialog.show()//로딩시작
            AccountManager().checkIfIdIsDuplicate(binding.idEt.text.toString()){ resultMessage: String, querySnapshot: QuerySnapshot ->
                if(resultMessage == AccountManager.ERROR_DUPLICATE_ID){
                    showDialogMessage("아이디 중복", "이미 가입된 아이디입니다"){}
                }
                else{
                    showDialogMessage("사용가능 아이디", "이 아이디로 가입하실 수 있습니다"){}
                    binding.checkUniqueBt.text = "사용가능"
                }
                mLoadingDialog.dismiss()//로딩완료
                binding.checkUniqueBt.isClickable = true//연타방지해제
            }
        }
        binding.idEt.addTextChangedListener {
            binding.checkUniqueBt.text = "중복체크"
            checkEditTextError()
        }
        binding.pwEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.pwCheckEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.nameEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.militaryIdEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.heightEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.weightEt.addTextChangedListener {
            checkEditTextError()
        }
        binding.birthDateLl.setOnClickListener {
            showDatePickerDialog()
        }

        binding.signInCv.setOnClickListener {
            signIn()
        }
        binding.backIb.setOnClickListener {
            finish()
        }
        binding.cancelIb.setOnClickListener {
            finish()
        }
    }

    private fun signIn() {
        if(binding.checkUniqueBt.text.toString() == "중복체크"){
            showDialogMessage("중복체크필요", "아이디 중복을 체크해주세요"){}
            return
        }
        else if(!checkEditTextError()){
            showDialogMessage("필수입력란 미기입", "비어있는 필수입력란을 기입해주세요"){}
            return
        }
        else if(!AccountManager().checkNetworkState(this)){
            showDialogMessage("네트워크 오류", "네트워크에 연결되어 있는 지 확인해주세요"){}
            return
        }

        binding.signInCv.isClickable = false//연타방지
        mLoadingDialog.show()//로딩시작
        val userData = UserData() //빈 유저데이터 객체 생성
        userData.id = if(binding.idEt.text.isNullOrEmpty()) null else binding.idEt.text.toString()
        userData.pw = if(binding.pwEt.text.isNullOrEmpty()) null else binding.pwEt.text.toString()
        userData.name = if(binding.nameEt.text.isNullOrEmpty()) null else binding.nameEt.text.toString()
        userData.birthDate = if(binding.birthDateTv.text.toString() == "생년월일 입력") null else binding.birthDateTv.text.toString()
        userData.militaryId  = if(binding.militaryIdEt.text.isNullOrEmpty()) null else binding.militaryIdEt.text.toString()
        userData.height  = if(binding.heightEt.text.isNullOrEmpty()) null else binding.heightEt.text.toString()
        userData.weight  = if(binding.weightEt.text.isNullOrEmpty()) null else binding.weightEt.text.toString()
        userData.bloodType  = binding.bloodTypeSp.selectedItemPosition
        userData.goalOfWeight = if(binding.goalOfWeightEt.text.isNullOrEmpty()) null else binding.goalOfWeightEt.text.toString()
        userData.goalOfTotalGrade = binding.goalOfTotalGradeSp.selectedItemPosition
        userData.goalOfLegTuckGrade = binding.goalOfLegTuckGradeSp.selectedItemPosition
        userData.goalOfShuttleRunGrade = binding.goalOfShuttleRunGradeSp.selectedItemPosition
        userData.goalOfFieldTrainingGrade = binding.goalOfFieldTrainingGradeSp.selectedItemPosition

        AccountManager().signIn(this, userData){resultMessage ->
            if(resultMessage == AccountManager.RESULT_SUCCESS){
                showDialogMessage("가입완료","회원가입이 완료되었습니다"){
                    finish()
                }
            }
            else if(resultMessage == AccountManager.ERROR_NETWORK_NOT_CONNECTED){
                showDialogMessage("네트워크 오류", "네트워크에 연결되어 있는 지 확인해주세요"){}
            }
            mLoadingDialog.dismiss()//로딩해제
            binding.signInCv.isClickable = true//연타방지해제
        }
    }

    private fun checkEditTextError(): Boolean{

        var valid = true

        if (binding.idEt.text.isNullOrEmpty()
            ||binding.idEt.text!!.length < 5
            ||binding.idEt.text!!.length > 20){
            binding.idEt.error = "5~20자리 글자로 작성해주세요"
            binding.idTl.helperText = binding.idEt.error
            valid = false
        }
        else{
            binding.idEt.error = null
            binding.idTl.helperText = null
        }

        if (binding.pwEt.text.isNullOrEmpty()
            ||binding.pwEt.text!!.length < 5
            ||binding.pwEt.text!!.length > 20){
            binding.pwEt.setError("5~20자리 글자로 작성해주세요",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.pwTl.helperText = binding.pwEt.error
            valid = false
        }
        else{
            binding.pwEt.error = null
            binding.pwTl.helperText = null
        }

        if (binding.pwCheckEt.text.isNullOrEmpty()
            ||binding.pwCheckEt.text!!.length < 5
            ||binding.pwCheckEt.text!!.length > 20){
            binding.pwCheckEt.setError("5~20자리 글자로 작성해주세요",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.pwCheckTl.helperText = binding.pwCheckEt.error
            valid = false
        }
        else if (binding.pwEt.text.toString() != binding.pwCheckEt.text.toString()){
            binding.pwCheckEt.setError("비밀번호 확인이 일치하지 않습니다",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.pwCheckTl.helperText = binding.pwCheckEt.error
            valid = false
        }
        else{
            binding.pwCheckEt.error = null
            binding.pwCheckTl.helperText = null
        }

        if (binding.nameEt.text.isNullOrEmpty()){
            binding.nameEt.setError("필수입력란 입니다",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.nameTl.helperText = binding.nameEt.error
            valid = false
        }
        else{
            binding.nameEt.error = null
            binding.nameTl.helperText = null
        }

        if (binding.militaryIdEt.text.isNullOrEmpty()){
            binding.militaryIdEt.setError("필수입력란 입니다",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.militaryIdTl.helperText = binding.militaryIdEt.error
            valid = false
        }
        else{
            binding.militaryIdEt.error = null
            binding.militaryIdTl.helperText = null
        }

        if (binding.heightEt.text.isNullOrEmpty()){
            binding.heightEt.setError("필수입력란 입니다",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.heightTl.helperText = binding.heightEt.error
            valid = false
        }
        else{
            binding.heightEt.error = null
            binding.heightTl.helperText = null
        }

        if (binding.weightEt.text.isNullOrEmpty()){
            binding.weightEt.setError("필수입력란 입니다",
                ContextCompat.getDrawable(this, MaterialR.drawable.mtrl_ic_error))
            binding.weightTl.helperText = binding.weightEt.error
            valid = false
        }
        else{
            binding.weightEt.error = null
            binding.weightTl.helperText = null
        }

        return valid
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