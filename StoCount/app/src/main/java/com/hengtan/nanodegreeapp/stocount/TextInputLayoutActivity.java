package com.hengtan.nanodegreeapp.stocount;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Eric on 15/6/1.
 */
public class TextInputLayoutActivity extends AppCompatActivity {

    private KeyListener passListener;
    private KeyListener userListener;
    private EditText usrname;
    private EditText pssword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_input_layout);


        usrname = (EditText)findViewById(R.id.et_username);
        pssword = (EditText)findViewById(R.id.et_password);

        userListener = usrname.getKeyListener();
        passListener = pssword.getKeyListener();

        usrname.setKeyListener(null);
        pssword.setKeyListener(null);

        Button editBtn = (Button)findViewById(R.id.editButton);

        editBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               Button btn =  ((Button)v);

                if(btn.getText().equals("Edit")) {
                    usrname.setKeyListener(userListener);
                    usrname.setHint("username");

                    pssword.setKeyListener(passListener);
                    pssword.setHint("password");

                    btn.setText("Save");
                }
                else
                {
                    usrname.setHint(null);
                    pssword.setHint(null);
                    usrname.setKeyListener(null);
                    pssword.setKeyListener(null);
                    btn.setText("Edit");
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("TextInputLayoutActivity");
    }

}
