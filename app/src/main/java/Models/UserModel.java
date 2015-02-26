package Models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    private static UserModel _obj = null;

    private UserModel() {

    }

    public static UserModel getInstance() {
        if (_obj == null) {
            _obj = new UserModel();
        }
        return _obj;
    }

    public void setList(UserModel obj) {
        _obj = obj;
    }


    @SerializedName("success")
    public String success;

    @SerializedName("expire")
    public String expire;

    @SerializedName("message")
    public String message;

    @SerializedName("user")
    public User user = new User();


    public class User {
        @SerializedName("auth_token")
        public String auth_token;

        @SerializedName("duration")
        public String duration;
    }
}
