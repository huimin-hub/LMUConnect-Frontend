package de.lmu.lmuconnect.general.api

import de.lmu.lmuconnect.auth.api.AuthLoginPostRequest
import de.lmu.lmuconnect.auth.api.AuthLoginPostResponse
import de.lmu.lmuconnect.auth.api.AuthSignupPostRequest
import de.lmu.lmuconnect.auth.api.AuthSignupPostResponse
import de.lmu.lmuconnect.home.api.NewsGetResponse
import de.lmu.lmuconnect.menu.api.MenuItemsDeleteRequest
import de.lmu.lmuconnect.menu.api.MenuItemsGetResponse
import de.lmu.lmuconnect.menu.api.MenuItemsPatchRequest
import de.lmu.lmuconnect.menu.api.MenuItemsPostRequest
import de.lmu.lmuconnect.social.api.*
import de.lmu.lmuconnect.study.calendar.api.StudyEventsGetResponse
import de.lmu.lmuconnect.study.courses.api.StudyCoursesGetResponse
import de.lmu.lmuconnect.study.courses.api.StudyCoursesRegistrationRequest
import de.lmu.lmuconnect.study.courses.data.Course
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    // --------- AUTH ---------
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun authLoginPost(@Body request: AuthLoginPostRequest): Call<AuthLoginPostResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/signup")
    fun authSignupPost(@Body request: AuthSignupPostRequest): Call<AuthSignupPostResponse>

    // --------- USER -----------

    @Headers("Content-Type: text/plain")
    @GET("username/{email}")
    fun userNameGet(@Path("email") email: String): Call<UserNameGetResponse>

    @Headers("Content-Type: text/plain")
    @GET("userid/{email}")
    fun userIdGet(@Path("email") email: String): Call<UserIdGetResponse>

    @Headers("Content-Type: application/json")
    @GET("email/{matrixId}")
    fun userNameMatrixGet(@Path("matrixId") matrix: String): Call<UserMailGetMatrixResponse>

    @Headers("Content-Type: application/json")
    @GET("matrixid/{email}")
    fun userMatrixIdGet(@Path("email") email: String, @Header("x-access-token") token: String): Call<GroupMemberIdResponse>


    // --------- HOME --------------
    @Headers("Content-Type: application/json")
    @GET("home/news")
    fun newsFeedGet(): Call<NewsGetResponse>


    // --------- MENU ---------
    @Headers("Content-Type: application/json")
    @GET("menu/items")
    fun menuItemsGet(@Header("x-access-token") token: String): Call<MenuItemsGetResponse>

    @Headers("Content-Type: application/json")
    @POST("menu/items")
    fun menuItemsPost(@Body request: MenuItemsPostRequest, @Header("x-access-token") token: String): Call<Void> // Maybe Unit

    @Headers("Content-Type: application/json")
    @PATCH("menu/items")
    fun menuItemsPatch(@Body request: MenuItemsPatchRequest, @Header("x-access-token") token: String): Call<Void> // Maybe Unit

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "menu/items", hasBody = true)
    fun menuItemsDelete(@Body request: MenuItemsDeleteRequest, @Header("x-access-token") token: String): Call<Void> // Maybe Unit


    // ------- STUDY ------------
    @Headers("Content-Type: application/json")
    @GET("study/courses/all")
    fun studyCoursesAllGet(@Header("x-access-token") token: String): Call<StudyCoursesGetResponse>

    @Headers("Content-Type: application/json")
    @GET("study/courses/personal")
    fun studyCoursesPersonalGet(@Header("x-access-token") token: String): Call<StudyCoursesGetResponse>

    @Headers("Content-Type: application/json")
    @GET("study/courses/info/{course_id}")
    fun studyCoursesInfoGet(@Path("course_id") courseId: String, @Header("x-access-token") token: String): Call<Course.Info>

    @Headers("Content-Type: application/json")
    @POST("study/courses/registration")
    fun studyCoursesRegistrationPost(@Body request: StudyCoursesRegistrationRequest, @Header("x-access-token") token: String): Call<Void> // Maybe Unit

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "study/courses/registration", hasBody = true)
    fun studyCoursesRegistrationDelete(@Body request: StudyCoursesRegistrationRequest, @Header("x-access-token") token: String): Call<Void> // Maybe Unit

    @Headers("Content-Type: application/json")
    @GET("study/events/{start_date}/{end_date}")
    fun studyEventsGet(@Path("start_date") startDate: String, @Path("end_date") endDate: String, @Header("x-access-token") token: String): Call<StudyEventsGetResponse>

    @Headers("Content-Type: application/json")
    @GET("study/events/today")
    fun studyEventsTodayGet(@Header("x-access-token") token: String): Call<StudyEventsGetResponse>


    // ------- SOCIAL ------------
    @Headers("Content-Type: application/json")
    @GET("/profile")
    fun profileInfoGet(@Header("x-access-token") token: String): Call<ProfileInfoGetResponse>

    @Headers("Content-Type: application/json")
    @PATCH("/profile")
    fun profileInfoPatch(@Body request: ProfileInfoPatchRequest, @Header("x-access-token") token: String): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("/social/friendsList")
    fun friendsListGet(@Header("x-access-token") token: String): Call<FriendsListGetResponse>

    @Headers("Content-Type: application/json")
    @POST("/social/friendsList")
    fun friendAddPost(@Body request: FriendAddRequest, @Header("x-access-token") token: String): Call<FriendAddResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/social/friendsList", hasBody = true)
    fun friendDelete(@Body request: FriendDeleteRequest, @Header("x-access-token") token: String): Call<FriendDeleteRequest>

    @Headers("Content-Type: application/json")
    @GET("/matrix/roomParams")
    fun roomParamsGet(@Header("x-access-token") token: String): Call<FriendsListGetResponse>

    @Headers("Content-Type: application/json")
    @POST("/matrix/joinRoom")
    fun joinRoomPost(@Body request: JoinRoomPostRequest, @Header("x-access-token") token: String): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("matrix/member")
    fun groupMemberGet(@Body request: GroupMemberGetRequest, @Header("x-access-token") token: String): Call<GroupMemberGetResponse>

    @Headers("Content-Type: application/json")
    @GET("/profile/others/{name}")
    fun othersProfileInfoGet(@Path("name") name: String,  @Header("x-access-token") token: String ): Call<OthersProfileInfoGetResponse>

    @Headers("Content-Type: application/json")
    @POST("matrix/member/add")
    fun groupMemberIdGet(@Body request: GroupMemberIdRequest, @Header("x-access-token") token: String): Call<GroupMemberIdResponse>

}