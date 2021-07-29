package com.amnapp.milimeter.viewModels

import android.app.ProgressDialog
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amnapp.milimeter.AccountManager
import com.amnapp.milimeter.GroupMemberData
import com.amnapp.milimeter.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AdminPageViewModel: ViewModel() {
    var parentName: MutableLiveData<String> = MutableLiveData<String>()//리스트의 부모가 누구인지를 표시하는 부분에 보이는 이름
    var userPathList: MutableLiveData<MutableList<UserData>> = MutableLiveData<MutableList<UserData>>()
    var groupMemberPathList: MutableLiveData<MutableList<GroupMemberData>> = MutableLiveData<MutableList<GroupMemberData>>()// 유저데이터의 리스트로 현재 경로를 나타냄
    var subUserList: MutableLiveData<MutableList<UserData>> = MutableLiveData<MutableList<UserData>>()// 현재 부모의 하위유저의 리스트
    var subGroupMemberList: MutableLiveData<MutableList<GroupMemberData>> = MutableLiveData<MutableList<GroupMemberData>>()
    val myUserData = UserData.getInstance()
    val myGroupMemberData = GroupMemberData.getInstance()

    init {
        parentName.value = myUserData.name
        userPathList.value = mutableListOf(myUserData)
        groupMemberPathList.value = mutableListOf(myGroupMemberData)
        CoroutineScope(Dispatchers.IO).launch{
            val am = AccountManager()
            val list = am.findSubGroupMemberListByIndex(myGroupMemberData.indexHashCode!!)
            val parsedList = am.findSubUserListBySubGroupMemberList(list)
            subGroupMemberList.postValue(list)
            subUserList.postValue(parsedList)
        }
    }

    fun downDirectory(newUserParent: UserData, newGroupMemberParent: GroupMemberData){
        parentName.value = if(newUserParent.name.isNullOrEmpty()) "___" else newUserParent.name
        val newUserPath = userPathList.value
        newUserPath?.add(newUserParent)
        userPathList.value = newUserPath // pathList.value.add()를 해도 참조주소가 바뀌는 게 아니기 때문에 이런 식으로 해줘야 변화가 관찰됨

        val newGroupPath = groupMemberPathList.value
        newGroupPath?.add(newGroupMemberParent)
        groupMemberPathList.value = newGroupPath

        CoroutineScope(Dispatchers.IO).launch{
            val am = AccountManager()

            val list = am.findSubGroupMemberListByIndex(newGroupMemberParent.indexHashCode!!)
            val parsedList = am.findSubUserListBySubGroupMemberList(list)
            subGroupMemberList.postValue(list)
            subUserList.postValue(parsedList)
        }
    }

    fun upDirectory(){
        val preUserParent = userPathList.value?.get(userPathList.value!!.size - 2)
        parentName.value = preUserParent?.name

        val preGroupMemberParent = groupMemberPathList.value?.get(groupMemberPathList.value!!.size - 2)

        val newUserPath = userPathList.value
        newUserPath?.removeAt(userPathList.value!!.size - 1)
        userPathList.value = newUserPath // pathList.value.add()를 해도 참조주소가 바뀌는 게 아니기 때문에 이런 식으로 해줘야 변화가 관찰됨

        val newGroupPath = groupMemberPathList.value
        newGroupPath?.removeAt(groupMemberPathList.value!!.size - 1)
        groupMemberPathList.value = newGroupPath

        CoroutineScope(Dispatchers.IO).launch{
            val am = AccountManager()

            val list = am.findSubGroupMemberListByIndex(preGroupMemberParent!!.indexHashCode!!)
            val parsedList = am.findSubUserListBySubGroupMemberList(list)
            subGroupMemberList.postValue(list)
            subUserList.postValue(parsedList)
        }
    }

    fun reloadItems(callBack: ()->Unit){
        CoroutineScope(Dispatchers.IO).launch{
            val am = AccountManager()

            val list = am.findSubGroupMemberListByIndex(groupMemberPathList.value?.last()?.indexHashCode!!)
            val parsedList = am.findSubUserListBySubGroupMemberList(list)
            subGroupMemberList.postValue(list)
            subUserList.postValue(parsedList)
            callBack()
        }
    }
}