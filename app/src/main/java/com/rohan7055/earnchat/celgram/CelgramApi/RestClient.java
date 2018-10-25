package com.rohan7055.earnchat.celgram.CelgramApi;

import com.rohan7055.earnchat.AppConstants;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts;
import com.rohan7055.earnchat.celgram.ResponseModels.CheckContacts8Chat;

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
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by manu on 8/28/2016.
 */
public class RestClient {
    private static ApiInterface apiInterface;

    public static ApiInterface getClient() {
        if (apiInterface == null) {



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
           //Rohan thakur
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppConstants.EIGHTCHAT_BASE_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = client.create(ApiInterface.class);
        }
        return apiInterface ;
    }


    public interface ApiInterface {
        @FormUrlEncoded
        @POST("check_contacts")
        Call<CheckContacts> checkContacts(@Field("session") String session,@Field("contacts") String contacts);

        //Rohan thakur
        @GET("advertisers")
        Call<CheckContacts8Chat> checkContacts8chatAdvertisers();
        //Rohan Thakur
        @GET("mediaowners")
        Call<CheckContacts8Chat> checkContacts8chatMediaOwners();
    }


}
