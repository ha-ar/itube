package services;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import Models.UserModel;


/**
 * Created by android on 10/10/14.
 */
public class LoginService extends BaseService {

    public LoginService(Context ctx) {
        super(ctx);
    }

    public void login(String mEmail, String mPassword,boolean message, CallBack obj){
        String url = Constants.BASE_URL + Constants.LOGIN_API;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user[email]", mEmail);
        params.put("user[password]", mPassword);
        this.post(url, params, obj, UserModel.getInstance(), message);

        Log.e("LoginService",url);
    }

    public void forgorPassword(String mEmail,boolean message, CallBack obj){
        String url = Constants.BASE_URL+"forgot_password?email="+mEmail;
        Log.e("forgorPassword",url);
        this.get(url, obj, UserModel.getInstance(), message);
    }
    public void isActive(String mEmail,boolean message, CallBack obj){
        String url = Constants.BASE_URL+"is_active?email="+mEmail;
        Log.e("isActive",url);
        this.get(url, obj, UserModel.getInstance(), message);
    }
    public void Activated(String mEmail,String code,boolean message, CallBack obj){
        String url = Constants.BASE_URL+"verify_activation_code?email="+mEmail+"&activation_code="+code;
        Log.e("Activated",url);
        this.get(url, obj, UserModel.getInstance(), message);
    }
}
