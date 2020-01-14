package com.truevalue.dreamappeal.http

import android.util.Log
import com.google.gson.Gson
import com.truevalue.dreamappeal.bean.BeanProfileUser
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object DAClient {

    val SUCCESS = "SUCCESS"
    val SUCCESS_ADDRESS = "SUCCESS_ADDRESS"
    val FAIL = "FAIL"
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

    val IMAGE_TYPE_PROFILE = "profiles"
    val IMAGE_TYPE_ACTION_POST = "action_posts"
    val IMAGE_TYPE_ACHIVEMENT_POST = "achievement_posts"

    val POST_TYPE_ACTION = 0
    val POST_TYPE_LIFE = 1
    val POST_TYPE_IDEA = 2

    fun sendToken(
        push_token: String?,
        callback: DAHttpCallback?
    ) {
        var nCallback: DAHttpCallback
        if (callback == null)
            nCallback = object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Log.d("Token Test ", body)
                }
            }
        else
            nCallback = callback

        val params = DAHttpParams()
        if (push_token.isNullOrEmpty()) return
        params.put("push_token", push_token!!)

        BaseOkhttpClient.request(
            HttpType.GET,
            "${Comm_Param.URL_API}/notification",
            getHttpHeader(),
            params,
            nCallback
        )
    }

    /**
     * PATCH
     * 이미지 업로드
     * idx -> profile_idx | achievement_post_idx | action_post_idx
     * type -> 0 : profile | 1 : action_post | 2 : achievement_post
     */
    fun uploadsImage(
        idx: Int,
        type: String,
        url: ArrayList<String>,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        val jsonObject = JSONObject()
        val numType = when (type) {
            IMAGE_TYPE_ACHIVEMENT_POST -> 2
            IMAGE_TYPE_ACTION_POST -> 1
            IMAGE_TYPE_PROFILE -> 0
            else -> 0
        }

        jsonObject.put("idx", idx)
        jsonObject.put("upload_type", numType)
        val path = JSONArray()
        for (i in url.indices) {
            val jsonUrl = JSONObject()
            jsonUrl.put("url", url[i])
            path.put(jsonUrl)
        }
        jsonObject.put("path", path)
        params.put(jsonObject)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_UPLOADS,
            getHttpHeader(),
            params,
            callback
        )
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
        if (!bean.name.isNullOrEmpty()) params.put("name", bean.name!!)
        if (!bean.nickname.isNullOrEmpty()) params.put("nickname", bean.nickname!!)
        params.put("gender", bean.gender)

        if (bean.address != null) {
            when (bean.address!!) {
                is JSONObject -> {
                    params.put("address", bean.address!! as JSONObject)
                }
                is String -> {
                    if (!(bean.address as String).isNullOrEmpty()) params.put(
                        "address",
                        bean.address!! as String
                    )
                }
            }
        }

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
     * 프로필 변경 (토큰 재생성)
     */
    fun profileChange(
        profile_order: Int,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("profile_order", profile_order)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_USERS_TOKENS_CHANGE,
            getHttpHeader(),
            params,
            callback
        )
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
        callback: DAHttpCallback
    ) {
        var url = Comm_Param.URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX.replace(
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

    /**
     * POST
     * 성과 등록
     */
    fun addAchievementPost(
        title: String,
        content: String,
        best_post_number: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_POSTS_BEST_POST_NUMBER.replace(
            Comm_Param.BEST_POST_NUMBER,
            best_post_number.toString()
        )
        val params = DAHttpParams()
        params.put("title", title)
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 성과 수정
     */
    fun updateAchievementPost(
        title: String,
        content: String,
        post_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_POSTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_idx.toString()
        )
        val params = DAHttpParams()
        params.put("title", title)
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            getHttpHeader(),
            params,
            callback
        )

    }

    /**
     * POST
     * 주요 성과 페이지 등록
     */
    @Deprecated("Not Used")
    fun addAchivementPost(
        title: String,
        content: String,
        register_date: String,
        tags: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("title", title)
        params.put("content", content)
        params.put("register_date", register_date)
        params.put("tags", tags)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_ACHIEVEMENT_POSTS,
            getHttpHeader(),
            params,
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
    fun upBestPost(
        post_idx: Int,
        best_post_number: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_BEST_POST_NUMBER_POST_IDX
            .replace(Comm_Param.POST_INDEX, post_idx.toString())
            .replace(Comm_Param.BEST_POST_NUMBER, best_post_number.toString())

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 대표성과 내리기
     */
    fun downBestPost(
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
        val url = Comm_Param.URL_BLUEPRINTS_PRFOILE_CUR_PROFILE_IDX.replace(
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
     * PROFILE
     * 나를 응원하는 어필러 리스트
     */
    fun getProfileCheering(
        idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_LIST_PROFILE_IDX.replace(Comm_Param.CHEERING_INDEX, idx.toString())

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
     * ACTION POST
     * 나를 응원하는 어필러 리스트
     */
    fun getActionCheering(
        idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_LIST_ACTION_IDX.replace(Comm_Param.CHEERING_INDEX, idx.toString())

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
     * ACHIEVEMENT POST
     * 나를 응원하는 어필러 리스트
     */
    fun getAchievementCheeing(
        idx: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_LIST_ACHIEVEMENT_IDX.replace(Comm_Param.CHEERING_INDEX, idx.toString())

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

    /**
     * POST
     * 드림포인트 쿠폰 입력
     */
    fun addDreamPointCoupon(
        serial_code: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("serial_code", serial_code)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_DREAMPOINT_COUPON,
            getHttpHeader(),
            params,
            callback
        )

    }

    /**
     * GET
     * 현재 보고있는 프로필의 드림노트 일상 경험 가져오기
     */
    fun getDreamNoteLife(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_DREAMNOTE_LIFE_PROFILE_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            cur_profile_index.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null, callback
        )
    }

    /**
     * GET
     * 현재 보고있는 프로필의 드림노트 영감갤러리 가져오기
     */
    fun getDreamNoteIdea(
        cur_profile_index: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_DREAMNOTE_IDEA_PROFILE_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            cur_profile_index.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null, callback
        )
    }

    /**
     * GET
     * 갖출 능력 조회
     */
    fun getAbilities(
        profile_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ABILITIES_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            profile_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null, callback
        )
    }

    /**
     * POST
     * 갖출 능력 등록
     */
    fun addAbility(
        ability: String,
        callback: DAHttpCallback
    ) {
        val params: DAHttpParams = DAHttpParams()
        params.put("ability", ability)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_ABILITIES,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 갖출 능력 수정
     */
    fun updateAbility(
        ability_idx: Int,
        ability: String,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_ABILITIES_IDX.replace(Comm_Param.ABILITY_INDEX, ability_idx.toString())
        val params = DAHttpParams()
        params.put("ability", ability)

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
     * 갖출 능력 삭제
     */
    fun deleteAbility(
        ability_idx: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_ABILITIES_IDX.replace(Comm_Param.ABILITY_INDEX, ability_idx.toString())

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 민들고픈 기회 조회
     */
    fun getOpportunity(
        profile_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_OPPORTUNITIES_PROFILE_IDX.replace(
            Comm_Param.PROFILE_INDEX,
            profile_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null, callback
        )
    }

    /**
     * POST
     * 만들고픈 기회 등록
     */
    fun addOpportunity(
        opportunity: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("opportunity", opportunity)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_OPPORTUNITIES,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 만들고픈 기회 수정
     */
    fun updateOpportunity(
        opportunity_idx: Int,
        opportunity: String,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OPPORTUNITIES_IDX.replace(
            Comm_Param.OPPORTUNITY_INDEX,
            opportunity_idx.toString()
        )
        val params = DAHttpParams()
        params.put("opportunity", opportunity)

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
     * 만들고픈 기회 삭제
     */
    fun deleteOpportunity(
        opportunity_idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OPPORTUNITIES_IDX.replace(
            Comm_Param.OPPORTUNITY_INDEX,
            opportunity_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 실천목표 추가
     */
    fun addObject(
        object_name: String,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        params.put("object_name", object_name)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_OBJECTS,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 실천 목표 조회 (세부단계 목록 조회)
     */
    fun getObjects(
        object_idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OBJECTS_OBJECT_IDX.replace(
            Comm_Param.OBJECT_INDEX,
            object_idx.toString()
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
     * 실천 목표 삭제
     * complete_date -> YYYY-MM-DD HH:mm:ss
     */
    fun updateObject(
        object_idx: Int,
        object_name: String?,
        thumbnail_image: String?,
        complete: Int?,
        complete_date: String?,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OBJECTS_OBJECT_IDX.replace(
            Comm_Param.OBJECT_INDEX,
            object_idx.toString()
        )

        val params = DAHttpParams()
        if (!object_name.isNullOrEmpty()) params.put("object_name", object_name)
        if (!thumbnail_image.isNullOrEmpty()) params.put("thumbnail_image", thumbnail_image)
        if (complete != null) params.put("complete", complete)
        if (!complete_date.isNullOrEmpty()) params.put("complete_date", complete_date)

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
     * 실천 목표 삭제
     */
    fun deleteObject(
        object_idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OBJECTS_OBJECT_IDX.replace(
            Comm_Param.OBJECT_INDEX,
            object_idx.toString()
        )

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 실천 목표 세부단계 등록
     */
    fun addObjectStepDetail(
        object_idx: Int,
        title: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_OBJECT_STEPS_OBJECT_IDX
            .replace(
                Comm_Param.OBJECT_INDEX, object_idx.toString()
            )

        val params = DAHttpParams()
        params.put("title", title)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 실천 목표 세부단계 수정
     */
    fun updateObjectStepDetail(
        step_idx: Int,
        object_idx: Int,
        title: String,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OBJECT_STEPS_IDX_OBJECT_IDX
            .replace(Comm_Param.STEP_INDEX, step_idx.toString())
            .replace(Comm_Param.OBJECT_INDEX, object_idx.toString())

        val params = DAHttpParams()
        params.put("title", title)

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
     * 실천 목표 세부단계 삭제
     */
    fun deleteObjectStepDetail(
        step_idx: Int,
        object_idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_OBJECT_STEPS_IDX_OBJECT_IDX
            .replace(Comm_Param.STEP_INDEX, step_idx.toString())
            .replace(Comm_Param.OBJECT_INDEX, object_idx.toString())

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }


    /**
     * GET
     * profile 예시 이미지 조회
     */
    fun profileExampleImage(
        ex_idx: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_EXAMPLE_PROFILE_EX_IDX.replace(Comm_Param.EX_INDEX, ex_idx.toString())

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
     * ability 예시 이미지 조회
     */
    fun abilityExampleImage(
        ex_idx: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_EXAMPLE_ABILITY_EX_IDX.replace(Comm_Param.EX_INDEX, ex_idx.toString())

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
     * opportunity 예시 이미지 조회
     */
    fun opportunityExampleImage(
        ex_idx: Int,
        callback: DAHttpCallback
    ) {
        val url = Comm_Param.URL_EXAMPLE_OPPORTUNITY_EX_IDX.replace(
            Comm_Param.EX_INDEX,
            ex_idx.toString()
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
     * object 예시 이미지 조회
     */
    fun objectExampleImage(
        ex_idx: Int,
        callback: DAHttpCallback
    ) {
        val url =
            Comm_Param.URL_EXAMPLE_OBJECT_EX_IDX.replace(Comm_Param.EX_INDEX, ex_idx.toString())

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
     * Ad Image
     */
    fun getAd(callback: DAHttpCallback) {
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_AD,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 실천목표 가져오기
     */
    fun getActionPostCategoty(callback: DAHttpCallback) {
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_ACTION_POSTS_CATEGORY,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 실천목표 세부단계 가져오기
     */
    fun getActionPostCategotyDetail(
        object_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACTION_POSTS_CATEGORY_OBJECT_IDX.replace(
            Comm_Param.OBJECT_INDEX,
            object_idx.toString()
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
     * POST
     * 내 꿈 소개 댓글 등록
     * parent_idx > 0 일경우 리플
     */
    fun addProfileComment(
        dst_profile_idx: Int,
        writer_idx: Int,
        parent_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_PRESENT_COMMENTS_PROFILE_IDX.replace(
            Comm_Param.DST_RPOFILE_INDEX,
            dst_profile_idx.toString()
        )

        val params = DAHttpParams()
        params.put("writer_idx", writer_idx)
        if (parent_idx > 0) {
            params.put("parent_idx", parent_idx)
        }
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 내 꿈 소개 댓글 조회
     */
    fun getProfileComment(
        dst_profile_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_PRESENT_COMMENTS_PROFILE_IDX.replace(
            Comm_Param.DST_RPOFILE_INDEX,
            dst_profile_idx.toString()
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
     * 내 꿈 소개 댓글 수정
     */
    fun updateProfileComment(
        comment_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_PRESENT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()
        params.put("content", content)

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
     * 내 꿈 소개 댓글 삭제
     */
    fun deleteProfileComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_PRESENT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * POST
     * 발전계획 댓글 등록
     * parent_idx > 0 일경우 리플
     */
    fun addBlueprintComment(
        dst_profile_idx: Int,
        writer_idx: Int,
        parent_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_BLUEPRINT_COMMENTS_PROFILE_IDX.replace(
            Comm_Param.DST_RPOFILE_INDEX,
            dst_profile_idx.toString()
        )

        val params = DAHttpParams()
        params.put("writer_idx", writer_idx)
        if (parent_idx > 0) {
            params.put("parent_idx", parent_idx)
        }
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 발전계획 댓글 조회
     */
    fun getBlueprintComment(
        dst_profile_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_BLUEPRINT_COMMENTS_PROFILE_IDX.replace(
            Comm_Param.DST_RPOFILE_INDEX,
            dst_profile_idx.toString()
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
     * 발전계획 댓글 수정
     */
    fun updateBlueprintComment(
        comment_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_BLUEPRINT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()
        params.put("content", content)

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
     * 발전계획 댓글 삭제
     */
    fun deleteBlueprintComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_BLUEPRINT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }


    /**
     * POST
     * 실현성과 댓글 등록
     * parent_idx > 0 일경우 리플
     */
    fun addAchievementPostComment(
        post_idx: Int,
        writer_idx: Int,
        parent_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_COMMENTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_idx.toString()
        )

        val params = DAHttpParams()
        params.put("writer_idx", writer_idx)
        if (parent_idx > 0) {
            params.put("parent_idx", parent_idx)
        }
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 실현성과 댓글 조회
     */
    fun getAchievementPostComment(
        post_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_COMMENTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_idx.toString()
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
     * 실현성과 댓글 수정
     */
    fun updateAchievementPostComment(
        comment_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()
        params.put("content", content)

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
     * 실현성과 댓글 삭제
     */
    fun deleteAchievementPostComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACHIEVEMENT_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * POST
     * 실현성과 댓글 등록
     * parent_idx > 0 일경우 리플
     */
    fun addActionPostComment(
        post_idx: Int,
        writer_idx: Int,
        parent_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACTION_COMMENTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_idx.toString()
        )

        val params = DAHttpParams()
        params.put("writer_idx", writer_idx)
        if (parent_idx > 0) {
            params.put("parent_idx", parent_idx)
        }
        params.put("content", content)

        BaseOkhttpClient.request(
            HttpType.POST,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 실현성과 댓글 조회
     */
    fun getActionPostComment(
        post_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACTION_COMMENTS_POST_IDX.replace(
            Comm_Param.POST_INDEX,
            post_idx.toString()
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
     * 실현성과 댓글 수정
     */
    fun updateActionPostComment(
        comment_idx: Int,
        content: String,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACTION_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()
        params.put("content", content)

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
     * 실현성과 댓글 삭제
     */
    fun deleteActionPostComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {

        val url = Comm_Param.URL_ACTION_COMMENTS_IDX.replace(
            Comm_Param.COMMENT_INDEX,
            comment_idx.toString()
        )

        val params = DAHttpParams()

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 실천인증 상세 가져오기
     */
    fun getActionPostsDetail(
        post_idx: Int,
        callback: DAHttpCallback
    ) {

        val url =
            Comm_Param.URL_ACTION_POSTS_POST_IDX.replace(Comm_Param.POST_INDEX, post_idx.toString())

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null,
            callback
        )

    }

    /**
     * DELETE
     * 실천인증 삭제
     */
    fun deleteActionPostsDetail(
        post_idx: Int,
        callback: DAHttpCallback
    ) {

        val url =
            Comm_Param.URL_ACTION_POSTS_POST_IDX.replace(Comm_Param.POST_INDEX, post_idx.toString())

        BaseOkhttpClient.request(
            HttpType.DELETE,
            url,
            getHttpHeader(),
            null,
            callback
        )

    }

    /**
     * Post
     * 실천인증 및 여러가지 추가
     * 0 : Action Post
     * 1 : Life Post
     * 2 : Idea Post
     */
    fun addActionPost(
        content: String?,
        post_type: Int,
        tags: String,
        object_idx: Int?,
        step_idx: Int?,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        if (content != null) params.put("content", content)
        params.put("post_type", post_type)
        params.put("tags", tags)
        if (object_idx != null) params.put("object_idx", object_idx)
        if (step_idx != null) params.put("step_idx", step_idx)
        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_ACTION_POSTS,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * PATCH
     * 실천인증 수정
     */
    fun updateActionPosts(
        post_idx: Int,
        object_idx: Int?,
        step_idx: Int?,
        thumbnail_image: String?,
        content: String?,
        tags: String?,
        callback: DAHttpCallback
    ) {

        val url =
            Comm_Param.URL_ACTION_POSTS_POST_IDX.replace(Comm_Param.POST_INDEX, post_idx.toString())

        val params = DAHttpParams()
        if (object_idx != null) params.put("object_idx", object_idx)
        if (step_idx != null) params.put("step_idx", step_idx)
        if (thumbnail_image != null) params.put("thumbnail_image", thumbnail_image)
        if (content != null) params.put("content", content)
        if (tags != null) params.put("tags", tags)

        BaseOkhttpClient.request(
            HttpType.PATCH,
            url,
            getHttpHeader(),
            params,
            callback
        )

    }

    /**
     * GET
     * 어필러 추천
     */
    fun searchAppealer(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_SEARCH_APPEALER,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 어필러 검색
     */
    fun searchAppealer(
        keyword: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("keyword", keyword)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_SEARCH_APPEALER,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 게시글 추천
     */
    fun searchBoard(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_SEARCH_ACTION_POST,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 게시글 검색
     */
    fun searchBoard(
        keyword: String,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        params.put("keyword", keyword)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_SEARCH_ACTION_POST,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 태그 추천
     */
    fun searchTag(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_SEARCH_TAG,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 태그 검색
     */
    fun searchTag(
        keyword: String,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        params.put("keyword", keyword)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_SEARCH_TAG_TAG,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * POST
     * 태그 게시물 검색
     */
    fun searchTagPost(
        keyword: String,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        params.put("keyword", keyword)

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_SEARCH_TAG_POST,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * DELETE
     * 태그 검색 기록 제거
     */
    fun deleteTagHistory(
        keyword: String,
        register_date: String?,
        callback: DAHttpCallback
    ) {
        val params = DAHttpParams()
        params.put("keyword", keyword)
        if (!register_date.isNullOrEmpty()) params.put("register_date", register_date)

        BaseOkhttpClient.request(
            HttpType.DELETE,
            Comm_Param.URL_SEARCH_TAG,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 타임라인 가져오기
     */
    fun getTimeLine(refresh: Boolean, last_idx: Int, callback: DAHttpCallback) {

        val params = DAHttpParams()
        if (refresh) params.put("refresh", refresh)
        if (last_idx > -1) params.put("last_idx", last_idx)
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_TIMELINES,
            getHttpHeader(),
            params,
            callback
        )
    }


    /**
     * PATCH
     * 프로필 좋아요
     */
    fun likeDreamPresent(
        profile_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_PROFILE_IDX.replace(
                Comm_Param.PROFILE_INDEX,
                profile_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * Achievement Post 좋아요
     */
    fun likeAchievementPost(
        post_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_ACHIEVEMENT_IDX.replace(
                Comm_Param.POST_INDEX,
                post_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * Action Post 좋아요
     */
    fun likeActionPost(
        post_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_ACTION_IDX.replace(
                Comm_Param.POST_INDEX,
                post_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 발전계획 댓글 좋아요
     */
    fun likeBlueprintComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_BLUEPRINT_COMMENT_IDX.replace(
                Comm_Param.COMMENT_INDEX,
                comment_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 프로필 댓글 좋아요
     */
    fun likeDreamPresentComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_PROFILE_COMMENT_IDX.replace(
                Comm_Param.COMMENT_INDEX,
                comment_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 성과 댓글 좋아요
     */
    fun likeAchievementPostComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_ACHIEVEMENT_COMMENT_IDX.replace(
                Comm_Param.COMMENT_INDEX,
                comment_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * PATCH
     * 인증 댓글 좋아요
     */
    fun likeActionPostComment(
        comment_idx: Int,
        callback: DAHttpCallback
    ) {
        BaseOkhttpClient.request(
            HttpType.PATCH,
            Comm_Param.URL_LIKES_ACTION_COMMENT_IDX.replace(
                Comm_Param.COMMENT_INDEX,
                comment_idx.toString()
            ),
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 드림보드 - 인기 조회
     */
    fun getDreamBoardPopular(callback: DAHttpCallback?) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_BOARD_POPULAR,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * POST
     * 회원 주소 등록
     */
    fun setUserAddress(
        address_name: String?,
        region_1depth_name: String?,
        region_2depth_name: String?,
        region_3depth_name: String?,
        region_3depth_h_name: String?,
        x: Double?,
        y: Double?,
        zip_code: String?,
        callback: DAHttpCallback
    ) {

        val params = DAHttpParams()
        if (!address_name.isNullOrEmpty()) params.put("address_name", address_name) else params.put(
            "address_name",
            ""
        )
        if (!region_1depth_name.isNullOrEmpty()) params.put(
            "region_1depth_name",
            region_1depth_name
        ) else params.put(
            "region_1depth_name",
            ""
        )
        if (!region_2depth_name.isNullOrEmpty()) params.put(
            "region_2depth_name",
            region_2depth_name
        ) else params.put(
            "region_2depth_name",
            ""
        )
        if (!region_3depth_name.isNullOrEmpty()) params.put(
            "region_3depth_name",
            region_3depth_name
        ) else params.put(
            "region_3depth_name",
            ""
        )
        if (!region_3depth_h_name.isNullOrEmpty()) params.put(
            "region_3depth_h_name",
            region_3depth_h_name
        ) else params.put(
            "region_3depth_h_name",
            ""
        )
        if (x != null) params.put("x", x) else params.put("x", 0)
        if (y != null) params.put("y", y) else params.put("y", 0)
        if (!zip_code.isNullOrEmpty()) params.put("zip_code", zip_code) else params.put(
            "zip_code",
            ""
        )

        BaseOkhttpClient.request(
            HttpType.POST,
            Comm_Param.URL_USERS_ADDRESS,
            getHttpHeader(),
            params,
            callback
        )
    }

    /**
     * GET
     * 이벤트 - 기본 조회
     */
    fun getBoardEvent(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_BOARD_EVENT,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 이벤트 - 프로모션 전체조회
     */
    fun getBoardEventAll(callback: DAHttpCallback) {

        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.URL_BOARD_EVENT_PROMOTIONS,
            getHttpHeader(),
            null,
            callback
        )
    }

    /**
     * GET
     * 이벤트 - 프로모션 전체조회 - 상세조회
     */
    fun getBoardEventDetail(promotion_idx : Int,
                            callback: DAHttpCallback) {

        val url = Comm_Param.URL_BOARD_EVENT_PROMOTIONS_IDX.replace(Comm_Param.PROMOTION_INDEX,promotion_idx.toString())

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
     * 소원 기본 조회
     */
    fun getWish(callback: DAHttpCallback){

        val url = Comm_Param.URL_WISH

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
     * 소원 추가 조회
     */
    fun getMoreWish(row_num : Int,
                    callback: DAHttpCallback){

        val url = Comm_Param.URL_WISH_MORE_ROW_NUM.replace(Comm_Param.ROW_NUM,row_num.toString())


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
     * 소원 게시글 조회
     */
    fun getWish(wish_idx : Int,
                    callback: DAHttpCallback){

        val url = Comm_Param.URL_WISH_IDX.replace(Comm_Param.WISH_INDEX,wish_idx.toString())

        BaseOkhttpClient.request(
            HttpType.GET,
            url,
            getHttpHeader(),
            null,
            callback
        )
    }

}