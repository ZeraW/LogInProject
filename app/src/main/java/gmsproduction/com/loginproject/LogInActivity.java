package gmsproduction.com.loginproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import gmsproduction.com.loginproject.LoginFragments.BasePage;
import gmsproduction.com.loginproject.LoginFragments.Login_Fragment;


/**
 * Created by Hima on 4/29/2018.
 */

public class LogInActivity extends AppCompatActivity {
    private static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_maain_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new BasePage(),
                            Utils.BasePage).commit();
        }

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                backback();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // Replace Login Fragment with animation
    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }
    // Replace BasePage Fragment with animation
    public void replaceBasePage(){
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new BasePage(),
                        Utils.BasePage).commit();
    }

    @Override
    public void onBackPressed() {

        backback();

    }

    public void backback(){
        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment SignIn_Fragment = fragmentManager
                .findFragmentByTag(Utils.Login_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);
        Fragment LogIn = fragmentManager.findFragmentByTag(Utils.Login_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (LogIn != null)
            replaceBasePage();
        else if (SignUp_Fragment != null)
            replaceBasePage();
        else if (ForgotPassword_Fragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    public void SharedPref(int id,String name,String email,String avatar){
        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_DETAILS, MODE_PRIVATE).edit();
        editor.putInt("User_id", id);
        editor.putString("User_name", name);
        editor.putString("User_email", email);
        editor.putString("User_avatar", avatar);
        editor.apply();
    }


}

