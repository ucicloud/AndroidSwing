package com.kidsdynamic.swing;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.bobomee.android.htttp.retrofit2.Retrofit2Client;
import com.kidsdynamic.data.net.user.UserApi;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.UpdateProfileEntity;
import com.kidsdynamic.data.utils.LogUtil2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    @BindView(R.id.button_test_login)
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


    }

    @OnClick(R.id.button_test_login)
    void loginTest(){
        UserApi userApi = Retrofit2Client.INSTANCE.getRetrofitBuilder()
                .baseUrl(UserApi.BASE_URL).build()
                .create(UserApi.class);

        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setEmail("only_aap@163.com");
        loginEntity.setPassword("123123");
        userApi.login(loginEntity).enqueue(new Callback<LoginSuccessRep>() {
            @Override
            public void onResponse(Call<LoginSuccessRep> call, Response<LoginSuccessRep> response) {
                LogUtil2.getUtils().d("login onResponse");

                if(response.code() == 200){
                    LogUtil2.getUtils().d("login success");
                    LogUtil2.getUtils().d(response.body().getUsername());

                }else{
                    LogUtil2.getUtils().d("login error, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginSuccessRep> call, Throwable t) {
                LogUtil2.getUtils().d("login onFailure");
                LogUtil2.getUtils().d(t.getMessage());
            }
        });


        userApi.updateProfile(new UpdateProfileEntity()).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)
                } else {
                    // parse the response body …
//                    APIError error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
//                    Log.d("error message", error.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }


    public class ErrorUtils {

        //https://futurestud.io/tutorials/retrofit-2-simple-error-handling
        /*public static APIError parseError(Response<?> response) {
            Converter<ResponseBody, APIError> converter =
                    ServiceGenerator.retrofit()
                            .responseBodyConverter(APIError.class, new Annotation[0]);

            APIError error;

            try {
                error = converter.convert(response.errorBody());
            } catch (IOException e) {
                return new APIError();
            }

            return error;
        }*/
    }
}
