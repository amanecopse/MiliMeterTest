package com.amnapp.milimeter.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.amnapp.milimeter.*
import com.amnapp.milimeter.databinding.ActivityLoginBinding
import kotlin.reflect.KClass

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
        binding.publishGroupTv.setOnClickListener {
            val intent = Intent(this, PublishGroupActivity::class.java)
            startActivity(intent)
        }
        binding.inviteSubUserTv.setOnClickListener {
            val id = UserData.getInstance().id
            var groupCode = AccountManager.mGroupCode
            val hashedGroupCode = GroupMemberData.getInstance().hashedGroupCode
            val ac = AccountManager()
            if(ac.checkGroupCodeValid(id, groupCode, hashedGroupCode)){
                val intent = Intent(this, InviteSubUserActivity::class.java)
                startActivity(intent)
            }
            else{
                showEditTextDialogMessage("그룹코드를 입력해주세요") { input ->
                    groupCode = input
                    if(ac.checkGroupCodeValid(id, groupCode, hashedGroupCode)){
                        PreferenceManager().setGroupCode(this, groupCode!!)
                        val intent = Intent(this, InviteSubUserActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
        binding.adminPageTv.setOnClickListener {
            val id = UserData.getInstance().id
            var groupCode = AccountManager.mGroupCode
            val hashedGroupCode = GroupMemberData.getInstance().hashedGroupCode
            val ac = AccountManager()
            if(ac.checkGroupCodeValid(id, groupCode, hashedGroupCode)){
                val intent = Intent(this, AdminPageActivity::class.java)
                startActivity(intent)
            }
            else{
                showEditTextDialogMessage("그룹코드를 입력해주세요") { input ->
                    groupCode = input
                    if(ac.checkGroupCodeValid(id, groupCode, hashedGroupCode)){
                        PreferenceManager().setGroupCode(this, groupCode!!)
                        val intent = Intent(this, AdminPageActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
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
        val groupMemberData = GroupMemberData.getInstance()
        if (userData.login) {//이미 로그인한 상태
            binding.afterLoginLl.visibility = View.VISIBLE
            binding.beforeLoginLl.visibility = View.GONE
            binding.signInCv.visibility = View.GONE
            if(groupMemberData.indexHashCode == null){// null이면 그룹 미가입자라는 뜻
                binding.inviteSubUserTv.visibility = View.GONE
                binding.adminPageTv.visibility = View.GONE
                binding.publishGroupTv.visibility = View.VISIBLE
                binding.leaveGroupTv.visibility = View.GONE
            }
            else{
                binding.inviteSubUserTv.visibility = if(groupMemberData.admin) View.VISIBLE else View.GONE
                binding.adminPageTv.visibility = if(groupMemberData.admin) View.VISIBLE else View.GONE
                binding.publishGroupTv.visibility = View.GONE
                binding.leaveGroupTv.visibility = if(groupMemberData.admin) View.VISIBLE else View.GONE
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

    fun showEditTextDialogMessage(title: String, callBack: (input: String) -> Unit) {//다이얼로그 메시지를 띄우는 함수
        val editText = EditText(this)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setView(editText)
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int -> callBack(editText.text.toString())}
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    fun showTwoButtonDialogMessage(title: String, body: String, callBack: (Int) -> Unit) {//다이얼로그 메시지를 띄우는 함수
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int -> callBack(i)}
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> callBack(i)}
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

    companion object{
        const val GROUP_CODE_VALID = "유효한 그룹코드"
        const val GROUP_CODE_VALID_MASTER = "유효한 마스터 그룹코드"
        const val GROUP_CODE_INVALID = "유효하지 않은 그룹코드"
    }

    override fun onResume() {
        super.onResume()

        renewLoginUI()
    }
}