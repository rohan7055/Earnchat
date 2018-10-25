package com.rohan7055.earnchat.celgram.CelgramApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.Model.StatusModel;
import com.rohan7055.earnchat.Model.StatusModelSession;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.GetGroupInfoNode;
import com.rohan7055.earnchat.celgram.ResponseModels.GetGroupWrapper;
import com.rohan7055.earnchat.celgram.ResponseModels.GroupDpUpload;
import com.rohan7055.earnchat.celgram.ResponseModels.GroupUpdation;
import com.rohan7055.earnchat.celgram.ResponseModels.RegisterResponse;
import com.rohan7055.earnchat.celgram.ResponseModels.Result;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class RestClientS3Group {
    private static RestClientS3Group.ApiGroupInterface apiGroupInterface;

    public static RestClientS3Group.ApiGroupInterface getClient() {
        if (apiGroupInterface == null) {



            OkHttpClient okClient = new OkHttpClient.Builder()
                    .connectTimeout(10000, TimeUnit.SECONDS)
                    .readTimeout(10000, TimeUnit.SECONDS)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppConstants.AWS_BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiGroupInterface = client.create(RestClientS3Group.ApiGroupInterface.class);
        }
        return apiGroupInterface ;
    }

    private static OkHttpClient getHttpClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient = okHttpClient.newBuilder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

        return okHttpClient;
    }


    public interface ApiGroupInterface {

        @FormUrlEncoded
        @POST("checkcontacts")
        Call<CheckContacts> checkContacts(@Field("contacts") String contacts);

        @Multipart
        @POST("upload")
        Call<GroupDpUpload> uploadDpPost(@Part("groupId") RequestBody groupId, @Part("sender_ID") RequestBody senderID, @Part MultipartBody.Part file_name);

        @Multipart
        @POST("registeruser")
        Call<RegisterResponse> register(@Part("user_id") RequestBody userid,
                                        @Part("firstname") RequestBody firstname,
                                        @Part("lastname") RequestBody lastname,
                                        @Part("session") RequestBody session,
                                        @Part("username") RequestBody username,
                                        @Part MultipartBody.Part file_name);
        @Multipart
        @POST("registeruser")
        Call<RegisterResponse> register(@Part("user_id") RequestBody userid,
                                        @Part("firstname") RequestBody firstname,
                                        @Part("lastname") RequestBody lastname,
                                        @Part("session") RequestBody session,
                                        @Part("username") RequestBody username);
        @Multipart
        @POST("uploadprofile")
        Call<RegisterResponse> uploadProfile(@Part("user_id") RequestBody userid,
                                             @Part MultipartBody.Part file_name);
        @FormUrlEncoded
        @POST("checkuser")
        Call<StatusModelSession> checkUser(@Field("user_id")String mobile);

        @FormUrlEncoded
        @POST("updateusername")
        Call<StatusModelSession> updateusername(@Field("user_id")String mobile,@Field("username") String username);

        @FormUrlEncoded
        @POST("updatestatus")
        Call<StatusModelSession> updatestatus(@Field("user_id")String mobile,@Field("status") String status);

        @FormUrlEncoded
        @POST("updatesession")
        Call<StatusModelSession> updatesession(@Field("user_id")String mobile,@Field("session") String session);


        @FormUrlEncoded
        @POST("alertuser")
        Call<StatusModelSession> alertuser(@Field("user_id")String mobile,@Field("session")String oldsession,@Field("new_session")String newsession);



        @POST("getgroupinfo")
        Call<GetGroupInfoNode> getGroupInfo(@Body GetGroupWrapper getGroupWrapper);

        @FormUrlEncoded
        @POST("addmember")
        Call<StatusModel> addMembers(@Field("uniquegroupid") String groupid, @Field("users")String users);

        @FormUrlEncoded
        @POST("update_group")
        Call<GroupUpdation> groupInfoUpdate(@Field("session") String session, @Field("data") String data, @Field("groupId") String groupId);


    }
}
