package com.truevalue.dreamappeal.utils

import com.truevalue.dreamappeal.BuildConfig

object Comm_Param {
    val REAL = !BuildConfig.DEBUG
    val APP_NAME = "DreamAppeal"

    // 개발용 아이디 비밀번호
    val DEBUG_NAME = "DEBUG"
    val DEBUG_EMAIL = "kmeoung@gmail.com"
    val DEBUG_PASSWORD = "12345678"

    // DEV 서버
    private val DEV_API = "http://ec2-15-164-118-112.ap-northeast-2.compute.amazonaws.com:8080"
    // REAL 서버
    private val REAL_API = "http://ec2-15-164-168-185.ap-northeast-2.compute.amazonaws.com:8080"
    // API
    val URL_API = if (REAL) REAL_API else DEV_API

    val PROFILE_INDEX = "PROFILE_INDEX"

    // 회원
    val URL_USERS = "$URL_API/users"
    val URL_USERS_PROFILE_IDX = "$URL_USERS/$PROFILE_INDEX"
    val URL_USERS_SIGNUP = "$URL_USERS/signup"

    // 소속
    val GROUP_INDEX = "GROUP_INDEX"
    val URL_USERS_GROUP = "$URL_USERS/group"
    val URL_USERS_GROUP_IDX = "$URL_USERS_GROUP/$GROUP_INDEX"

    // 회원가입 유틸
    val URL_USERS_SIGNUP_EMAIL = "$URL_USERS_SIGNUP/email"
    val URL_USERS_SIGNUP_EMAIL_CHECK = "$URL_USERS_SIGNUP_EMAIL/check"
    val URL_USERS_SIGNUP_EMAIL_VERIFY = "$URL_USERS_SIGNUP_EMAIL/verify"

    // 로그인
    val URL_USERS_TOKENS = "$URL_USERS/tokens"

    // 비밀번호 재설정
    val URL_RECOVER = "$URL_API/recover"
    val URL_RECOVER_INITIATE = "$URL_RECOVER/initiate"
    val URL_RECOVER_CODE = "$URL_RECOVER/code"
    val URL_RECOVER_PASSWORD = "$URL_RECOVER/password"

    // 프로필
    val URL_PROFILES = "$URL_API/profiles"
    val URL_PROFILES_CUR_PROFILE_IDX = "$URL_PROFILES/$PROFILE_INDEX"

    // 프로필 리스트
    val URL_PROFILES_PROFILE_IDX = "$URL_PROFILES/$PROFILE_INDEX"
    val URL_PROFILES_PROFILE_IDX_LIST = "$URL_PROFILES_CUR_PROFILE_IDX/list"
    val URL_PROFILES_LIST = "$URL_PROFILES/list"

    // 주요성과
    val POST_SIZE = "POST_SIZE"
    val POST_INDEX = "POST_INDEX"
    val URL_ACHIEVEMENT_POSTS = "$URL_API/achievement_posts"
    val URL_ACHIEVEMENT_POSTS_PROFILE = "$URL_ACHIEVEMENT_POSTS/profile"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX = "$URL_ACHIEVEMENT_POSTS_PROFILE/$PROFILE_INDEX"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX_POST_SIZE = "$URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX/POST_SIZE"
    val URL_ACHIEVEMENT_POSTS_POST_IDX = "$URL_ACHIEVEMENT_POSTS/$POST_INDEX"

    // 대표성과
    val BEST_POST_NUMBER = "BEST_POST_NUMBER"
    val URL_BEST_POST = "$URL_API/best_post"
    val URL_BEST_POST_NUMBER = "$URL_BEST_POST/$BEST_POST_NUMBER"

    // 발전계획
    val URL_BLUEPRINT = "$URL_API/blueprint"
    val URL_BLUEPRINT_PROFILE = "$URL_BLUEPRINT/profile"
    val URL_BLUEPRINT_PRFOILE_CUR_PROFILE_IDX = "$URL_BLUEPRINT_PROFILE/$PROFILE_INDEX"

    // 팔로우
    val URL_FOLLOW = "$URL_API/follow"
    val URL_FOLLOW_PROFILE_IDX = "$URL_FOLLOW/$PROFILE_INDEX"

    // 드림포인트
    val MISSION_INDEX = "MISSION_INDEX"
    val URL_DREAMPOINT = "$URL_API/dreampoint"
    val URL_DREAMPOINT_MISSION_IDX = "$URL_DREAMPOINT/$MISSION_INDEX"
    val URL_DREAMPOINT_HISTORY = "$URL_DREAMPOINT/history"
    val URL_DREAMPOINT_HISTORY_GET = "$URL_DREAMPOINT_HISTORY/get"
    val URL_DREAMPOINT_HISTORY_USE = "$URL_DREAMPOINT_HISTORY/use"
    val URL_DREAMPOINT_COUPON = "$URL_DREAMPOINT/coupon"


}