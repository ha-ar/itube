package co.vector.itube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.androidquery.AQuery;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

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
    private static BaseClass baseClass;
    String regId;
    AsyncTask<Void, Void, Void> mRegisterTask;
    static GoogleCloudMessaging gcm;
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
        registerGCM(this);
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
                baseClass.setEmail(aq.id(R.id.email).getText().toString());
                //////////////////////////////
                if (!baseClass.isConnectingToInternet()) {
                    baseClass.showAlertDialog(SignupActivity.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                /////////////////////////////////////////////
                if(!baseClass.getGCM_Key().isEmpty()){
                    SignupService obj = new SignupService(SignupActivity.this);
                    obj.signup(aq.id(R.id.email).getText().toString(),
                            aq.id(R.id.password).getText().toString(),
                            aq.id(R.id.confirm_password).getText().toString(),baseClass.getGCM_Key(),true, new CallBack(SignupActivity.this, "ConfirmSignup"));
                }else{
                    Crouton.makeText(SignupActivity.this,"Try again in few seconds",Style.ALERT).show();
                }
            }
        });
    }
    public void ConfirmSignup(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            baseClass.setAUTH_TOKEN(UserModel.getInstance().user.auth_token);
//            GCMRegistration();
            // Toast.makeText(getApplication(), "Registered Successfully.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            SignupActivity.this.finish();
        } else {
            Crouton.makeText(this, "Email already exists.", Style.ALERT).show();
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    @Override
    protected void onDestroy() {
        // Cancel AsyncTask
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        super.onDestroy();
    }


    public void GCMRegistration()
    {
        ///////////////////////////////////////////////////////////////
        if (!baseClass.isConnectingToInternet()) {

            // Internet Connection is not present
            baseClass.showAlertDialog(SignupActivity.this,
                    "Internet Connection Error",
                    "Please connect to Internet connection", false);
            // stop executing code by return
            return;
        }


        // Make sure the device has the proper dependencies.
        //  GCMRegistrar.checkDevice(this);

        // Make sure the manifest permissions was properly set
        //  GCMRegistrar.checkManifest(this);


        // Register custom Broadcast receiver to show messages on activity
        // registerReceiver(mHandleMessageReceiver, new IntentFilter(
        //      Config.DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        if(baseClass.getGCM_Key().equalsIgnoreCase("")) {
//            regId = GCMRegistrar.getRegistrationId(this);
//            baseClass.setGCM_Key(regId);
//            GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
//            Log.e("ID", regId);

        }

        final Context context = this;
        mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                // Register on our server
                // On server creates a new user
                baseClass.register(context, baseClass.getAUTH_TOKEN(),  aq.id(R.id.email).getText().toString(), regId);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
            }

        };

        // execute AsyncTask
        mRegisterTask.execute(null, null, null);

        ////////////////////////////////////////////////////////////////////////////
    }

    static void registerGCM(final Context context)  {


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    baseClass.setGCM_Key(gcm.register(Config.GOOGLE_SENDER_ID));
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                Log.v("REGISTERID", "this is my reg id**********" + baseClass.getGCM_Key());
            }
        }.execute(null, null, null);
    }
}
