package com.truevalue.dreamappeal.utils

import com.truevalue.dreamappeal.BuildConfig

object Comm_Param {
    //val REAL = BuildConfig.DEBUG
    val REAL = true
    val APP_NAME = "DreamAppeal"

    // DEV 서버
    private val DEV_API = "http://ec2-15-164-118-112.ap-northeast-2.compute.amazonaws.com:8080"
    // REAL 서버
    private val REAL_API = "http://ec2-15-164-168-185.ap-northeast-2.compute.amazonaws.com:8080"
    // API
    val URL_API = if (REAL) REAL_API else DEV_API

    val PROFILE_INDEX = "PROFILE_INDEX"

    // 이미지 업로드
    val URL_UPLOADS = "$URL_API/uploads/"

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
    val URL_USERS_TOKENS_CHANGE = "$URL_USERS_TOKENS/change"

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
    val BEST_POST_NUMBER = "BEST_POST_NUMBER"
    val POST_SIZE = "POST_SIZE"
    val POST_INDEX = "POST_INDEX"
    val URL_ACHIEVEMENT_POSTS = "$URL_API/achievement_posts"
    val URL_ACHIEVEMENT_POSTS_BEST_POST_NUMBER = "$URL_ACHIEVEMENT_POSTS/$BEST_POST_NUMBER"
    val URL_ACHIEVEMENT_POSTS_PROFILE = "$URL_ACHIEVEMENT_POSTS/profile"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX = "$URL_ACHIEVEMENT_POSTS_PROFILE/$PROFILE_INDEX"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX_POST_SIZE = "$URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX/POST_SIZE"
    val URL_ACHIEVEMENT_POSTS_POST_IDX = "$URL_ACHIEVEMENT_POSTS/$POST_INDEX"

    // 대표성과
    val URL_BEST_POST = "$URL_API/best_post"
    val URL_BEST_POST_NUMBER = "$URL_BEST_POST/$BEST_POST_NUMBER"
    val URL_BEST_POST_NUMBER_POST_IDX = "$URL_BEST_POST_NUMBER/$POST_INDEX"

    // 발전계획
    val URL_BLUEPRINT = "$URL_API/blueprint"
    val URL_BLUEPRINTS = "$URL_API/blueprints"
    val URL_BLUEPRINTS_PROFILE = "$URL_BLUEPRINTS/profile"
    val URL_BLUEPRINTS_PRFOILE_CUR_PROFILE_IDX = "$URL_BLUEPRINTS_PROFILE/$PROFILE_INDEX"

    // 팔로우
    val URL_FOLLOW = "$URL_API/follow"
    val URL_FOLLOW_PROFILE_IDX = "$URL_FOLLOW/$PROFILE_INDEX"

/**
 * @GET {'/list/profile/:idx'} : lookup list of profile likes
 * @GET {'/list/action/:idx'} : lookup list of action post likes
 * @GET {'/list/achievement/:idx'} : lookup list of achievement post likes**/

    // 응원해준 어필러
    val CHEERING_INDEX = "CHEERING_INDEX"
    val URL_LIST = "$URL_API/likes/list"
    val URL_LIST_PROFILE = "$URL_LIST/profile"
    val URL_LIST_PROFILE_IDX = "$URL_LIST_PROFILE/$CHEERING_INDEX"
    val URL_LIST_ACTION = "$URL_LIST/action"
    val URL_LIST_ACTION_IDX = "$URL_LIST_ACTION/$CHEERING_INDEX"
    val URL_LIST_ACHIEVEMENT = "$URL_LIST/achievement"
    val URL_LIST_ACHIEVEMENT_IDX = "$URL_LIST_ACHIEVEMENT/$CHEERING_INDEX"

    // 드림포인트
    val MISSION_INDEX = "MISSION_INDEX"
    val URL_DREAMPOINT = "$URL_API/dreampoint"
    val URL_DREAMPOINT_MISSION_IDX = "$URL_DREAMPOINT/$MISSION_INDEX"
    val URL_DREAMPOINT_HISTORY = "$URL_DREAMPOINT/history"
    val URL_DREAMPOINT_HISTORY_GET = "$URL_DREAMPOINT_HISTORY/get"
    val URL_DREAMPOINT_HISTORY_USE = "$URL_DREAMPOINT_HISTORY/use"
    val URL_DREAMPOINT_COUPON = "$URL_DREAMPOINT/coupon"

