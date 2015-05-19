package co.vector.itube;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.LoginService;

/**
 * Created by android on 3/17/15.
 */
public class ActivityForgotPassword extends Activity {
    AQuery aq;
    LoginService obj;

    private BaseClass baseClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj = new LoginService(ActivityForgotPassword.this);
        baseClass = ((BaseClass) getApplicationContext());
        if(baseClass.isTabletDevice(ActivityForgotPassword.this))
        {
            setContentView(R.layout.fragment_forgotpassword_tablet);
        }
        else {
            setContentView(R.layout.fragment_forgotpassword);
        }
        obj = new LoginService(this);
        aq = new AQuery(ActivityForgotPassword.this);
        aq.id(R.id.email_sign_in_button).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aq.id(R.id.email).getText().toString().isEmpty())
                {
                    Crouton.makeText(ActivityForgotPassword.this, "Enter Email.", Style.ALERT).show();
                    return;
                }
                if(!isEmailValid(aq.id(R.id.email).getText().toString()))
                {
                    Crouton.makeText(ActivityForgotPassword.this,"Please Enter Valid Email.",Style.ALERT).show();
                    return;
                }
                obj.forgorPassword(aq.id(R.id.email).getText().toString(),true, new CallBack(ActivityForgotPassword.this, "PasswordChanged"));
            }
        });
    }
    public void PasswordChanged(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            Crouton.makeText(ActivityForgotPassword.this, "An Email sent to you", Style.CONFIRM).show();
        }
        else
        {
            Crouton.makeText(ActivityForgotPassword.this, "User not exist", Style.ALERT).show();
        }
    }
    public boolean isEmailValid(String email) {
        return email.contains("@");
    }
}