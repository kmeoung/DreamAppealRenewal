package com.truevalue.dreamappeal.utils

import com.truevalue.dreamappeal.BuildConfig

object Comm_Param {

    val REAL = !BuildConfig.DEBUG
    //    val REAL = true
    const val APP_NAME = "DreamAppeal"

    // DEV 서버
    private const val DEV_API =
        "http://ec2-15-164-118-112.ap-northeast-2.compute.amazonaws.com:8080"
    // REAL 서버
    private const val REAL_API =
        "http://ec2-15-164-168-185.ap-northeast-2.compute.amazonaws.com:8080"
    // Kakao Addr
    const val KAKAO_ADDRESS_API = "https://dapi.kakao.com/v2/local/search/address.json"
    // API
    val URL_API = if (REAL) REAL_API else DEV_API

    const val PROFILE_INDEX = "PROFILE_INDEX"

    // 이미지 업로드
    val URL_UPLOADS = "$URL_API/uploads/"

    // 회원
    val URL_USERS = "$URL_API/users"
    val URL_USERS_PROFILE_IDX = "$URL_USERS/$PROFILE_INDEX"
    val URL_USERS_SIGNUP = "$URL_USERS/signup"

    // 주소
    val URL_USERS_ADDRESS = "$URL_USERS/address"

    // 소속
    const val GROUP_INDEX = "GROUP_INDEX"
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
    const val BEST_POST_NUMBER = "BEST_POST_NUMBER"
    const val POST_SIZE = "POST_SIZE"
    const val POST_INDEX = "POST_INDEX"
    val URL_ACHIEVEMENT_POSTS = "$URL_API/achievement_posts"
    val URL_ACHIEVEMENT_POSTS_BEST_POST_NUMBER = "$URL_ACHIEVEMENT_POSTS/$BEST_POST_NUMBER"
    val URL_ACHIEVEMENT_POSTS_PROFILE = "$URL_ACHIEVEMENT_POSTS/profile"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX = "$URL_ACHIEVEMENT_POSTS_PROFILE/$PROFILE_INDEX"
    val URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX_POST_SIZE =
        "$URL_ACHIEVEMENT_POSTS_PROFILE_PROFILE_IDX/POST_SIZE"
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
    const val CHEERING_INDEX = "CHEERING_INDEX"
    val URL_LIST = "$URL_API/likes/list"
    val URL_LIST_PROFILE = "$URL_LIST/profile"
    val URL_LIST_PROFILE_IDX = "$URL_LIST_PROFILE/$CHEERING_INDEX"
    val URL_LIST_ACTION = "$URL_LIST/action"
    val URL_LIST_ACTION_IDX = "$URL_LIST_ACTION/$CHEERING_INDEX"
    val URL_LIST_ACHIEVEMENT = "$URL_LIST/achievement"
    val URL_LIST_ACHIEVEMENT_IDX = "$URL_LIST_ACHIEVEMENT/$CHEERING_INDEX"

    // 드림포인트
    const val MISSION_INDEX = "MISSION_INDEX"
    val URL_DREAMPOINT = "$URL_API/dreampoint"
    val URL_DREAMPOINT_MISSION_IDX = "$URL_DREAMPOINT/$MISSION_INDEX"
    val URL_DREAMPOINT_HISTORY = "$URL_DREAMPOINT/history"
    val URL_DREAMPOINT_HISTORY_GET = "$URL_DREAMPOINT_HISTORY/get"
    val URL_DREAMPOINT_HISTORY_USE = "$URL_DREAMPOINT_HISTORY/use"
    val URL_DREAMPOINT_COUPON = "$URL_DREAMPOINT/coupon"

    val URL_DREAMPOINT_COUPON_TEMP = "$URL_DREAMPOINT_COUPON/temp"

    // 드림노트
    val URL_DREAMNOTE = "$URL_API/dreamnote"
    val URL_DREAMNOTE_LIFE = "$URL_DREAMNOTE/life"
    val URL_DREAMNOTE_LIFE_PROFILE = "$URL_DREAMNOTE_LIFE/profile"
    val URL_DREAMNOTE_LIFE_PROFILE_PROFILE_IDX = "$URL_DREAMNOTE_LIFE_PROFILE/$PROFILE_INDEX"

