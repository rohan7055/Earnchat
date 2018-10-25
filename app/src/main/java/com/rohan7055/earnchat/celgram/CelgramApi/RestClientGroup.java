package com.rohan7055.earnchat.celgram.CelgramApi;

import com.rohan7055.earnchat.celgram.group_dp_model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.celgram.ResponseModels.GetGroupInfo;
import com.rohan7055.earnchat.celgram.ResponseModels.GroupDpUpload;
import com.rohan7055.earnchat.celgram.ResponseModels.GroupUpdation;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ron on 4/8/2017.
 */

public class RestClientGroup {

    private static ApiGroupInterface apiGroupInterface;

    public static ApiGroupInterface getClient() {
        if (apiGroupInterface == null) {

            OkHttpClient okClient = new OkHttpClient.Builder()
                   .connectTimeout(10000, TimeUnit.SECONDS)
                    .readTimeout(10000, TimeUnit.SECONDS)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppConstants.UPLOADS_URL_GROUP)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiGroupInterface = client.create(RestClientGroup.ApiGroupInterface.class);
        }
        return apiGroupInterface ;
    }


    public interface ApiGroupInterface {
        @Multipart
        @POST("group_dp_upload")
        Call<GroupDpUpload> uploadDpPost( @Part("groupId") RequestBody groupId,@Part("session") RequestBody session, @Part MultipartBody.Part file_name);

        @FormUrlEncoded
        @POST("upload_group_dp.php")
        Call<group_dp_model> uploadDP(@Field("title") String title, @Field("image") String image);

        @FormUrlEncoded
        @POST("upload")
        Call<group_dp_model> uploadDP2(@Field("title") String title, @Field("image") String image);

        @FormUrlEncoded
        @POST("get_group_info")
        Call<GetGroupInfo> getGroupInfo(@Field("session") String session,@Field("groupId") String groupId);

        @FormUrlEncoded
        @POST("update_group")
        Call<GroupUpdation> groupInfoUpdate(@Field("session") String session,@Field("data") String data,@Field("groupId") String groupId);


    }
}
