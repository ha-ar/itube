package services;

import android.content.Context;

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
    }
}
