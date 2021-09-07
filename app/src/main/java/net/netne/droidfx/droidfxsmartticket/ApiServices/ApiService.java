package net.netne.droidfx.droidfxsmartticket.ApiServices;

/**
 * Created by R$ on 5/7/2017.
 */

import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel2;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiService {
    @FormUrlEncoded
    @POST("transactions.php")
    Call<InsertDataResponseModel> insertData(@Field("device") String device, @Field("bus") String bus, @Field("route") String route, @Field("driver") String driver, @Field("taguid") String taguid, @Field("time") String tim, @Field("location") String loc, @Field("status") String status, @Field("totalkm") String totalkm, @Field("totalcost") String totalcost);
}
