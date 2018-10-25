package com.rohan7055.earnchat;

import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.Model.UserCreationStatusModel;
import com.rohan7055.earnchat.Model.UserModel;
import com.rohan7055.earnchat.celgram.ResponseModels.AdveryiserLoginModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Karthik on 22-06-2016.
 */
public interface LoginApi {

    @FormUrlEncoded
    @POST("user/login")
    Call<UserModel> login(@Field("mobile") String mobile, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/register")
    Call<UserCreationStatusModel> register(@Field("username") String username, @Field("mobile") String mobile,
                                           @Field("firstname") String firstname, @Field("lastname") String lastname, @Field("email") String email, @Field("password") String password, @Field("refered_by")String refered_by);
    @FormUrlEncoded
    @POST("user/check_username")
    Call<UserCreationStatusModel> check(@Field("username") String username);

    @FormUrlEncoded
    @POST("user/verify_mobile")
    Call<UserCreationStatusModel> OTP(@Field("mobile") String mobile, @Field("status_val") String status_val);

    @FormUrlEncoded
    @POST("user/update_profile")
    Call<UserCreationStatusModel> verification(@Field("data") String data, @Field("session") String session);

    @FormUrlEncoded
    @POST("user/forgot_password")
    Call<UserCreationStatusModel> forgot_password(@Field("email") String email);

    @FormUrlEncoded
    @POST("user/reset_password")
    Call<UserCreationStatusModel> reset_password(@Field("email") String email, @Field("code") String code, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/support_message")
    Call<UserCreationStatusModel> support(@Field("name") String name, @Field("email") String email, @Field("contact") String contact, @Field("message") String message, @Field("admin") String admin);

    @FormUrlEncoded
    @POST("user/resend_referral_code")
    Call<UserCreationStatusModel> resendmail(@Field("contact") String contact, @Field("email") String email, @Field("name") String name);


    @POST("login/advertisers")
    Call<StatusModel> loginAdvertiser (@Body AdveryiserLoginModel model);

    @POST("login/mediaowners")
    Call<StatusModel> loginMediaOwners (@Body AdveryiserLoginModel model);
    /*@FormUrlEncoded
    @POST("user/retrieve_referral_code")
    Call<CodeRetrievalModel> referral_code(@Field("session") String session, @Field("contact") String contact);

    @FormUrlEncoded
    @POST("user/update_profile")
    Call<UserCreationStatusModel> update_profile(@Field("data") String data, @Field("session") String session);


    @GET("login")
    Call<Profileget> get_details(@Query("mobile") String mobile, @Query("password") String password);
    @FormUrlEncoded
    @POST("user/login")
    Call<Profileget> login1(@Field("mobile") String mobile, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/get_user")
    Call<ProfileInfoCinsio> getUserProfile(@Field("session") String session, @Field("user_id") String userid);*/
}
