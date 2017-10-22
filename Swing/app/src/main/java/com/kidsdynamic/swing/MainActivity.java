package com.kidsdynamic.swing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.avatar.AvatarApi;
import com.kidsdynamic.data.net.avatar.PartUtils;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.kids.KidsApi;
import com.kidsdynamic.data.net.kids.model.KidsAddRequest;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.UserApiNoNeedToken;
import com.kidsdynamic.data.net.user.model.InternalErrMsgEntity;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.RegisterEntity;
import com.kidsdynamic.data.net.user.model.RegisterFailResponse;
import com.kidsdynamic.data.net.user.model.UpdateKidAvatarRepEntity;
import com.kidsdynamic.data.net.user.model.UpdateProfileEntity;
import com.kidsdynamic.data.net.user.model.UpdateProfileSuccess;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.presenter.ActivityTest;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    void loginTest2(){
        //show waiting dialog

        String email = "only_app@163.com";
        String psw = "123456";

        final LoginEntity loginEntity = new LoginEntity();
        loginEntity.setEmail(email);
        loginEntity.setPassword(psw);

        //查询账户是否注册；如果未注册则展示注册界面（输入last name, first name;avatar）;注册成功后，再次执行
        //登录流程；登录成功后，开始同步数据（同步那些？）
        final UserApiNoNeedToken userApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi(UserApiNoNeedToken.class,false);

        userApi.checkEmailAvailableToRegister(email).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                LogUtil2.getUtils().d("check mail onResponse code: " + response.code());

                if (response.code() == 200) {
                    //邮箱未注册，则展示注册界面
                    showRegisterUI();

                }else if(response.code() == 409){
                    //邮箱已经注册，则执行登录流程
                    exeLogin(userApi,loginEntity);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                //dismiss waiting dialog,show error; terminate exe
                t.printStackTrace();
                LogUtil2.getUtils().d("check mail fail, " );
            }
        });

    }

    @OnClick(R.id.btn_register)
    protected void showRegisterUI() {
        //dismiss wait dialog, show register UI
        // TODO: 2017/10/17

        String email = "123@qq.com";
        String psw = "123456";
        register(email, psw);
    }

    void exeLogin(UserApiNoNeedToken userApi, LoginEntity loginEntity){

        userApi.login(loginEntity).enqueue(new Callback<LoginSuccessRep>() {
            @Override
            public void onResponse(Call<LoginSuccessRep> call, Response<LoginSuccessRep> response) {
                //

                if(response.code() == 200){
                    LogUtil2.getUtils().d("login success");
                    LogUtil2.getUtils().d(response.body().getAccess_token());
                    //缓存token
                    new LoginManager().cacheToken(response.body().getAccess_token());

                    //同步数据
                    syncData();

                }else{
                    LogUtil2.getUtils().d("login error, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginSuccessRep> call, Throwable t) {
                //fail, dismiss dialog, show error info
                // TODO: 2017/10/17
            }
        });

    }

    private void syncData() {
        // TODO: 2017/10/17 同步账户数据 成功后关闭等待对话框，跳转到主界面；失败提醒
        Toast.makeText(this,"login ok, start sync data", Toast.LENGTH_SHORT).show();

        //登陆成功后，需要获取两个业务数据：
        //“/v1/user/retrieveUserProfile”
        //“/v1/event/retrieveAllEventsWithTodo”

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(this.getApplicationContext()).
                generateApi(UserApiNeedToken.class,true);

        userApiNeedToken.retrieveUserProfile().enqueue(new Callback<UserProfileRep>() {
            @Override
            public void onResponse(Call<UserProfileRep> call, Response<UserProfileRep> response) {
                LogUtil2.getUtils().d("onResponse: " + response.code());
                if(response.code() == 200){
                    LogUtil2.getUtils().d("onResponse: " + response.body());

                    //todo save to db
                    //继续获取信息
                    getKidInfos();

                }else {
                    //todo dismiss dialog
                    LogUtil2.getUtils().d("onResponse not 200" );
                }
            }

            @Override
            public void onFailure(Call<UserProfileRep> call, Throwable t) {
                //todo dismiss dialog
                LogUtil2.getUtils().d("retrieveUserProfile error, ");
                t.printStackTrace();
            }
        });
    }

    private void getKidInfos() {
        LogUtil2.getUtils().d("getKidInfos ");
        final EventApi eventApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi(EventApi.class,true);

        eventApi.retrieveAllEventsWithTodo().enqueue(new Callback<List<EventWithTodo>>() {
            @Override
            public void onResponse(Call<List<EventWithTodo>> call, Response<List<EventWithTodo>> response) {
                if(response.code() == 200){
                    LogUtil2.getUtils().d("onResponse: " + response.body());

                    //todo dismiss dialog save to db
                    Toast.makeText(MainActivity.this," sync data ok, show next UI", Toast.LENGTH_SHORT).show();

                }else{
                    LogUtil2.getUtils().d("login error, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventWithTodo>> call, Throwable t) {
                //todo dismiss dialog
                LogUtil2.getUtils().d("retrieveAllEventsWithTodo error, ");
                t.printStackTrace();
            }
        });
    }


    public void register(@NonNull final String email, @NonNull final String psw){
        //使用用户数据，执行注册，如果注册成功则进行登录，登录成功执行同步数据
        final RegisterEntity registerEntity = new RegisterEntity();
        registerEntity.setEmail(email);
        registerEntity.setPassword(psw);
        registerEntity.setFirstName("firstName");
        registerEntity.setLastName("LastName");
        registerEntity.setPhoneNumber("18567891234");
        registerEntity.setZipCode("12345");

        final UserApiNoNeedToken userApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi(UserApiNoNeedToken.class,false);

        userApi.registerUser(registerEntity).enqueue(new Callback<RegisterFailResponse>() {
            @Override
            public void onResponse(Call<RegisterFailResponse> call, Response<RegisterFailResponse> response) {
                LogUtil2.getUtils().d("register onResponse");
                int code = response.code();
                if(code == 200){
                    //
                    LogUtil2.getUtils().d("register ok start login");

                    LoginEntity loginEntity = new LoginEntity();
                    loginEntity.setEmail(email);
                    loginEntity.setPassword(psw);
                    exeLogin(userApi,loginEntity);
                }else{
                    LogUtil2.getUtils().d("register fail code: " + code);
                    // TODO: 2017/10/17 dismiss dialog, show error msg

                }
            }

            @Override
            public void onFailure(Call<RegisterFailResponse> call, Throwable t) {
                //todo fail, dismiss dialog, show error info
                LogUtil2.getUtils().d("register error, code: ");
                t.printStackTrace();
            }
        });
    }

    void loginTest(){
       /* UserApi userApi = Retrofit2Client.INSTANCE.getRetrofitBuilder()
                .baseUrl(UserApi.BASE_URL).build()
                .create(UserApi.class);*/

        UserApiNoNeedToken userApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi(UserApiNoNeedToken.class,false);

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


        userApi.updateProfile(new UpdateProfileEntity()).enqueue(new Callback<UpdateProfileSuccess>() {
            @Override
            public void onResponse(Call<UpdateProfileSuccess> call, Response<UpdateProfileSuccess> response) {

                //code >= 200 && code < 300
                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)

                    response.body().getUser();
                    //show msg
                } else {
                    // parse the response body …
//                    APIError error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
//                    Log.d("error message", error.message());

                    if(response.code() == 400){
                        //bad request, missing some parameters
                    }else if(response.code() == 500){
                        //internal error
                        Gson gson = new Gson();
                        InternalErrMsgEntity internalErrMsgEntity = gson.fromJson(gson.newJsonReader(response.errorBody().charStream()),
                                InternalErrMsgEntity.class);


                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileSuccess> call, Throwable t) {

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

//    @OnClick(R.id.btn_upload_avatar)
    public void uploadAvatar(){
//        "upload"
        final AvatarApi avatarApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi4Avatar(AvatarApi.class);

        String filePath = "/sdcard/1.jpg";
        MultipartBody.Part filePart = PartUtils.prepareFilePart("upload", "avatar_t01", new File(filePath));

        avatarApi.uploadUserAvatar(filePart).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                LogUtil2.getUtils().d("uploadUserAvatar onResponse");
                LogUtil2.getUtils().d("uploadUserAvatar  code: " + response.code());
                //code == 200 upload ok
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                LogUtil2.getUtils().d("uploadUserAvatar onFailure");
                t.printStackTrace();
            }
        });
    }

    @OnClick(R.id.btn_upload_avatar)
    public void uploadKidAvatar(){
//        "upload"
        final AvatarApi avatarApi = ApiGen.getInstance(this.getApplicationContext()).
                generateApi4Avatar(AvatarApi.class);

        String filePath = "/sdcard/1.jpg";
        MultipartBody.Part filePart = PartUtils.prepareFilePart("upload", "avatar_t01", new File(filePath));

        avatarApi.uploadKidAvatar(getUploadKidAvatarPram(),filePart).enqueue(new Callback<UpdateKidAvatarRepEntity>() {
            @Override
            public void onResponse(Call<UpdateKidAvatarRepEntity> call, Response<UpdateKidAvatarRepEntity> response) {
                LogUtil2.getUtils().d("uploadKidAvatar onResponse");
                LogUtil2.getUtils().d("uploadKidAvatar  code: " + response.code());
                //code == 200 upload ok
            }

            @Override
            public void onFailure(Call<UpdateKidAvatarRepEntity> call, Throwable t) {
                LogUtil2.getUtils().d("uploadKidAvatar onFailure");
                t.printStackTrace();
            }
        });
    }

    private Map<String, RequestBody> getUploadKidAvatarPram(){
        HashMap<String, RequestBody> paramMap = new HashMap<>();
        PartUtils.putRequestBodyMap(paramMap,AvatarApi.param_kidId,"123");

        return paramMap;
    }

    @OnClick(R.id.btn_kids_is_bind)
    public void checkWatchBindStatus(){
        String watchMacId = "hgweorahgbkljwhnpi2";
        KidsApi kidsApi = ApiGen.getInstance(this.getApplication()).
                generateApi(KidsApi.class, true);

        kidsApi.whoRegisteredMacID(watchMacId).enqueue(new Callback<KidsWithParent>() {
            @Override
            public void onResponse(Call<KidsWithParent> call, Response<KidsWithParent> response) {
                LogUtil2.getUtils().d("whoRegisteredMacID onResponse: " + response.code());

                if (response.code() == 200) {
                    LogUtil2.getUtils().d("watch binder: ");
                    LogUtil2.getUtils().d("watch binder info: " + response.body().getName());
                    LogUtil2.getUtils().d("watch binder info: " + response.body().getParent().getFirstName());
                }else if(response.code() == 404){
                    LogUtil2.getUtils().d("watch not binde: ");
                }
            }

            @Override
            public void onFailure(Call<KidsWithParent> call, Throwable t) {
                LogUtil2.getUtils().d("whoRegisteredMacID onFailure");
                t.printStackTrace();
            }
        });
    }

    @OnClick(R.id.btn_kids_add)
    public void addKids(){
        String watchMacId = "hgweorahgbkljwhnpi2";
        String kidsName = "yy1";

        KidsApi kidsApi = ApiGen.getInstance(this.getApplication()).
                generateApi(KidsApi.class, true);

        KidsAddRequest kidsAddRequest = new KidsAddRequest();
        kidsAddRequest.setMacId(watchMacId);
        kidsAddRequest.setName(kidsName);

        kidsApi.addKid(kidsAddRequest).enqueue(new Callback<KidsWithParent>() {
            @Override
            public void onResponse(Call<KidsWithParent> call, Response<KidsWithParent> response) {
                LogUtil2.getUtils().d("addKid onResponse: " + response.code());

                if(response.code() == 200){
                    //add successfully
                    LogUtil2.getUtils().d("addKid rep kid ID: " + response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<KidsWithParent> call, Throwable t) {
                LogUtil2.getUtils().d("addKid onFailure");
                t.printStackTrace();
            }
        });
    }

    @OnClick(R.id.btn_show_main)
    public void showMain(){
        startActivity(new Intent(this, ActivityTest.class));
    }
}
