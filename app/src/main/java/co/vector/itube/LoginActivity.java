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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import Models.AdsMessage;
import Models.UserModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.LoginService;
import services.SignupService;

public class LoginActivity extends Activity {//implements  BillingProcessor.IBillingHandler{
    private static final String TAG = "Android BillingService";
    AQuery aq;
    LoginService obj;
    private String expiryUpdate = "43800";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AQniU0uc6MT5kS1JcA2nOWf5mbwfEOYlGhFuiXUTNTChQmtuR0grEkAZrOS9MaCaj7yYSDybpxRAWXOL";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);
    private static final int REQUEST_CODE_PAYMENT = 1;
//    BillingProcessor bp;
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

        //bp = new BillingProcessor(this, inAppKey, this);

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
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Register.getWindowToken(), 0);


                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
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
            Log.e("SUCCESS",UserModel.getInstance().success + "/" + UserModel.getInstance().expire);
            baseClass.setAUTH_TOKEN(UserModel.getInstance().auth_token);
            startActivity(new Intent(LoginActivity.this, BaseActivity.class));
            LoginActivity.this.finish();
            //in case of expiry
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
//                                        bp.purchase(LoginActivity.this, "android.test.purchased");
//                                        onBuyPressed();
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

//    @Override
//    public void onBillingInitialized() {
//        Log.e("Billing", "billing init");
//        /*
//         * Called when BillingProcessor was initialized and it's ready to purchase
//         */
//    }
//
//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        Log.e("Billing", "billing purchased");
//        /*
//         * Called when requested PRODUCT ID was successfully purchased
//         */
//        ExpiryUpdateService obj = new ExpiryUpdateService(LoginActivity.this);
//        obj.expiryUpdate(expiryUpdate, true, baseClass.getAUTH_TOKEN(), new CallBack(LoginActivity.this, "expiryUpdate"));
//
//    }
//
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        Log.e("error code", errorCode+"");
//        Crouton.makeText(LoginActivity.this, "Oops! Something went wrong.", Style.ALERT).show();
//        /*
//         * Called when some error occurred. See Constants class for more details
//         */
//    }
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//        /*
//         * Called when purchase history was restored and the list of all owned PRODUCT ID's
//         * was loaded from Google Play
//         */
//    }
    @Override
    public void onDestroy() {
//        if (bp != null)
//            bp.release();

        super.onDestroy();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        if (!bp.handleActivityResult(requestCode, resultCode, data))
//            super.onActivityResult(requestCode, resultCode, data);
//    }

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

        Intent intent = new Intent(LoginActivity.this, PaymentActivity.class);
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

                        SignupService obj = new SignupService(LoginActivity.this);
                        obj.signup(aq.id(R.id.email).getText().toString(),
                                aq.id(R.id.password).getText().toString(),
                                aq.id(R.id.confirm_password).getText().toString(), baseClass.getGCM_Key(), true, new CallBack(LoginActivity.this, "ConfirmSignup"));

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
    }}



