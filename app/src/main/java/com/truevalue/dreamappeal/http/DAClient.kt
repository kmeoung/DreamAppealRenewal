package com.truevalue.dreamappeal.http

import com.google.gson.Gson
import com.truevalue.dreamappeal.bean.BeanProfileUser
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
     * POST
     * 유저 그룹 추가하기
     */
    fun addUsersGroup(
        groupName: String,
        position: String,
        Class: Int,
        start_date: String,
        end_date: String,
        description: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("groupName", groupName)
        params.put("position", position)
        params.put("class", Class)
        params.put("start_date", start_date)
        params.put("end_date", end_date)
        params.put("description", description)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_GROUP,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 유저 그룹 수정하기
     */
    fun editUsersGroup(
        group_idx: Int,
        groupName: String,
        position: String,
        Class: Int,
        start_date: String,
        end_date: String,
        description: String,
        callback: DAHttpCallback
    ) {

        val url =
            Comm_Param.URL_USERS_GROUP_IDX.replace(Comm_Param.GROUP_INDEX, group_idx.toString())
        val params = DAHttpParams()
        params.put("groupName", groupName)
        params.put("position", position)
        params.put("class", Class)
        params.put("start_date", start_date)
        params.put("end_date", end_date)
        params.put("description", description)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * DELETE
     * 유저 그룹 삭제
     */
    fun deleteUsersGroup(
        group_idx: Int,
        callback: DAHttpCallback
    ) {

        val url =
            Comm_Param.URL_USERS_GROUP_IDX.replace(Comm_Param.GROUP_INDEX, group_idx.toString())
        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 내 유저 정보 업데이트
     */
    fun updateMyUserData(
        bean: BeanProfileUser,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("name", bean.name)
        params.put("nickname", bean.nickname)
        params.put("gender", bean.gender)
        params.put("address", bean.address)

        val privates = Gson().toJson(bean.private)
        params.put("privates", privates)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_USERS,
            getHttpHeader(),
            params,
            callback
        )


    }

    /**
     * GET
     * 다른 유저 정보 가져오기
     */
    fun getAnotherUserData(
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
    fun deleteProfiles(
        profile_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_PROFILES_CUR_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            profile_idx.toString()
        )

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

    /**
     * GET
     * 주요 성과 페이지 조회
     */
    fun achievementPostMain(
        cur_profile_index: Int,
        cur_view_page: Int,
        callback: DAHttpCallback
    ) {
        var url = Comm_Param.URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX_POST_SIZE.replace(
            Comm_Param.POST_SIZE,
            cur_view_page.toString()
        )
        url = url.replace(Comm_Param.PROFILE_INDEX, cur_profile_index.toString())
        val header = getHttpHeader()

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            header,
            null,
            callback
        )
    }

    /**
     * GET
     * 실현 성과 상세 조회
     */
    fun achievementPostDetail(
        post_index: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_ACHIEVEMENT_POSTS_POST_IDX.replace(
                Comm_Param.POST_INDEX,
                post_index.toString()
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

    /**
     * PATCH
     * 실현 성과 수정
     */
    fun editachievementPost(
        post_index: Int,
        title: String,
        content: String,
        thumbnail_image: String,
        tags: ArrayList<String>,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_ACHIEVEMENT_POSTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_index.toString()
        )
        val header = getHttpHeader()
        val params = DAHttpParams()
        params.put("title", title)
        params.put("content", content)
        params.put("thumbnail_image", thumbnail_image)
        // todo : 태그 확인 필요


        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            header,
            null,
            callback
        )
    }

    /**
     * DELETE
     * 실현 성과 삭제
     */
    fun deleteachievementPost(
        post_index: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_ACHIEVEMENT_POSTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_index.toString()
        )
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
     * POST
     * 대표 성과 등록
     */
    fun uploadBestPost(
        post_idx: Int,
        best_post_number: Int,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("post_idx", post_idx)
        params.put("best_post_number", best_post_number)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_BEST_POST,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 대표성과 내리기
     */
    fun uploadBestPost(
        best_post_number: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_BEST_POST_NUMBER.replace(
            Comm_Param.BEST_POST_NUMBER,
            best_post_number.toString()
        )

        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 발전 계획 페이지 조회
     */
    fun getBlueprint(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        // todo : 아마 profile Index 가 필요할거 같습니다.
        val url = Comm_Param.URL_BLUEPRINT_PRFOILE_CUR_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            cur_profile_index.toString()
        )
        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 팔로우 / 언팔로우
     */
    fun follow(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_FOLLOW_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            cur_profile_index.toString()
        )

        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 나를 팔로워한 사람들 리스트
     */
    fun getFollowerList(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_FOLLOW_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            cur_profile_index.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null,
            callback
        )

    }

    /**
     * GET
     * 내가 팔로우한 사람들 리스트
     */
    fun getFollowingList(
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_FOLLOW,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 드림포인트 조회
     */
    fun getDreamPoint(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_DREAMPOINT,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 드림포인트 미션 포인트 얻기
     */
    fun getMissionPoint(
        mission_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_DREAMPOINT_MISSION_IDX.replace(
            Comm_Param.MISSION_INDEX,
            mission_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 드림포인트 획득내역 가져오기
     */
    fun historyGet(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_DREAMPOINT_HISTORY_GET,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 드림포인트 사용내역 가져오기
     */
    fun historyUse(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_DREAMPOINT_HISTORY_USE,
            getHttpHeader(),
            null,
            callback
        )
    }


}