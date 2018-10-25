package com.rohan7055.earnchat.celgram.CelgramApi;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.celgram.ResponseModels.ChangeEmailModel;
import com.rohan7055.earnchat.celgram.ResponseModels.ChangePasswordModel;
import com.rohan7055.earnchat.celgram.ResponseModels.ChangeUsername;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckMagicContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.GetStatus;
import com.rohan7055.earnchat.celgram.ResponseModels.SetCelgramstatus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ron on 4/15/2017.
 */
public class RestClientUser
{
    private static ApiInterfaceUser apiInterfaceUser;

    public static ApiInterfaceUser getClient() {
        if (apiInterfaceUser == null) {



            OkHttpClient okClient = new OkHttpClient.Builder()
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Interceptor.Chain chain) throws IOException {
                                    Request request = chain.request().newBuilder()
                                            .addHeader("Accept", "Application/JSON").build();
                                    return chain.proceed(request);
                                }
                            }).build();


            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterfaceUser = client.create(ApiInterfaceUser.class);
        }
        return apiInterfaceUser ;
    }


    public interface ApiInterfaceUser {
        @FormUrlEncoded
        @POST("user/get_user_status")
        Call<GetStatus> getStatus(@Field("session") String session, @Field("user_id") String userid);

        @FormUrlEncoded
        @POST("user/set_user_status")
        Call<SetCelgramstatus> setStatus(@Field("session") String session,@Field("celgram_status") String celgram_status);

        @FormUrlEncoded
        @POST("user/get_user_info_magic")
        Call<CheckMagicContacts> getmagicData(@Field("session") String session, @Field("select") String select);


        @FormUrlEncoded
        @POST("user/change_username")
        Call<ChangeUsername> changeusername(@Field("session") String session, @Field("old_username") String oldusername,@Field("new_username")String newusername);


        @FormUrlEncoded
        @POST("user/change_email")
        Call<ChangeEmailModel> changeemail(@Field("session") String session, @Field("email") String email);


        @FormUrlEncoded
        @POST("user/change_password")
        Call<ChangePasswordModel> changepassword(@Field("session") String session, @Field("phone") String phone, @Field("password_old") String oldpass,
                                                 @Field("password_new") String newpass,
                                                 @Field("confirm_password") String confpass);
        @FormUrlEncoded
        @POST("user/logout")
        Call<StatusModel> logoutSession(@Field("session") String session);


    }
}
