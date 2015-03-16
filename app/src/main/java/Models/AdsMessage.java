package Models;

import com.google.gson.annotations.SerializedName;

public class AdsMessage {
    private static AdsMessage _obj = null;

    private AdsMessage() {

    }

    public static AdsMessage getInstance() {
        if (_obj == null) {
            _obj = new AdsMessage();
        }
        return _obj;
    }

    public void setList(AdsMessage obj) {
        _obj = obj;
    }


    @SerializedName("success")
    public String success;

    @SerializedName("message")
    public String message;
}
