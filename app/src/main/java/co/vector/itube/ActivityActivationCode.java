package co.vector.itube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.LoginService;

/**
 * Created by android on 3/20/15.
 */
public class ActivityActivationCode extends Activity {
    AQuery aq;
    LoginService obj;

    private BaseClass baseClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj = new LoginService(ActivityActivationCode.this);
        baseClass = ((BaseClass) getApplicationContext());
        if(baseClass.isTabletDevice(ActivityActivationCode.this))
        {
            setContentView(R.layout.activity_activationcode_tablet);
        }
        else {
            setContentView(R.layout.activity_activationcode);
        }
        obj = new LoginService(this);
        aq = new AQuery(ActivityActivationCode.this);
        aq.id(R.id.email_sign_in_button).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aq.id(R.id.activation_code).getText().toString().isEmpty())
                {
                    Crouton.makeText(ActivityActivationCode.this, "Enter Activation Code.", Style.ALERT).show();
                    return;
                }
                obj.Activated(baseClass.getEmail(),aq.id(R.id.activation_code).getText().toString(),true, new CallBack(ActivityActivationCode.this, "Send"));
            }
        });
    }
    public void Send(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            startActivity(new Intent(ActivityActivationCode.this, BaseActivity.class));
            ActivityActivationCode.this.finish();
        }
        else
        {
            Crouton.makeText(ActivityActivationCode.this, "Invalid Activation Code", Style.ALERT).show();
        }
    }
}