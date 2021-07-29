package com.amnapp.milimeter.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amnapp.milimeter.GroupMemberData
import com.amnapp.milimeter.UserData
import com.amnapp.milimeter.databinding.ActivityAdminPageBinding
import com.amnapp.milimeter.recyclerViews.AdminPageRecyclerAdapter
import com.amnapp.milimeter.viewModels.AdminPageViewModel

class AdminPageActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminPageBinding
    lateinit var mLoadingDialog: AlertDialog//로딩화면임. setProgressDialog()를 실행후 mLoadingDialog.show()로 시작
    private val viewModel: AdminPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setProgressDialog()// 로딩다이얼로그 세팅
        mLoadingDialog.show()//로딩 시작

        binding.pathEt.setSelection(binding.pathEt.length())//항상 마지막 경로로 커서가 오도록 설정
        binding.subUserListRv.layoutManager = LinearLayoutManager(this)// 리사이클러뷰 리스트에 레이아웃 매니저를 설정

        viewModel.parentName.observe(this, Observer { // 현재부모가 디렉토리 이동으로 바뀌면 그 이름으로 UI갱신
            binding.parentNameTv.text = it
        })
        viewModel.userPathList.observe(this, Observer {
            var path = ""
            for (i in it){
                path += "/"+i.name
            }
            binding.pathEt.setText(path)
        })
        viewModel.subUserList.observe(this, Observer {
            if(it == null) return@Observer
            val adminPageRecyclerAdapter = AdminPageRecyclerAdapter(it)
            //아이템 내용 클릭 리스너 등록
            adminPageRecyclerAdapter.setItemOnClickListener(object: AdminPageRecyclerAdapter.OnItemClickListener{
                override fun onClicked(v: View, pos: Int) {
                    mLoadingDialog.show() // 로딩 시작
                    viewModel.downDirectory(it[pos], viewModel.subGroupMemberList.value!![pos])
                }
            })
            //아이템 편집 클릭 리스너 등록
            adminPageRecyclerAdapter.setEditOnClickListener(object: AdminPageRecyclerAdapter.OnEditClickListener{
                override fun onClicked(v: View, pos: Int) {
                    UserData.mTmpUserData = it[pos]
                    GroupMemberData.mTmpGroupMemberData = viewModel.subGroupMemberList.value!![pos]
                    val intent = Intent(this@AdminPageActivity, EditSubUserInfoActivity::class.java)
                    startActivity(intent)
                }

            })
            binding.subUserListRv.adapter = adminPageRecyclerAdapter//로드된 데이터를 담은 어댑터를 뷰에 탑재
            mLoadingDialog.dismiss() //로딩 해제
        })

        binding.upDirectoryIv.setOnClickListener {
            if (viewModel.userPathList.value?.size == 1) return@setOnClickListener // 최상위 경로면 작동 안한다
            mLoadingDialog.show()//로딩 시작
            viewModel.upDirectory()
        }
        binding.cancelIb.setOnClickListener {
            finish()
        }
        binding.backIb.setOnClickListener {
            finish()
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

    override fun onResume() {
        super.onResume()

        mLoadingDialog.show()
        viewModel.reloadItems{
            mLoadingDialog.dismiss()
        }
    }
}