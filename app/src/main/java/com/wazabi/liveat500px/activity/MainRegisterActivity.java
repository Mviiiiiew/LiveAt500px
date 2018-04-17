package com.wazabi.liveat500px.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wazabi.liveat500px.R;

public class MainRegisterActivity extends BaseActivity {

    EditText editText_input_password;
    EditText  editText_input_email;
    Button btn_register;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);


        editText_input_password = (EditText)findViewById(R.id.editText_input_password);
        editText_input_email = (EditText) findViewById(R.id.editText_input_email);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = editText_input_email.getText().toString().replace(" ","");
                String mPass = editText_input_password.getText().toString().replace(" ","");
                if(mEmail.equals("") || mPass.equals("")){
                    Toast.makeText(getApplicationContext(), R.string.LoginFail,Toast.LENGTH_LONG).show();
                }else{

                   saveValueToFirebase(mEmail,mPass);
                }

            }
        });

    }

    private  void  saveValueToFirebase(String mEmanl,String mPass){
        showProgressDialog();
        mAuth =  FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mEmanl,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"Retister Success",Toast.LENGTH_LONG).show();

                }else{
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"Retister fail",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
