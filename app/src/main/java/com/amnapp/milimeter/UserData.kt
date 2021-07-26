package com.amnapp.milimeter

data class UserData(var id: String? = null){
    var pw: String? = null
    var childCount: Int = 0
    var hashedGroupCode: String? = null
    var inviteHashCode: String? = null
    var indexHashCode: String? = null
    var login: Boolean = false
    var isAdmin: Boolean = false
    //아래는 프로필 정보
    var name: String? = null
    var birthDate: Int? = null
    var militaryId: Int? = null // 입력받을 때 군번에서 '-'없이 입력받을 것
    var height: Int? = null
    var weight: Int? = null
    var bloodType: Int? = null
    //목표 정보
    var goalOfWeight: Int? = null
    var goalOfTotalGrade: Int? = null
    var goalOfLegTuckGrade: Int? = null
    var goalOfShuttleRunGrade: Int? = null
    var goalOfFieldTrainingGrade: Int? = null

    companion object{
        private var mUserData: UserData? = null
        var mTmpUserData: UserData? = null

        fun setInstance(userData: UserData){
            mUserData = getInstance()
            mUserData!!.id = userData.id
            mUserData!!.pw = userData.pw
            mUserData!!.childCount = userData.childCount
            mUserData!!.hashedGroupCode = userData.hashedGroupCode
            mUserData!!.inviteHashCode = userData.inviteHashCode
            mUserData!!.indexHashCode = userData.indexHashCode
            mUserData!!.login = userData.login
            mUserData!!.isAdmin = userData.isAdmin
            mUserData!!.name = userData.name
            mUserData!!.birthDate = userData.birthDate
            mUserData!!.militaryId = userData.militaryId
            mUserData!!.height = userData.height
            mUserData!!.weight = userData.weight
            mUserData!!.bloodType = userData.bloodType
            mUserData!!.goalOfWeight = userData.goalOfWeight
            mUserData!!.goalOfTotalGrade = userData.goalOfTotalGrade
            mUserData!!.goalOfLegTuckGrade = userData.goalOfLegTuckGrade
            mUserData!!.goalOfShuttleRunGrade = userData.goalOfShuttleRunGrade
            mUserData!!.goalOfFieldTrainingGrade = userData.goalOfFieldTrainingGrade
        }
        fun getInstance(): UserData{
            return mUserData ?: UserData().also{
                mUserData = it
            }
        }
    }
}
