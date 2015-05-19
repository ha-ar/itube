package co.vector.itube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.androidquery.AQuery;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import Models.AdsMessage;
import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.ExpiryUpdateService;
import services.LoginService;

public class LoginActivity extends Activity implements BillingProcessor.IBillingHandler{
    private static final String TAG = "Android BillingService";
    AQuery aq;
    LoginService obj;
    private String expiryUpdate = "43800";
    BillingProcessor bp;
    private String inAppKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt1bneditxo/p+q3yffJlUFO1X+M8r5G+WjpHOjccBD0gkHYC7YfwVDwJA0UUMRbSO/dnfYT38LIxMOJBnAK+PLGv1N3kciDDZamGOADZtC1gW9eTlMM3OWkc7KUxvltfk2miHpG8elM9ZGl1zPoLGmgkg3DXKz+IjVsVXt2I8OTt/vqSn7zeezWANfVTqlakFuiN1pXoo76/ER87g8gM9HLpysenbRNBAIvvJcPQog0Uu+ol4csLtvSmSPY4OmMrfPml8lfJh5cF1Uov8cTSvMuC+muAttXiZAIUoD8eU3laDJvdZsee5pbr2Z2dFQ3ZJAmkm33lkQuGKay7FNv7iQIDAQAB";

    private BaseClass baseClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj = new LoginService(LoginActivity.this);
        baseClass = ((BaseClass) getApplicationContext());
        if(BaseClass.isTabletDevice(this))
        {
            setContentView(R.layout.activity_login_tablet);
        }
        else {
            setContentView(R.layout.activity_login);
        }

        bp = new BillingProcessor(this, inAppKey, this);

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
                    Crouton.makeText(LoginActivity.this,"Please Enter Valid Email.",Style.ALERT).show();
                    return;
                }
                if(aq.id(R.id.password).getText().length() < 8)
                {
                    Crouton.makeText(LoginActivity.this,"Password should be at least 8 digits.",Style.ALERT).show();
                    return;
                }

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
        aq.id(R.id.forgot_password).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ActivityForgotPassword.class));
            }
        });


    }
    public void IsActive(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            startActivity(new Intent(LoginActivity.this, BaseActivity.class));
            LoginActivity.this.finish();
        }
        else {
            startActivity(new Intent(LoginActivity.this, ActivityActivationCode.class));
            LoginActivity.this.finish();
        }
    }
    public void ConfirmLogin(Object caller, Object model) {
        UserModel.getInstance().setList((UserModel) model);
        if (UserModel.getInstance().success.equalsIgnoreCase("true")) {
            baseClass.setAUTH_TOKEN(UserModel.getInstance().auth_token);  //in case of expiry
            if (UserModel.getInstance().expire.equalsIgnoreCase("false")) {
                baseClass.setAUTH_TOKEN(UserModel.getInstance().user.auth_token);
                baseClass.setEmail(aq.id(R.id.email).getText().toString());
                baseClass.setCheckDuration(Long.parseLong(UserModel.getInstance().user.duration));
                obj.isActive(aq.id(R.id.email).getText().toString(), true, new CallBack(LoginActivity.this, "IsActive"));
            } else {
                Crouton.makeText(this, "Trail Expired,Please get Subscription.", Style.ALERT).show();
                new AlertDialog.Builder(this)
                        .setMessage("Trail Expired,Please get Subscription.")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        bp.purchase(LoginActivity.this, "android.test.purchased");

                                    }
                                }).setNegativeButton("No", null).show();
            }
        } else {
            Crouton.makeText(this, "Invalid Username/Password", Style.ALERT).show();
        }
    }

    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public void expiryUpdate(Object caller, Object model) {
        AdsMessage.getInstance().setList((AdsMessage) model);
        Crouton.makeText(LoginActivity.this, AdsMessage.getInstance().message, Style.INFO).show();
    }

    @Override
    public void onBillingInitialized() {
        Log.e("Billing", "billing init");
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e("Billing", "billing purchased");
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        ExpiryUpdateService obj = new ExpiryUpdateService(LoginActivity.this);
        obj.expiryUpdate(expiryUpdate, true, baseClass.getAUTH_TOKEN(), new CallBack(LoginActivity.this, "expiryUpdate"));

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("error code", errorCode+"");
        Crouton.makeText(LoginActivity.this, "Oops! Something went wrong.", Style.ALERT).show();
        /*
         * Called when some error occurred. See Constants class for more details
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}