    val URL_DREAMNOTE_IDEA = "$URL_DREAMNOTE/idea"
    val URL_DREAMNOTE_IDEA_PROFILE = "$URL_DREAMNOTE_IDEA/profile"
    val URL_DREAMNOTE_IDEA_PROFILE_PROFILE_IDX = "$URL_DREAMNOTE_IDEA_PROFILE/$PROFILE_INDEX"

    // 능력
    const val ABILITY_INDEX = "ABILITY_INDEX"
    val URL_ABILITIES = "$URL_API/abilities"
    val URL_ABILITIES_PROFILE = "$URL_ABILITIES/profile"
    val URL_ABILITIES_PROFILE_IDX = "$URL_ABILITIES_PROFILE/$PROFILE_INDEX"
    val URL_ABILITIES_IDX = "$URL_ABILITIES/$ABILITY_INDEX"

    // 기회
    const val OPPORTUNITY_INDEX = "OPPORTUNITY_INDEX"
    val URL_OPPORTUNITIES = "$URL_API/opportunities"
    val URL_OPPORTUNITIES_PROFILE = "$URL_OPPORTUNITIES/profile"
    val URL_OPPORTUNITIES_PROFILE_IDX = "$URL_OPPORTUNITIES_PROFILE/$PROFILE_INDEX"
    val URL_OPPORTUNITIES_IDX = "$URL_OPPORTUNITIES/$OPPORTUNITY_INDEX"

    // 목표
    const val OBJECT_INDEX = "OBJECT_INDEX"
    val URL_OBJECTS = "$URL_API/objects"
    val URL_OBJECTS_OBJECT_IDX = "$URL_OBJECTS/$OBJECT_INDEX"

    // 세부단계
    const val STEP_INDEX = "STEP_INDEX"
    val URL_OBJECT_STEPS = "$URL_API/object_steps"
    val URL_OBJECT_STEPS_OBJECT = "$URL_OBJECT_STEPS/object"
    val URL_OBJECT_STEPS_OBJECT_IDX = "$URL_OBJECT_STEPS_OBJECT/$OBJECT_INDEX"
    val URL_OBJECT_STEPS_IDX = "$URL_OBJECT_STEPS/$STEP_INDEX"
    val URL_OBJECT_STEPS_IDX_OBJECT_IDX = "$URL_OBJECT_STEPS_IDX/object/$OBJECT_INDEX"

    // 광고 및 예시
    const val EX_INDEX = "EX_INDEX"
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
    const val DST_RPOFILE_INDEX = "DST_RPOFILE_INDEX"
    const val COMMENT_INDEX = "COMMENT_INDEX"
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
    val URL_ACTION_COMMENTS_IDX = "$URL_ACTION_COMMENTS/$COMMENT_INDEX"

    //검색
    val URL_SEARCH = "$URL_API/search"
    val URL_SEARCH_POPULAR = "$URL_SEARCH/popular"
    val URL_SEARCH_APPEALER = "$URL_SEARCH/appealer"
    val URL_SEARCH_ACTION_POST = "$URL_SEARCH/action"
    val URL_SEARCH_TAG = "$URL_SEARCH/tag"
    val URL_SEARCH_TAG_TAG = "${URL_SEARCH_TAG}-tag"
    val URL_SEARCH_TAG_POST = "${URL_SEARCH_TAG}-post"
    val URL_SEARCH_CONCERN = "$URL_SEARCH/concern"

    //타임라인
    val URL_TIMELINES = "$URL_API/timelines"

    //좋아요
    private val URL_LIKES = "$URL_API/likes"
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

    //드림보드 /board/popluar
    val URL_BOARD = "$URL_API/board"
    val URL_BOARD_POPULAR = "$URL_BOARD/popular"

    //이벤트
    val PROMOTION_INDEX = "PROMOTION_INDEX"
    val URL_BOARD_EVENT = "$URL_BOARD/event"
    val URL_BOARD_EVENT_PROMOTIONS = "$URL_BOARD_EVENT/promotions"
    val URL_BOARD_EVENT_PROMOTIONS_IDX = "$URL_BOARD_EVENT/$PROMOTION_INDEX"

