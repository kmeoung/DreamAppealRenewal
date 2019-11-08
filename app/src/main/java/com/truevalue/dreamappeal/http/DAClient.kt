package com.truevalue.dreamappeal.http

import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object DAClient {

    val SUCCESS = "SUCCESS"
    val VALIDATE_EMAIL = "VALIDATE_EMAIL"
    val API_NOT_FOUND = "API_NOT_FOUND"
    val NOT_GRANTED = "NOT_GRANTED"
    val USER_NOT_FOUND = "USER_NOT_FOUND"
    val EMAIL_NOT_FOUND = "EMAIL_NOT_FOUND"
    val PASSWORD_NOT_MATCHED = "PASSWORD_NOT_MATCHED"
    val DUPLICATED_EMAIL = "DUPLICATED_EMAIL"

    fun getHttpHeader(token: String?): DAHttpHeader {
        val header = DAHttpHeader()
        if (!token.isNullOrEmpty()) {
            header.put("Authorization", "Bearer $token")
        }
        return header
    }

    fun getHttpHeader(): DAHttpHeader {
        val header = DAHttpHeader()
        val token = Comm_Prefs.getToken()
        if (!token.isNullOrEmpty()) {
            header.put("Authorization", "Bearer $token")
        }
        return header
    }

    /**
     * GET
     * 내 유저 정보 가져오기
     */
    fun getMyUserData(callback: DAHttpCallback) {
        val header = getHttpHeader()

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_USERS,
            header,
            null,
            callback
        )
    }

    /**
     * GET
     * 다른 유저 정보 가져오기
     */
    fun getOtherUserData(
        profile_idx: Int
        , callback: DAHttpCallback
    ) {
        val header = getHttpHeader()
        val url = Comm_Param.URL_USERS_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            profile_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            header,
            null,
            callback
        )
    }

    /**
     * POST
     * 회원가입
     */
    fun register(
        email: String,
        password: String,
        name: String,
        gender: Boolean,
        birth: Date,
        callback: DAHttpCallback
    ) {
//        {
//            email: string
//            password: string
//            name: string
//            gender: string // 짝수 여자 홀수 남자
//            birth: string 'YYYY-MM-DD'
//        }

        val params = DAHttpParams()
        params.put("email", email)
        params.put("password", password)
        params.put("name", name)
        var numGender: Int
        if (gender) numGender = 2
        else numGender = 1
        params.put("gender", numGender)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        params.put("birth", sdf.format(birth))

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_SIGNUP,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 이메일 인증코드 전송
     */
    fun sendEmail(
        email: String,
        name: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("email", email)
        params.put("name", name)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_SIGNUP_EMAIL_CHECK,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 이메일 인증코드 확인
     */
    fun emailVerify(
        email: String,
        verify_code: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("email", email)
        params.put("verify_code", verify_code)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_SIGNUP_EMAIL_VERIFY,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 로그인
     */
    fun login(
        email: String,
        password: String,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        params.put("email", email)
        params.put("password", password)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_TOKENS,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 비밀번호 찾기
     * 인증번호 보내기
     */
    fun recoverInitiate(
        email: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("email", email)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_RECOVER_INITIATE,
            null,
            params,
            callback
        )

    }


    /**
     * POST
     * 비밀번호 찾기
     * 인증번호 확인
     */
    fun recoverCode(
        email: String,
        verify_code: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("verify_code", verify_code)
        params.put("email", email)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_RECOVER_CODE,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 비밀번호 재설정
     */
    fun recoverPassword(
        email: String,
        verify_code: String,
        password: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("email", email)
        params.put("verify_code", verify_code)
        params.put("password", password)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_RECOVER_PASSWORD,
            null,
            params,
            callback
        )
    }

    /**
     * POST
     * 내 꿈 소개 등록
     */
    fun addProfiles(
        job: String,
        value_style: String,
        description: String,
        description_spec: JSONObject,
        meritNmotive: String,
        callback: DAHttpCallback
    ) {

        val header = getHttpHeader()
        val params = DAHttpParams()
        params.put("job", job)
        params.put("value_style", value_style)
        params.put("description", description)
        params.put("description_spec", description_spec)
        params.put("meritNmotive", meritNmotive)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_PROFILES,
            header,
            params,
            callback
        )
    }

    /**
     * GET
     * 내 꿈 소개 조회
     */
    fun getProfiles(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        if (cur_profile_index > -1 && !Comm_Prefs.getToken().isNullOrEmpty()) {
            val url = Comm_Param.URL_PROFILES_CUR_PROFILE_IDX.replace(
                Comm_Param.PROFILE_INDEX,
                cur_profile_index.toString()
            )
            val header = getHttpHeader()

            BaseOkhttpClient.request(
                HttpType.GET,
                url,
                header,
                null,
                callback
            )
        }
    }

    /**
     * PATCH
     * 내 꿈 소개 업데이트
     */
    fun updateProfiles(
        job: String?,
        value_style: String?,
        description: String?,
        description_spec: JSONArray?,
        meritNmotive: String?,
        callback: DAHttpCallback
    ) {
        val header = getHttpHeader()
        val params = DAHttpParams()

        if (!job.isNullOrEmpty()) params.put("job", job)
        if (!value_style.isNullOrEmpty()) params.put("value_style", value_style)
        if (!description.isNullOrEmpty()) params.put("description", description)
        if (description_spec != null && description_spec.length() > 0) params.put(
            "description_spec",
            description_spec
        )
        if (!meritNmotive.isNullOrEmpty()) params.put("meritNmotive", meritNmotive)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_PROFILES,
            header,
            params,
            callback
        )
    }

    /**
     * DELETE
     * 내 꿈 프로필 삭제
     */
    fun deleteProfiles(profile_idx: Int,
                       callback: DAHttpCallback) {

        val url = Comm_Param.URL_PROFILES_CUR_PROFILE_IDX.replace(Comm_Param.PROFILE_INDEX,profile_idx.toString())

        val header = getHttpHeader()
        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            header,
            null,
            callback
        )
    }

    /**
     * GET
     * 꿈 목록 조회
     */
    fun profilesList(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        if (cur_profile_index > -1 && !Comm_Prefs.getToken().isNullOrEmpty()) {
            val url = Comm_Param.URL_PROFILES_PROFILE_IDX_LIST.replace(
                Comm_Param.PROFILE_INDEX,
                cur_profile_index.toString()
            )
            val header = getHttpHeader()

            BaseOkhttpClient.request(
                HttpType.GET,
                url,
                header,
                null,
                callback
            )
        }
    }

    /**
     * PATCH
     * 꿈 목록 순서 변경
     */
    fun updateProfilesList() {
        // todo : 생각이 필요함
    }


}