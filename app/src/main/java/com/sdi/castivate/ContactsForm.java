package com.sdi.castivate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdi.castivate.model.ContactFormRequest;
import com.sdi.castivate.model.UsersRes;
import com.sdi.castivate.utils.Library;
import com.sdi.castivate.utils.ServiceCall;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ContactsForm extends Activity {

    Context mContext;
    String getImageID = "";
    int intValue;
    Button btnSubmit;
    EditText edtName, edtPhone, edtEmail, edtMsg;

    ImageView imgBack;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.contact_form);
        mContext = this;
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtBack = (TextView) findViewById(R.id.txtBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBack.performClick();
            }
        });

        edtName = (EditText) findViewById(R.id.edtName);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtMsg = (EditText) findViewById(R.id.edtMsg);

        edtName.setCursorVisible(true);
        edtPhone.setCursorVisible(true);
        edtEmail.setCursorVisible(true);
        edtMsg.setCursorVisible(true);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getNAme = edtName.getText().toString().trim();
                String getPh = edtPhone.getText().toString().trim();
                String getEmail = edtEmail.getText().toString().trim();
                String getMsg = edtMsg.getText().toString().trim();

                if (getNAme.equals("") ||
                        getPh.equals("") ||
                        getEmail.equals("") ||
                        getMsg.equals("")
                        ) {
                    Library.alert(ContactsForm.this, "Please fill all the fields..");
                    //String userId, String talent_image_id, String agent_email, String agent_name, String agent_phone, String agent_message)

                } else {
                    if (isValidEmail(getEmail)) {
                        if (getPh.length() < 10) {
                            Library.alert(ContactsForm.this, "Please enter the valid phone number");
                        } else {
                            getRetro(ContactsForm.this, new ContactFormRequest(Library.getUserId(ContactsForm.this), getImageID, getEmail, getNAme, getPh, getMsg));
                        }
                    } else {
                        Library.alert(ContactsForm.this, "Please enter the valid email id");
                    }
                }
            }
        });


        if (getIntent().hasExtra("imageId"))
            intValue = getIntent().getIntExtra("imageId", 0);
        //    getImageID = getIntent().getStringExtra("imageId");
        getImageID = intValue + "";

    }

    // HTTP POST request
//	private void sendPost() throws Exception {
//
////		String url = "https://selfsolve.apple.com/wcResults.do";
//		String url = "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/reportAbuse";
//		URL obj = new URL(url);
//		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//
////		http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/reportAbuse?userId=28&casting_id=5141
//
//		//add reuqest header
//		con.setRequestMethod("POST");
////		con.setRequestProperty("User-Agent", USER_AGENT);
////		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//		String urlParameters = "userId=28&casting_id=5141";
//
//		// Send post request
//		con.setDoOutput(true);
//		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//		wr.writeBytes(urlParameters);
//		wr.flush();
//		wr.close();
//
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'POST' request to URL : " + url);
//		System.out.println("Post parameters : " + urlParameters);
//		System.out.println("Response Code : " + responseCode);
//
//		BufferedReader in = new BufferedReader(
//				new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//
//		//print result
//		System.out.println(response.toString());
//
//	}
    ProgressDialog pDialog = null;

    public void getRetro(final Context context, ContactFormRequest request) {

        pDialog = new ProgressDialog(context);
        pDialog.show();
        ServiceCall.UpdateContactFormBody updateReportAubuseBody = ServiceCall.initRestADapters().create(ServiceCall.UpdateContactFormBody.class);
        updateReportAubuseBody.createUser(request, new Callback<UsersRes>() {
            @Override
            public void success(UsersRes s, Response response) {
                System.out.println("success");
                pDialog.dismiss();
                alertContact(context, "Your contact form has been submitted successfully.");

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("error");
                pDialog.dismiss();
                Library.alert(context, "Error");
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void alertContact(Context context, final String msg) {

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LayoutInflater inflater = getLayoutInflater(getArguments());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.alert_common, null);
        alertDialog.setContentView(convertView);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtContent);
        txtContent.setText("Message");
        Button btnOk = (Button) convertView.findViewById(R.id.btnOk);
        txtContent.setText(msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();

            }
        });


        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int value = (int) ((width) * 3 / 4);

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = value;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}