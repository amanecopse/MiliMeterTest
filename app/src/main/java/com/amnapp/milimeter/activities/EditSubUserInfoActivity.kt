package com.amnapp.milimeter.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.GroupMemberData
import com.amnapp.milimeter.UserData
import com.amnapp.milimeter.databinding.ActivityEditSubUserInfoBinding
import java.text.SimpleDateFormat
import java.util.*

class EditSubUserInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditSubUserInfoBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mLoadingDialog.show()로 시작
    lateinit var mSubUserData: UserData
    lateinit var mSubGroupMemberData: GroupMemberData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSubUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog()// 로딩다이얼로그 설치
        mSubUserData = UserData.mTmpUserData!!
        mSubGroupMemberData = GroupMemberData.mTmpGroupMemberData!!
        if (mSubUserData != null) {
            binding.nameTv.text = mSubUserData.name
            binding.idTv.text = mSubUserData.id
            binding.pwEt.setText(mSubUserData.pw)
            mSubUserData.birthDate?.let { binding.birthDateTv.setText(it.toString()) }
            mSubUserData.militaryId?.let { binding.militaryIdEt.setText(it.toString()) }
            mSubUserData.height?.let { binding.heightEt.setText(it.toString()) }
            mSubUserData.weight?.let { binding.weightEt.setText(it.toString()) }
            mSubUserData.bloodType?.let { binding.bloodTypeSp.setSelection(it) }
            mSubUserData.goalOfWeight?.let { binding.goalOfWeightEt.setText(it.toString()) }
            mSubUserData.goalOfTotalGrade?.let { binding.goalOfTotalRankSp.setSelection(it) }
            mSubUserData.goalOfLegTuckGrade?.let { binding.goalOfLegTuckRankSp.setSelection(it) }
            mSubUserData.goalOfShuttleRunGrade?.let { binding.goalOfShuttleRunRankSp.setSelection(it) }
            mSubUserData.goalOfFieldTrainingGrade?.let { binding.goalOfFieldTrainingRankSp.setSelection(it) }
        }

        binding.confirmCv.setOnClickListener{
            if(
                binding.nameTv.text.isNullOrEmpty()
                ||binding.pwEt.text.isNullOrEmpty()
                ||binding.militaryIdEt.text.isNullOrEmpty()
                ||binding.heightEt.text.isNullOrEmpty()
                ||binding.weightEt.text.isNullOrEmpty()
                ||binding.pwEt.text.isNullOrEmpty()
            ){
                showDialogMessage("입력 오류", "필수 입력란이 비어있습니다")
                return@setOnClickListener
            }
            mSubUserData.pw = binding.pwEt.text.toString()
            mSubUserData.birthDate = if(binding.birthDateTv.text.isNullOrEmpty()) null else binding.birthDateTv.text.toString()
            mSubUserData.militaryId = binding.militaryIdEt.text.toString()
            mSubUserData.height = binding.heightEt.text.toString()
            mSubUserData.weight = binding.weightEt.text.toString()
            mSubUserData.bloodType = binding.bloodTypeSp.selectedItemPosition
            mSubUserData.goalOfWeight = if(binding.goalOfWeightEt.text.isNullOrEmpty()) null else binding.goalOfWeightEt.text.toString()
            mSubUserData.goalOfTotalGrade = binding.goalOfTotalRankSp.selectedItemPosition
            mSubUserData.goalOfLegTuckGrade = binding.goalOfLegTuckRankSp.selectedItemPosition
            mSubUserData.goalOfShuttleRunGrade = binding.goalOfShuttleRunRankSp.selectedItemPosition
            mSubUserData.goalOfFieldTrainingGrade = binding.goalOfFieldTrainingRankSp.selectedItemPosition

            mLoadingDialog.show() //로딩시작
            AccountManager().uploadUserData(mSubUserData){message ->
                if(message == AccountManager.RESULT_SUCCESS)
                    showDialogMessage("수정 완료", "하위회원의 정보가 수정되었습니다")
                mLoadingDialog.dismiss()
            }
        }
        binding.birthDateLl.setOnClickListener {
            showDatePickerDialog()
        }
        binding.withdrawBt.setOnClickListener {
            AccountManager().leaveGroup(mSubGroupMemberData.indexHashCode!!){
                showDialogMessage("탈퇴완료", "해당 하위유저가 탈퇴되었습니다")
            }
        }

        binding.cancelIb.setOnClickListener{
            finish()
        }
        binding.backIb.setOnClickListener{
            finish()
        }

    }

    fun showDialogMessage(title:String, body:String) {
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