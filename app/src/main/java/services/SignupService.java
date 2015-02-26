package services;

import android.content.Context;

import java.util.HashMap;

import Models.UserModel;

/**
 * Created by android on 11/11/14.
 */
public class SignupService extends BaseService {

    public SignupService(Context ctx) {
        super(ctx);
    }

    public void signup(String mEmail, String mPassword, String confirmPassword,boolean message, CallBack obj) {
        String url = Constants.BASE_URL + Constants.SIGNUP_API;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user[email]", mEmail);
        params.put("user[password]", mPassword);
        params.put("user[confirmation_password]", confirmPassword);
        this.post(url, params, obj, UserModel.getInstance(), message);
    }
}