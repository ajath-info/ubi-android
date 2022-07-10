package com.ubi.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    ProgressDialog pd;
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        pd = new ProgressDialog(PaymentActivity.this);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView packagetype = findViewById(R.id.packagetype);
        packagetype.setText(getIntent().getStringExtra("type"));
        price = getIntent().getStringExtra("price");
        TextView packageprice = findViewById(R.id.packageprice);
        packageprice.setText("$" + price);
//        findViewById(R.id.cardpaymentlay).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                acceptpayment(true, false, false);
//            }
//        });
//        findViewById(R.id.netbankingtlay).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                acceptpayment(false, true, false);
//            }
//        });
//        findViewById(R.id.upilay).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                acceptpayment(false, false, true);
//            }
//        });

        acceptpayment(true, false, false);
    }

    private void submitpayment(String ref) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ads_id", getIntent().getStringExtra("ads_id"));
        params.put("package_id", getIntent().getStringExtra("package_id"));
        params.put("price", getIntent().getStringExtra("price"));
        params.put("payment_type", "O");
        params.put("payment_status", "Success");
        params.put("transaction_id", ref);
        submitAd(params);
    }

    private void submitAd(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<BaseResponse> call = apiInterface.addPayment(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (pd != null)
                            pd.dismiss();
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
                                Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                                intent.putExtra("isSuccess", true);
                                startActivity(intent);
                                finish();
                            } else {
                                AppUtils.showalert(PaymentActivity.this, response.body().message, false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    if (pd != null)
                        pd.dismiss();
                    AppUtils.showalert(PaymentActivity.this, t.getMessage(), false);
                }
            });
        } else {
            AppUtils.Nointernetalert(PaymentActivity.this);
        }
    }

    private void acceptpayment(boolean iscardpayment, boolean isnetbanking, boolean isupi) {
        UserData data = AppPreferences.getInstance().getUserData(getApplicationContext());
        new RaveUiManager(this).setAmount(Double.parseDouble(price))
                .setCurrency("USD")
                .setEmail(data.getEmail())
                .setfName(data.getFirst_name())
                .setlName(data.getLast_name())
                .setNarration(data.getFirst_name() + " " + data.getLast_name())
                .setPublicKey(AppUtils.Public_Key)
                .setEncryptionKey(AppUtils.Encryption_Key)
                .setTxRef(getString(R.string.app_name) + "_PAY_" + System.currentTimeMillis())
                .setPhoneNumber(data.getPhone(), false)
                .acceptCardPayments(iscardpayment)
//                .acceptAccountPayments(isnetbanking)
//                .acceptMpesaPayments(isupi)
//                    .acceptAchPayments( true)
//                    .acceptGHMobileMoneyPayments( boolean)
//                    .acceptUgMobileMoneyPayments( boolean)
//                    .acceptZmMobileMoneyPayments( boolean)
//                    .acceptRwfMobileMoneyPayments( boolean)
//                    .acceptSaBankPayments( boolean)
//                    .acceptUkPayments( boolean)
//                    .acceptBankTransferPayments( boolean)
//                    .acceptUssdPayments( boolean)
//                    .acceptBarterPayments( boolean)
//                    .acceptFrancMobileMoneyPayments( boolean)
                .allowSaveCardFeature(true)
//                .onStagingEnv(true)
                .isPreAuth(true)
//                .shouldDisplayFee(true)
//                .showStagingLabel(true)
                .initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (message != null)
                Log.d(PaymentActivity.class.getName(), message);
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                try {
                    JSONObject object = new JSONObject(message);
                    JSONObject datas = object.getJSONObject("data");
                    String raveRef = datas.getString("raveRef");
                    submitpayment(raveRef);
                    Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
//                AppUtils.showalert(PaymentActivity.this, "ERROR " + message, false);
                Toast.makeText(this, "Payment Failed ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("isSuccess", false);
                startActivity(intent);

            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
//                AppUtils.showalert(PaymentActivity.this, "CANCELLED " + message, false);
                Toast.makeText(this, "Payment CANCELLED " + message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("isSuccess", false);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
//{"status":"success","message":"Tx Fetched","data":{"id":3456902,"txRef":"EXIBINE_PAY_1654612282271","orderRef":"URF_9F9CFFD8F9A7BBEDE7_2296920","flwRef":"FLW-M03K-22861b54e616f821dccd9d607134e4e7","redirectUrl":"https://rave-webhook.herokuapp.com/receivepayment","device_fingerprint":"1ffa49d79a96b271","settlement_token":null,"cycle":"one-time","amount":99,"charged_amount":99,"appfee":3.77,"merchantfee":0,"merchantbearsfee":1,"chargeResponseCode":"00","raveRef":null,"chargeResponseMessage":"Approved","authModelUsed":"noauth-saved-card","currency":"USD","IP":"52.209.154.143","narration":"Exibine","status":"successful","modalauditid":"089ad2bb649cbcf651143032bbc6af23","vbvrespmessage":"Approved","authurl":"N/A","vbvrespcode":"00","acctvalrespmsg":null,"acctvalrespcode":null,"paymentType":"card","paymentPlan":null,"paymentPage":null,"paymentId":"6527778","fraud_status":"ok","charge_type":"normal","is_live":0,"retry_attempt":null,"getpaidBatchId":null,"createdAt":"2022-06-07T14:31:36.000Z","updatedAt":"2022-06-07T14:31:37.000Z","deletedAt":null,"customerId":1639592,"AccountId":1737425,"customer.id":1639592,"customer.phone":"9716071684","customer.fullName":"Shubham sharma","customer.customertoken":null,"customer.email":"vickys43@yahoo.com","customer.createdAt":"2022-05-28T17:45:11.000Z","customer.updatedAt":"2022-05-28T17:45:11.000Z","customer.deletedAt":null,"customer.AccountId":1737425,"meta":[],"flwMeta":{}}}
