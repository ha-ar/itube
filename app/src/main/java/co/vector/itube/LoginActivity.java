package co.vector.itube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.androidquery.AQuery;

import Models.AdsMessage;
import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.ExpiryUpdateService;
import services.LoginService;

public class LoginActivity extends Activity  {
    private static final String TAG = "Android BillingService";
    AQuery aq;
    private String expiryUpdate = "43800";

    private BaseClass baseClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseClass = ((BaseClass) getApplicationContext());
        if(baseClass.isTabletDevice(this))
        {
            setContentView(R.layout.activity_login_tablet);
        }
        else {
            setContentView(R.layout.activity_login);
        }

        startService(new Intent(getApplicationContext(), BillingService.class));
        BillingHelper.setCompletedHandler(mTransactionHandler);

//        if(!baseClass.getAUTH_TOKEN().equalsIgnoreCase(""))
//        {
//            startActivity(new Intent(LoginActivity.this,BaseActivity.class));
//        }

        final Button Register = (Button) findViewById(R.id.register_here);
        final Button Signin = (Button) findViewById(R.id.email_sign_in_button);
        aq = new AQuery(LoginActivity.this);

        aq.id(R.id.email_sign_in_button).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Signin.getWindowToken(), 0);
                }catch(NullPointerException e){}
                if(aq.id(R.id.email).getText().toString().isEmpty())
                {
                    Crouton.makeText(LoginActivity.this,"Enter Email.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.password).getText().toString().isEmpty())
                {
                    Crouton.makeText(LoginActivity.this,"Enter Password.",Style.ALERT).show();
                    return;
                }
                if(!isEmailValid(aq.id(R.id.email).getText().toString()))
                {
                    Crouton.makeText(LoginActivity.this,"Enter Correct Email.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.password).getText().length() < 8)
                {
                    Crouton.makeText(LoginActivity.this,"Password should be at least 8 digits.",Style.ALERT).show();
                    return;
                }
                LoginService obj = new LoginService(LoginActivity.this);
                obj.login(aq.id(R.id.email).getText().toString(),
                        aq.id(R.id.password).getText().toString(),true, new CallBack(LoginActivity.this, "ConfirmLogin"));
            }
        });
        aq.id(R.id.register_here).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Register.getWindowToken(), 0);
               startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

    }

    public void ConfirmLogin(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            baseClass.setAUTH_TOKEN(UserModel.getInstance().auth_token);  //in case of expiry
            if (UserModel.getInstance().expire.equalsIgnoreCase("false")) {
                baseClass.setAUTH_TOKEN(UserModel.getInstance().user.auth_token);
                baseClass.setCheckDuration(Long.parseLong(UserModel.getInstance().user.duration));
                startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                LoginActivity.this.finish();
            } else {
                Crouton.makeText(this, "Trail Expired,Please get Subscription.", Style.ALERT).show();
                new AlertDialog.Builder(this)
                        .setMessage("Trail Expired,Please get Subscription.")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(BillingHelper.isBillingSupported()){
                                        BillingHelper.requestPurchase(getApplicationContext(), "android.test.purchased");
                                        } else {
                                            Log.i(TAG,"Can't purchase on this device");
                                        }
                                    }
                                }).setNegativeButton("No", null).show();
            }
        } else {
            Crouton.makeText(this, "Invalid Username/Password", Style.ALERT).show();
        }
    }

    public Handler mTransactionHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                Log.i(TAG, "Transaction complete");
                ExpiryUpdateService obj = new ExpiryUpdateService(LoginActivity.this);
                obj.expiryUpdate(expiryUpdate,true, baseClass.getAUTH_TOKEN(), new CallBack(LoginActivity.this, "expiryUpdate"));

//                Log.i(TAG, "Transaction status: " + BillingHelper.latestPurchase.purchaseState);
//                Log.i(TAG, "Item purchased is: " + BillingHelper.latestPurchase.productId);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        };

    };
    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public void expiryUpdate(Object caller, Object model) {
        AdsMessage.getInstance().setList((AdsMessage) model);

        Crouton.makeText(LoginActivity.this, AdsMessage.getInstance().message, Style.INFO).show();
    }

    @Override
    public void onBackPressed() {
        try {
            BillingHelper.stopService();
        }catch (NullPointerException e){}
        super.onBackPressed();

    }

}