    // 드림노트
    val URL_DREAMNOTE = "$URL_API/dreamnote"
    val URL_DREAMNOTE_LIFE = "$URL_DREAMNOTE/life"
    val URL_DREAMNOTE_LIFE_PROFILE = "$URL_DREAMNOTE_LIFE/profile"
    val URL_DREAMNOTE_LIFE_PROFILE_PROFILE_IDX = "$URL_DREAMNOTE_LIFE_PROFILE/$PROFILE_INDEX"

    val URL_DREAMNOTE_IDEA = "$URL_DREAMNOTE/idea"
    val URL_DREAMNOTE_IDEA_PROFILE = "$URL_DREAMNOTE_IDEA/profile"
    val URL_DREAMNOTE_IDEA_PROFILE_PROFILE_IDX = "$URL_DREAMNOTE_IDEA_PROFILE/$PROFILE_INDEX"

    // 능력
    val ABILITY_INDEX = "ABILITY_INDEX"
    val URL_ABILITIES = "$URL_API/abilities"
    val URL_ABILITIES_PROFILE = "$URL_ABILITIES/profile"
    val URL_ABILITIES_PROFILE_IDX = "$URL_ABILITIES_PROFILE/$PROFILE_INDEX"
    val URL_ABILITIES_IDX = "$URL_ABILITIES/$ABILITY_INDEX"

    // 기회
    val OPPORTUNITY_INDEX = "OPPORTUNITY_INDEX"
    val URL_OPPORTUNITIES = "$URL_API/opportunities"
    val URL_OPPORTUNITIES_PROFILE = "$URL_OPPORTUNITIES/profile"
    val URL_OPPORTUNITIES_PROFILE_IDX = "$URL_OPPORTUNITIES_PROFILE/$PROFILE_INDEX"
    val URL_OPPORTUNITIES_IDX = "$URL_OPPORTUNITIES/$OPPORTUNITY_INDEX"

    // 목표
    val OBJECT_INDEX = "OBJECT_INDEX"
    val URL_OBJECTS = "$URL_API/objects"
    val URL_OBJECTS_OBJECT_IDX = "$URL_OBJECTS/$OBJECT_INDEX"

    // 세부단계
    val STEP_INDEX = "STEP_INDEX"
    val URL_OBJECT_STEPS = "$URL_API/object_steps"
    val URL_OBJECT_STEPS_OBJECT = "$URL_OBJECT_STEPS/object"
    val URL_OBJECT_STEPS_OBJECT_IDX = "$URL_OBJECT_STEPS_OBJECT/$OBJECT_INDEX"
    val URL_OBJECT_STEPS_IDX = "$URL_OBJECT_STEPS/$STEP_INDEX"
    val URL_OBJECT_STEPS_IDX_OBJECT_IDX = "$URL_OBJECT_STEPS_IDX/object/$OBJECT_INDEX"

    // 광고 및 예시
    val EX_INDEX = "EX_INDEX"
    val URL_EXAMPLE = "$URL_API/example"
    val URL_EXAMPLE_PROFILE = "$URL_EXAMPLE/profile"
    val URL_EXAMPLE_PROFILE_EX_IDX = "$URL_EXAMPLE_PROFILE/$EX_INDEX"
    val URL_EXAMPLE_ABILITY = "$URL_EXAMPLE/ability"
    val URL_EXAMPLE_ABILITY_EX_IDX = "$URL_EXAMPLE_ABILITY/$EX_INDEX"
    val URL_EXAMPLE_OPPORTUNITY = "$URL_EXAMPLE/opportunity"
    val URL_EXAMPLE_OPPORTUNITY_EX_IDX = "$URL_EXAMPLE_OPPORTUNITY/$EX_INDEX"
    val URL_EXAMPLE_OBJECT = "$URL_EXAMPLE/object"
    val URL_EXAMPLE_OBJECT_EX_IDX = "$URL_EXAMPLE_OBJECT/$EX_INDEX"
    val URL_AD = "$URL_API/ad"

    //인증게시
    val URL_ACTION_POSTS = "$URL_API/action_posts"
    val URL_ACTION_POSTS_CATEGORY = "$URL_ACTION_POSTS/category"
    val URL_ACTION_POSTS_CATEGORY_OBJECT_IDX = "$URL_ACTION_POSTS_CATEGORY/$OBJECT_INDEX"

    //인증 게시물
    val URL_ACTION_POSTS_POST_IDX = "$URL_ACTION_POSTS/$POST_INDEX"

