package co.vector.itube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.androidquery.AQuery;

import net.simonvt.menudrawer.MenuDrawer;

import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.SignupService;

/**
 * Created by android on 11/10/14.
 */
public class SignupActivity extends Activity {
    AQuery aq ;
    private BaseClass baseClass;
    private MenuDrawer mDrawerLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseClass = ((BaseClass) getApplicationContext());
        if(baseClass.isTabletDevice(this))
        {
            setContentView(R.layout.signup_activity_tablet);
        }
        else {
            setContentView(R.layout.signup_activity);
        }
        final Button Signup = (Button) findViewById(R.id.email_sign_in_button);
        baseClass = ((BaseClass) getApplicationContext());
        aq = new AQuery(SignupActivity.this);
        aq.id(R.id.signup).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromWindow(Signup.getWindowToken(), 0);
               }catch(NullPointerException e){}
                if(aq.id(R.id.email).getText().toString().isEmpty())
                {
                    Crouton.makeText(SignupActivity.this,"Enter Email.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.password).getText().toString().isEmpty())
                {
                    Crouton.makeText(SignupActivity.this,"Enter Password.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.confirm_password).getText().toString().isEmpty())
                {
                    Crouton.makeText(SignupActivity.this,"Enter Confirm Password.",Style.ALERT).show();
                    return;
                }
                if(!isEmailValid(aq.id(R.id.email).getText().toString()))
                {
                    Crouton.makeText(SignupActivity.this,"Enter Correct Email.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.password).getText().length() < 8)
                {
                    Crouton.makeText(SignupActivity.this,"Password should be at least 8 digits.",Style.ALERT).show();
                    return;
                }
                if(!aq.id(R.id.password).getText().toString().equalsIgnoreCase(aq.id(R.id.confirm_password).getText().toString()))
                {
                    Crouton.makeText(SignupActivity.this,"Password not matched.",Style.ALERT).show();
                    return;
                }
                SignupService obj = new SignupService(SignupActivity.this);
                obj.signup(aq.id(R.id.email).getText().toString(),
                        aq.id(R.id.password).getText().toString(),
                        aq.id(R.id.confirm_password).getText().toString(),true, new CallBack(SignupActivity.this, "ConfirmSignup"));
            }
        });
    }
    public void ConfirmSignup(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            baseClass.setAUTH_TOKEN(UserModel.getInstance().user.auth_token.toString());
           // Toast.makeText(getApplication(), "Registered Successfully.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            SignupActivity.this.finish();
        } else {
            Crouton.makeText(this, "Email already exists.", Style.ALERT).show();
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
