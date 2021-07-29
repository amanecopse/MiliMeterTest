package com.amnapp.milimeter

data class UserData(var id: String? = null){
    var pw: String? = null
    var login: Boolean = false
    //아래는 프로필 정보
    var name: String? = null
    var birthDate: String? = null
    var militaryId: String? = null // 입력받을 때 군번에서 '-'없이 입력받을 것
    var height: String? = null
    var weight: String? = null
    var bloodType: Int? = null
    //목표 정보
    var goalOfWeight: String? = null
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
            mUserData!!.login = userData.login
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

data class GroupMemberData(var indexHashCode: String? = null){
    var hashedGroupCode: String? = null
    var admin: Boolean = false
    var childCount: Int = 0
    var id: String? = null

    companion object{
        private var mGroupMemberData: GroupMemberData? = null
        var mTmpGroupMemberData: GroupMemberData? = null

        fun setInstance(groupMemberData: GroupMemberData){
            mGroupMemberData = getInstance()
            mGroupMemberData!!.hashedGroupCode = groupMemberData.hashedGroupCode
            mGroupMemberData!!.admin = groupMemberData.admin
            mGroupMemberData!!.childCount = groupMemberData.childCount
            mGroupMemberData!!.indexHashCode = groupMemberData.indexHashCode
            mGroupMemberData!!.id = groupMemberData.id
        }
        fun getInstance(): GroupMemberData{
            return mGroupMemberData ?: GroupMemberData().also{
                mGroupMemberData = it
            }
        }
    }
}