    //댓글
    val DST_RPOFILE_INDEX = "DST_RPOFILE_INDEX"
    val COMMENT_INDEX = "COMMENT_INDEX"
    val URL_PRESENT_COMMENTS = "$URL_API/present_comments"
    val URL_PRESENT_COMMENTS_PROFILE = "$URL_PRESENT_COMMENTS/profile"
    val URL_PRESENT_COMMENTS_PROFILE_IDX = "$URL_PRESENT_COMMENTS_PROFILE/$DST_RPOFILE_INDEX"
    val URL_PRESENT_COMMENTS_IDX = "$URL_PRESENT_COMMENTS/$COMMENT_INDEX"

    val URL_BLUEPRINT_COMMENTS = "$URL_API/blueprint_comments"
    val URL_BLUEPRINT_COMMENTS_PROFILE = "$URL_BLUEPRINT_COMMENTS/profile"
    val URL_BLUEPRINT_COMMENTS_PROFILE_IDX = "$URL_BLUEPRINT_COMMENTS_PROFILE/$DST_RPOFILE_INDEX"
    val URL_BLUEPRINT_COMMENTS_IDX = "$URL_BLUEPRINT_COMMENTS/$COMMENT_INDEX"

    val URL_ACHIEVEMENT_COMMENTS = "$URL_API/achievement_comments"
    val URL_ACHIEVEMENT_COMMENTS_POST = "$URL_ACHIEVEMENT_COMMENTS/post"
    val URL_ACHIEVEMENT_COMMENTS_POST_IDX = "$URL_ACHIEVEMENT_COMMENTS_POST/$POST_INDEX"
    val URL_ACHIEVEMENT_COMMENTS_IDX = "$URL_ACHIEVEMENT_COMMENTS/$COMMENT_INDEX"

    val URL_ACTION_COMMENTS = "$URL_API/action_comments"
    val URL_ACTION_COMMENTS_POST = "$URL_ACTION_COMMENTS/post"
    val URL_ACTION_COMMENTS_POST_IDX = "$URL_ACTION_COMMENTS_POST/$POST_INDEX"
    val URL_ACTION_COMMENTS_IDX = "$URL_ACTION_COMMENTS_POST/$COMMENT_INDEX"

    //검색
    val URL_SEARCH = "$URL_API/search"
    val URL_SEARCH_POPULAR = "$URL_SEARCH/popular"
    val URL_SEARCH_APPEALER = "$URL_SEARCH/appealer"
    val URL_SEARCH_ACTION_POST = "$URL_SEARCH/action-post"
    val URL_SEARCH_TAG = "$URL_SEARCH/tag"

    //타임라인
    val URL_TIMELINES = "$URL_API/timelines"

    //좋아요
    val URL_LIKES = "$URL_API/likes"
    val URL_LIKES_PROFILE = "$URL_LIKES/profile"
    val URL_LIKES_PROFILE_IDX = "$URL_LIKES_PROFILE/$PROFILE_INDEX"
    val URL_LIKES_ACTION = "$URL_LIKES/action"
    val URL_LIKES_ACTION_IDX = "$URL_LIKES_ACTION/$POST_INDEX"
    val URL_LIKES_ACHIEVEMENT = "$URL_LIKES/achievement"
    val URL_LIKES_ACHIEVEMENT_IDX = "$URL_LIKES_ACHIEVEMENT/$POST_INDEX"

    //댓글 좋아요

    val URL_LIKES_BLUEPRINT = "$URL_LIKES/blueprint"
    val URL_LIKES_PROFILE_COMMENT = "$URL_LIKES_PROFILE/comment"
    val URL_LIKES_PROFILE_COMMENT_IDX = "$URL_LIKES_PROFILE_COMMENT/$COMMENT_INDEX"
    val URL_LIKES_BLUEPRINT_COMMENT = "$URL_LIKES_BLUEPRINT/comment"
    val URL_LIKES_BLUEPRINT_COMMENT_IDX = "$URL_LIKES_BLUEPRINT_COMMENT/$COMMENT_INDEX"
    val URL_LIKES_ACHIEVEMENT_COMMENT = "$URL_LIKES_ACHIEVEMENT/comment"
    val URL_LIKES_ACHIEVEMENT_COMMENT_IDX = "$URL_LIKES_ACHIEVEMENT_COMMENT/$COMMENT_INDEX"
    val URL_LIKES_ACTION_COMMENT = "$URL_LIKES_ACTION/comment"
    val URL_LIKES_ACTION_COMMENT_IDX = "$URL_LIKES_ACTION_COMMENT/$COMMENT_INDEX"

}