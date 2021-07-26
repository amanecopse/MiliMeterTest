package com.amnapp.milimeter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.nio.charset.Charset
import java.security.MessageDigest

class AccountManager {
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
        const val ERROR_WRONG_INFO = "입력정보가 다름"
        const val LOGIN_SUCCESS = "로그인 성공"
        const val UPLOAD_SUCCESS = "업로드 성공"

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