package net.netne.droidfx.droidfxsmartticket.ApiServices;

/**
 * Created by R$ on 5/7/2017.
 */

import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel2;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiService2 {
    @FormUrlEncoded
    @POST("busreports.php")
    Call<InsertDataResponseModel2> insertData2(@Field("taguid") String taguid, @Field("name") String name, @Field("amount") String amount);
}
