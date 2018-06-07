package gmsproduction.com.loginproject.LoginFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gmsproduction.com.loginproject.Constants;
import gmsproduction.com.loginproject.CustomToast;
import gmsproduction.com.loginproject.LogInActivity;
import gmsproduction.com.loginproject.R;
import gmsproduction.com.loginproject.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Login_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText emailid, password;
    private static Button loginButton2;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    Button facebookbtnLog;
    private CallbackManager callbackManager;
    List<String> permissionNeeds = Arrays.asList("user_photos", "email",
            "user_birthday", "public_profile");
    String id, name, email, gender, birthday, profile_pic;
    HashMap<String, String> jsonObjectStore = new HashMap<String, String>();
    private RequestQueue mRequestQueue;

    public Login_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        getActivity().setTitle("Login");

        initViews();
        setListeners();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getActivity());
        callbackManager = CallbackManager.Factory.create();

        //facebook login button
        facebookbtnLog = view.findViewById(R.id.fb_mang2);
        facebookbtnLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookMethod();
            }
        });

        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();
        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton2 = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        /*XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }*/
    }

    // Set Listeners
    private void setListeners() {
        loginButton2.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUp_Fragment(),
                                Utils.SignUp_Fragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");
            // Else do login and do your stuff
        else {
            postyNormal(getEmailId, getPassword);

            Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT)
                    .show();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void facebookMethod() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                permissionNeeds
        );
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        jsonObjectStore.put("user", object.toString());
                                        /*FacebookPostRequest facebookPostRequest = new FacebookPostRequest(getContext(), new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("lol", response);

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e("lol2", "" + error.getMessage());

                                            }
                                        });
                                        facebookPostRequest.setBody(jsonObjectStore);
                                        facebookPostRequest.start();*/


                                        /*Log.e("LoginActivity",
                                                object.toString());*/
                                        try {
                                            id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "http://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.i("profile_pic",
                                                        profile_pic + "");

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            name = object.getString("name");
                                            email = object.getString("email");
                                            gender = object.getString("gender");
                                            birthday = object.getString("birthday");
                                            profile_pic = "http://graph.facebook.com/" + id + "/picture?type=large";
                                            postyy(id, name, email, profile_pic);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });


                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("LoginActivity",
                                "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("LoginActivity",
                                exception.toString());
                    }
                }
        );

    }

    //this method will post the data of user to the server
    //facebookLogin
    public void postyy(String id, String name, String email, String avatar) {

        JSONObject jsonobject_one = new JSONObject();
        try {
            jsonobject_one.put("name", name);
            jsonobject_one.put("email", email);
            jsonobject_one.put("id", id);
            jsonobject_one.put("avatar", avatar);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    Request.Method.POST, Constants.basicUrl+"/auth/facebook/callback", jsonobject_one,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("lol", response.toString());

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("lol2", "" + error.getMessage());

                    //VolleyLog.d("error", "Error: " + error.getMessage());

                }
            });
            mRequestQueue = Volley.newRequestQueue(getActivity());
            mRequestQueue.add(jsonObjReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Normal Login
    public void postyNormal(String email, String password) {
        String url = "https://dregy01.frb.io/api/login";
        JSONObject jsonobject_one = new JSONObject();
        try {
            jsonobject_one.put("email", email);
            jsonobject_one.put("password", password);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    Request.Method.POST, Constants.basicUrl+"/login", jsonobject_one,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Login", response.toString());
                            int id;
                            String name, email, avatar;
                            try {
                                id = response.getInt("id");
                                name = response.getString("name");
                                email = response.getString("email");
                                try {
                                    avatar = response.getString("avatar");
                                } catch (Exception e) {
                                    avatar = "";
                                }

                                ((LogInActivity) getActivity()).SharedPref(id, name, email, avatar);

                                //SharedPref(id, name, email, avatar);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Login", "error" + error.getMessage());

                    //VolleyLog.d("error", "Error: " + error.getMessage());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Accept", "application/json");
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            mRequestQueue = Volley.newRequestQueue(getActivity());
            mRequestQueue.add(jsonObjReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* public void SharedPref(int id,String name,String email,String avatar){
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.USER_DETAILS, MODE_PRIVATE).edit();
        editor.putInt("User_id", id);
        editor.putString("User_name", name);
        editor.putString("User_email", email);
        editor.putString("User_avatar", avatar);
        editor.apply();
    }*/
}
