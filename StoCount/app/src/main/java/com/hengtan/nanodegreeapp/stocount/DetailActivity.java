package com.hengtan.nanodegreeapp.stocount;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Eric on 15/6/1.
 */
public class DetailActivity extends AppCompatActivity {

    private KeyListener passListener;
    private KeyListener userListener;
    private TextInputLayout userTextInputLayout;
    private TextInputLayout passTextInputLayout;
    private Drawable userDrawable;
    private Drawable passDrawable;

    private EditText usrname;
    private EditText pssword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initToolbar();

        usrname = (EditText)findViewById(R.id.et_username);
        pssword = (EditText)findViewById(R.id.et_password);

        userDrawable = usrname.getBackground();
        passDrawable = pssword.getBackground();

        userTextInputLayout = (TextInputLayout) findViewById(R.id.til_username);
        passTextInputLayout = (TextInputLayout) findViewById(R.id.til_password);

        userListener = usrname.getKeyListener();
        passListener = pssword.getKeyListener();

        usrname.setKeyListener(null);
        pssword.setKeyListener(null);

        usrname.setBackgroundResource(R.color.transparent);
        pssword.setBackgroundResource(R.color.transparent);

        Button editBtn = (Button)findViewById(R.id.editButton);

        editBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Button btn =  ((Button)v);

                if(btn.getText().equals("Edit")) {
                    usrname.setKeyListener(userListener);
                    userTextInputLayout.setHint("username");

                    pssword.setKeyListener(passListener);
                    passTextInputLayout.setHint("password");

                    btn.setText("Save");

                    usrname.setBackground(userDrawable);
                    pssword.setBackground(passDrawable);
                }
                else
                {
                    userTextInputLayout.setHint(null);
                    passTextInputLayout.setHint(null);
                    usrname.setKeyListener(null);
                    pssword.setKeyListener(null);
                    usrname.setBackgroundResource(R.color.transparent);
                    pssword.setBackgroundResource(R.color.transparent);
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //CollapsingToolbarLayout collapsingToolbar =
         //       (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("Apple iPod touch 32GB  (Assorted Colors)");
    }

}