    // 소원
    val ROW_NUM = "ROW_NUM"
    val WISH_INDEX = "WISH_INDEX"
    val URL_WISH = "$URL_API/wish"
    private val URL_WISH_MORE = "$URL_WISH/more"
    val URL_WISH_MORE_ROW_NUM = "$URL_WISH_MORE/$ROW_NUM"
    val URL_WISH_IDX = "$URL_WISH/$WISH_INDEX"

    private val URL_WISH_LIKE = "$URL_WISH/like"
    val URL_WISH_LIKE_IDX = "$URL_WISH_LIKE/$WISH_INDEX"

    //질문 게시판
    val CONCERN_INDEX = "CONCERN_INDEX"
    val RE_INDEX = "RE_INDEX"
    val URL_BOARD_CONCERN = "$URL_BOARD/concern"
    val URL_BOARD_CONECERN_MORE_IDX = "$URL_BOARD_CONCERN/more/$CONCERN_INDEX"
    val URL_BOARD_CONCERN_IDX = "$URL_BOARD_CONCERN/$CONCERN_INDEX"
    val URL_BOARD_CONCERN_STATUS = "$URL_BOARD_CONCERN/status"

    val URL_BOARD_RE_CONCERN = "$URL_BOARD/re-concern"
    val URL_BOARD_RE_CONCERN_IDX = "$URL_BOARD_RE_CONCERN/$CONCERN_INDEX"
    val URL_BOARD_RE_CONCERN_RE_IDX = "$URL_BOARD_RE_CONCERN/$RE_INDEX"

    private val URL_BOARD_VOTE = "$URL_BOARD/vote"
    private val URL_BOARD_VOTE_CONCERN = "$URL_BOARD_VOTE/concern"
    private val URL_BOARD_VOTE_RE_CONCERN = "$URL_BOARD_VOTE/re-concern"
    val URL_BOARD_VOTE_CONCERN_IDX = "$URL_BOARD_VOTE_CONCERN/$CONCERN_INDEX"
    val URL_BOARD_VOTE_RE_CONCERN_IDX = "$URL_BOARD_VOTE_RE_CONCERN/$RE_INDEX"

    val URL_BOARD_ADOPT = "$URL_BOARD/adopt"
    val URL_BOARD_ADOPT_RE_CONCERN = "$URL_BOARD_ADOPT/re-concern"
    val URL_BOARD_ADOPT_RE_CONCERN_IDX = "$URL_BOARD_ADOPT_RE_CONCERN/$RE_INDEX"

    //푸시토큰 등록
    val URL_NOTIFICATION = "$URL_API/notification"
    val URL_NOTIFICATION_TOKEN = "$URL_NOTIFICATION/token"
    val URL_NOTIFICATION_CHECK = "$URL_NOTIFICATION/check"

    //랭킹
    private val URL_RANK = "$URL_API/rank"
    val URL_RANK_TOTAL = "$URL_RANK/total"
    val URL_RANK_ACTION = "$URL_RANK/action"
    val URL_RANK_IDEA = "$URL_RANK/idea"
    val URL_RANK_LIFE = "$URL_RANK/life"
    val URL_RANK_REPUTATION = "$URL_RANK/reputation"

    val URL_RANK_TOTAL_MORE = "$URL_RANK_TOTAL/more"
    val URL_RANK_ACTION_MORE = "$URL_RANK_ACTION/more"
    val URL_RANK_IDEA_MORE = "$URL_RANK_IDEA/more"
    val URL_RANK_LIFE_MORE = "$URL_RANK_LIFE/more"
    val URL_RANK_REPUTATION_MORE = "$URL_RANK_REPUTATION/more"

    //약관 조회
    private val URL_DOCS = "$URL_API/docs"
    val URL_DOCS_PRIVACY = "$URL_DOCS/privacy"
    val URL_DOCS_TERMS = "$URL_DOCS/terms"

    //영감 게시물 저장
    private val URL_SHARE = "$URL_API/share"
    private val URL_SHARE_IDEA = "$URL_SHARE/idea"
    val URL_SHARE_IDEA_POST_IDX = "$URL_SHARE_IDEA/$POST_INDEX"



}