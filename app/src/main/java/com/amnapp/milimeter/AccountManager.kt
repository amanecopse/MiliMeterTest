package com.amnapp.milimeter

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.nio.charset.Charset
import java.security.MessageDigest

class AccountManager {

    fun signIn(userData: UserData, callBack: (resultMessage: String) -> Unit){
        Firebase.firestore.collection(USERS).document(userData.id!!)
            .set(userData)
            .addOnSuccessListener {
                callBack(RESULT_SUCCESS)
            }
    }

    fun login(id: String, pw: String, callBack: (resultMessage: String) -> Unit){
        findUserDataById(id){resultMessage, querySnapShot ->
            if(resultMessage == RESULT_FAILURE)
                callBack(ERROR_NOT_FOUND_ID)// 아이디가 존재하지 않음
            else if(resultMessage == RESULT_SUCCESS){
                val doc = querySnapShot.documents[0]
                val userData = doc.toObject<UserData>()
                if (userData != null) {
                    if(userData.pw != pw){
                        callBack(ERROR_WRONG_PASSWORD)
                        return@findUserDataById
                    }// 비밀번호가 달라 실패
                    userData.login = true //접속중으로 처리
                    UserData.setInstance(userData) // 서버에서 받은 계정정보를 등록
                    Firebase.firestore.collection(USERS).document(doc.reference.id)
                        .set(userData) // 서버에 접속중임을 알림
                        .addOnSuccessListener {
                            callBack(RESULT_SUCCESS)
                        }
                }
            }
        }
    }

    fun logout(){
        val userData = UserData.getInstance()
        val docIndex = if(userData.indexHashCode == null) userData.id else userData.indexHashCode
        userData.login = false
        Firebase.firestore.collection(USERS).document(docIndex!!)
            .set(userData) // 서버에 로그아웃함을 알림
    }

    fun autoLogin(context: Context, callBack: () -> Unit){
        val pm = PreferenceManager()
        if(pm.isAutoLoginEnable(context)){
            val loginData = pm.getLoginData(context)
            login(loginData[0].toString(), loginData[1].toString()){resultMessage ->
                if(resultMessage == RESULT_SUCCESS)
                    callBack()
            }
        }
    }

    fun checkIfIdIsDuplicate(id: String, callBack: (resultMessage: String, querySnapShot: QuerySnapshot) -> Unit){
        findUserDataById(id){resultMessage, querySnapShot ->
            if(resultMessage == RESULT_FAILURE){
                callBack(RESULT_SUCCESS, querySnapShot)
            }
            else{
                callBack(ERROR_DUPLICATE_ID, querySnapShot)
            }
        }
    }

    fun findUserDataById(id: String, callBack: (resultMessage: String, querySnapShot: QuerySnapshot) -> Unit){
        Firebase.firestore.collection(USERS).whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callBack(RESULT_FAILURE, it)
                }
                else{
                    callBack(RESULT_SUCCESS, it)
                }
            }
    }

    fun hash(text: String): String? {
        val sha = SHA256()
        return sha.encrypt(text)

        //초대해시코드는 myUd.id+"!@#"+inviteCode+"!@#"+mGroupCode
        //자식의 인덱스 새로 만들 때는 -> myIndexHashCode+"!@#"+mGroupCode+"!@#"+i 순서유의
    }

    fun checkNetworkState(context: Context): Boolean {//인터넷 상태를 확인하는 함수
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    companion object{
        var mGroupCode: String? = null //트리순회에 필요하므로 로그인 시 정적으로 입력할 것
        const val TAG = "AccountManager"
        const val USERS = "users"
        const val ERROR_NOT_FOUND_ID = "아이디없는 오류"
        const val ERROR_DUPLICATE_ID = "아이디 중복 오류"
        const val ERROR_WRONG_INFO = "입력정보가 다름"
        const val ERROR_WRONG_PASSWORD = "비밀번호가 다름"
        const val RESULT_SUCCESS = "성공"
        const val RESULT_FAILURE = "실패"

    }
}

class SHA256(){
    fun encrypt(text: String): String? {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(text.toByteArray(Charset.defaultCharset()))

        return bytesToHex(md.digest())
    }

    private fun bytesToHex(bytes: ByteArray): String? {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }
}