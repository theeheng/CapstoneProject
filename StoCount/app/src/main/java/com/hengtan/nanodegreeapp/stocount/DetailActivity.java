package com.hengtan.nanodegreeapp.stocount;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Eric on 15/6/1.
 */
public class DetailActivity extends AppCompatActivity {

    private KeyListener nameListener;
    private KeyListener categoryListener;
    private KeyListener descriptionListener;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout categoryTextInputLayout;
    private TextInputLayout descriptionTextInputLayout;
    private Drawable nameDrawable;
    private Drawable categoryDrawable;
    private Drawable descriptionDrawable;

    private EditText name;
    private EditText category;
    private EditText description;
    private TextView descriptionTextView;
    private ImageView image;

    private String nameOriginalText;
    private String categoryOriginalText;
    private String descriptionOriginalText;

    private boolean mIsEditable = false;

    private CollapsingToolbarLayout mCollapsingToolbar;

    public static final String PRODUCT_PARCELABLE = "PRODUCTPARCELABLE";

    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initToolbar();

        if (savedInstanceState != null && savedInstanceState.containsKey(PRODUCT_PARCELABLE)) {
            mProduct = savedInstanceState.getParcelable(PRODUCT_PARCELABLE);
        }
        else
        {
            //Bundle arguments = getArguments();
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if(bundle.get(PRODUCT_PARCELABLE) != null)
            {
                bundle = (Bundle) bundle.get(PRODUCT_PARCELABLE);
                mProduct = bundle.getParcelable(PRODUCT_PARCELABLE);
            }
        }

        image = (ImageView)findViewById(R.id.photo);
        name = (EditText)findViewById(R.id.et_name);
        category = (EditText)findViewById(R.id.et_category);
        description = (EditText)findViewById(R.id.et_description);
        descriptionTextView = (TextView)findViewById(R.id.description);

        if(mProduct != null) {
            name.setText(mProduct.getName());
            mCollapsingToolbar.setTitle(mProduct.getName());
            category.setText(mProduct.getCategory());
            description.setText(mProduct.getDescription());
            descriptionTextView.setText(mProduct.getDescription());
            Glide.with(this).load(mProduct.getLargeImage()).fitCenter().into(image);
        }

        nameOriginalText = name.getText().toString();
        categoryOriginalText = category.getText().toString();
        descriptionOriginalText = description.getText().toString();

        nameDrawable = name.getBackground();
        categoryDrawable = category.getBackground();
        descriptionDrawable = description.getBackground();

        nameTextInputLayout = (TextInputLayout) findViewById(R.id.til_name);
        categoryTextInputLayout = (TextInputLayout) findViewById(R.id.til_category);
        descriptionTextInputLayout = (TextInputLayout) findViewById(R.id.til_description);
        description.setVisibility(View.GONE);
        descriptionTextInputLayout.setVisibility(View.GONE);

        nameListener = category.getKeyListener();
        categoryListener = category.getKeyListener();
        descriptionListener = description.getKeyListener();

        name.setKeyListener(null);
        category.setKeyListener(null);
        description.setKeyListener(null);

        name.setBackgroundResource(R.color.transparent);
        category.setBackgroundResource(R.color.transparent);
        description.setBackgroundResource(R.color.transparent);

        final FloatingActionsMenu famButton = (FloatingActionsMenu) findViewById(R.id.famButton);

        final FloatingActionButton photoFabButton = (FloatingActionButton) findViewById(R.id.fabPhotoButton);




        final FloatingActionButton editFabButton = (FloatingActionButton) findViewById(R.id.fabEditButton);
        editFabButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (!mIsEditable) {
                    mIsEditable = true;

                    name.setKeyListener(nameListener);
                    nameTextInputLayout.setHint("product name");

                    category.setKeyListener(categoryListener);
                    categoryTextInputLayout.setHint("category");

                    description.setKeyListener(descriptionListener);
                    descriptionTextInputLayout.setHint("description");

                    name.setBackground(nameDrawable);
                    category.setBackground(categoryDrawable);
                    description.setBackground(descriptionDrawable);

                    description.setVisibility(View.VISIBLE);
                    descriptionTextInputLayout.setVisibility(View.VISIBLE);
                    descriptionTextView.setVisibility(View.GONE);
                    photoFabButton.setVisibility(View.GONE);
                    editFabButton.setIcon(R.drawable.ic_star);


                }
                else
                {
                    nameOriginalText = name.getText().toString();
                    categoryOriginalText = category.getText().toString();
                    descriptionOriginalText = description.getText().toString();


                    Toast.makeText(DetailActivity.this, "Save Detail.........", Toast.LENGTH_LONG).show();
                    famButton.collapse();
                    editFabButton.setIcon(android.R.drawable.ic_menu_camera);
                    mIsEditable = false;
                    nameTextInputLayout.setHint(null);
                    categoryTextInputLayout.setHint(null);
                    descriptionTextInputLayout.setHint(null);
                    name.setKeyListener(null);
                    category.setKeyListener(null);
                    description.setKeyListener(null);
                    name.setBackgroundResource(R.color.transparent);
                    category.setBackgroundResource(R.color.transparent);
                    description.setBackgroundResource(R.color.transparent);

                    mCollapsingToolbar.setTitle(nameOriginalText);
                    name.setText(nameOriginalText);
                    category.setText(categoryOriginalText);
                    description.setText(descriptionOriginalText);
                    descriptionTextView.setText(descriptionOriginalText);
                    
                    photoFabButton.setVisibility(View.VISIBLE);
                    description.setVisibility(View.GONE);
                    descriptionTextInputLayout.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.VISIBLE);
                }
            }

        }
        );

        famButton.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuCollapsed() {

                        if (mIsEditable) {
                            editFabButton.setIcon(android.R.drawable.ic_menu_camera);
                            photoFabButton.setIcon(android.R.drawable.ic_menu_gallery);
                            mIsEditable = false;
                            nameTextInputLayout.setHint(null);
                            categoryTextInputLayout.setHint(null);
                            descriptionTextInputLayout.setHint(null);
                            name.setKeyListener(null);
                            category.setKeyListener(null);
                            description.setKeyListener(null);
                            name.setBackgroundResource(R.color.transparent);
                            category.setBackgroundResource(R.color.transparent);
                            description.setBackgroundResource(R.color.transparent);
                            name.setText(nameOriginalText);
                            category.setText(categoryOriginalText);
                            description.setText(descriptionOriginalText);
                            editFabButton.setVisibility(View.VISIBLE);
                            photoFabButton.setVisibility(View.VISIBLE);
                            description.setVisibility(View.GONE);
                            descriptionTextInputLayout.setVisibility(View.GONE);
                            descriptionTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onMenuExpanded() {

                    }
                }
        );


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
      //  toolbar.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar =
             (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
    }

}
