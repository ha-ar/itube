package co.vector.itube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;

import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.SignupService;

/**
 * Created by android on 11/10/14.
 */
public class SignupActivity extends Activity {

    private static final String TAG = "PayPalpayment";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AQniU0uc6MT5kS1JcA2nOWf5mbwfEOYlGhFuiXUTNTChQmtuR0grEkAZrOS9MaCaj7yYSDybpxRAWXOL";

    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);
    /////////////////////////////////////////////////////////////
    AQuery aq ;
    private static BaseClass baseClass;
    AsyncTask<Void, Void, Void> mRegisterTask;
    static GoogleCloudMessaging gcm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        //////////////////////////////////////////////////////////////////
        baseClass = ((BaseClass) getApplicationContext());
        if(baseClass.isTabletDevice(this))
        {
            setContentView(R.layout.signup_activity_tablet);
        }
        else {
            setContentView(R.layout.signup_activity);
        }
        if (!baseClass.isConnectingToInternet()) {
            // Internet Connection is not present
            baseClass.showAlertDialog(SignupActivity.this,
                    "Internet Connection Error",
                    "Please connect to Internet connection", false);

            return;
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
                } catch (NullPointerException e) {
                }
                if (aq.id(R.id.email).getText().toString().isEmpty()) {
                    Crouton.makeText(SignupActivity.this, "Enter Email.", Style.ALERT).show();
                    return;
                }
                if (aq.id(R.id.password).getText().toString().isEmpty()) {
                    Crouton.makeText(SignupActivity.this, "Enter Password.", Style.ALERT).show();
                    return;
                }
                if (aq.id(R.id.confirm_password).getText().toString().isEmpty()) {
                    Crouton.makeText(SignupActivity.this, "Enter Confirm Password.", Style.ALERT).show();
                    return;
                }
                if (!isEmailValid(aq.id(R.id.email).getText().toString())) {
                    Crouton.makeText(SignupActivity.this, "Enter Correct Email.", Style.ALERT).show();
                    return;
                }
                if (aq.id(R.id.password).getText().length() < 8) {
                    Crouton.makeText(SignupActivity.this, "Password should be at least 8 digits.", Style.ALERT).show();
                    return;
                }
                if (!aq.id(R.id.password).getText().toString().equalsIgnoreCase(aq.id(R.id.confirm_password).getText().toString())) {
                    Crouton.makeText(SignupActivity.this, "Password not matched.", Style.ALERT).show();
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
                if (!baseClass.getGCM_Key().isEmpty()) {
                    onBuyPressed();
//                    SignupService obj = new SignupService(SignupActivity.this);
//                    obj.signup(aq.id(R.id.email).getText().toString(),
//                            aq.id(R.id.password).getText().toString(),
//                            aq.id(R.id.confirm_password).getText().toString(), baseClass.getGCM_Key(), true, new CallBack(SignupActivity.this, "ConfirmSignup"));
                } else {
                    Crouton.makeText(SignupActivity.this, "Try again in few seconds", Style.ALERT).show();
                }
            }
        });
    }
    public void onBuyPressed() {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(SignupActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("1.75"), "USD", "sample item",
                paymentIntent);
    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Toast.makeText(
                                getApplicationContext(),
                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
                                .show();

                        SignupService obj = new SignupService(SignupActivity.this);
                        obj.signup(aq.id(R.id.email).getText().toString(),
                                aq.id(R.id.password).getText().toString(),
                                aq.id(R.id.confirm_password).getText().toString(), baseClass.getGCM_Key(), true, new CallBack(SignupActivity.this, "ConfirmSignup"));

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }

        }
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
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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
