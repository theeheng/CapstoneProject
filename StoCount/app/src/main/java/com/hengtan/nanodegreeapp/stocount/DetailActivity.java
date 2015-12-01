package com.hengtan.nanodegreeapp.stocount;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncTask;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Eric on 15/6/1.
 */
public class DetailActivity extends AppCompatActivity {

    private KeyListener nameListener;
    private KeyListener additionalInfoListener;
    private KeyListener productCountListener;
    private KeyListener descriptionListener;

    private Drawable nameDrawable;
    private Drawable additionalInfoDrawable;
    private Drawable productCountDrawable;
    private Drawable descriptionDrawable;


    @InjectView(R.id.et_name)
    protected EditText name;

    @InjectView(R.id.et_additionalinfo)
    protected EditText additionalInfo;

    @InjectView(R.id.et_productcount)
    protected EditText productCount;

    @InjectView(R.id.et_description)
    protected EditText description;

    @InjectView(R.id.description)
    protected TextView descriptionTextView;

    @InjectView(R.id.photo)
    protected ImageView image;

    @InjectView(R.id.til_name)
    protected TextInputLayout nameTextInputLayout;

    @InjectView(R.id.til_additionalinfo)
    protected TextInputLayout additionalInfoTextInputLayout;

    @InjectView(R.id.til_productcount)
    protected TextInputLayout productCountTextInputLayout;

    @InjectView(R.id.til_description)
    protected TextInputLayout descriptionTextInputLayout;

    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @InjectView(R.id.collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbar;

    @InjectView(R.id.famDetailButton)
    protected FloatingActionsMenu famButton;

    @InjectView(R.id.fabPhotoButton)
    protected FloatingActionButton photoFabButton;

    @InjectView(R.id.fabEditButton)
    protected FloatingActionButton editFabButton;

    private String nameOriginalText;
    private String additionalInfoOriginalText;
    private String productCountOriginalText;
    private String descriptionOriginalText;

    private boolean mIsEditable = false;
    private boolean mIsStockCountEntry = false;
    public static final String PRODUCT_PARCELABLE = "PRODUCTPARCELABLE";
    public static final String PRODUCT_COUNT_PARCELABLE = "PRODUCTCOUNTPARCELABLE";
    public static final String IS_STOCK_ENTRY_EXTRA = "ISSTOCKENTRYEXTRA";

    private Product mProduct;
    private ProductCount mProductCount;
    private StockPeriod mStockPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        initToolbar();

        mStockPeriod = Application.getCurrentStockPeriod();

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
                mProductCount = bundle.getParcelable(PRODUCT_COUNT_PARCELABLE);
            }

            mIsStockCountEntry = intent.getBooleanExtra(IS_STOCK_ENTRY_EXTRA, false);
        }

        if(mProduct != null) {
            name.setText(mProduct.getName());
            mCollapsingToolbar.setTitle(mProduct.getName());
            additionalInfo.setText(mProduct.getAdditionalInfo());
            description.setText(mProduct.getDescription());
            descriptionTextView.setText(mProduct.getDescription());
            Glide.with(this).load(mProduct.getLargeImage()).fitCenter().into(image);
        }

        if(mProductCount != null)
        {
            productCount.setText(mProductCount.getQuantity().toString());
        }

        nameOriginalText = name.getText().toString();
        additionalInfoOriginalText = additionalInfo.getText().toString();
        productCountOriginalText = productCount.getText().toString();
        descriptionOriginalText = description.getText().toString();

        nameDrawable = name.getBackground();
        additionalInfoDrawable = additionalInfo.getBackground();
        productCountDrawable = productCount.getBackground();
        descriptionDrawable = description.getBackground();

        description.setVisibility(View.GONE);
        descriptionTextInputLayout.setVisibility(View.GONE);

        nameListener = additionalInfo.getKeyListener();
        additionalInfoListener = additionalInfo.getKeyListener();
        productCountListener = productCount.getKeyListener();
        descriptionListener = description.getKeyListener();

        name.setKeyListener(null);
        additionalInfo.setKeyListener(null);
        productCount.setKeyListener(null);
        description.setKeyListener(null);

        name.setBackgroundResource(R.color.transparent);
        additionalInfo.setBackgroundResource(R.color.transparent);
        productCount.setBackgroundResource(R.color.transparent);
        description.setBackgroundResource(R.color.transparent);

        additionalInfoTextInputLayout.setHint("additional info");
        productCountTextInputLayout.setHint("product count");
        descriptionTextInputLayout.setHint("description");

        showHideProductCount();

        //Only update for editing at after getting all previous state of editText control
        if(mProduct!= null && (mProduct.IsAddingNewProduct()|| mIsStockCountEntry))
        {
            famButton.expand();
            updateUIForEditing();
        }

        famButton.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuCollapsed() {

                        if (mIsEditable) {

                            showHideProductCount();

                            editFabButton.setIcon(android.R.drawable.ic_menu_camera);
                            photoFabButton.setIcon(android.R.drawable.ic_menu_gallery);
                            mIsEditable = false;
                            nameTextInputLayout.setHint(null);
                            //additionalInfoTextInputLayout.setHint(null);
                            //productCountTextInputLayout.setHint(null);
                            //descriptionTextInputLayout.setHint(null);
                            name.setKeyListener(null);
                            additionalInfo.setKeyListener(null);
                            productCount.setKeyListener(null);
                            description.setKeyListener(null);
                            name.setBackgroundResource(R.color.transparent);
                            additionalInfo.setBackgroundResource(R.color.transparent);
                            productCount.setBackgroundResource(R.color.transparent);
                            description.setBackgroundResource(R.color.transparent);
                            name.setText(nameOriginalText);
                            additionalInfo.setText(additionalInfoOriginalText);
                            productCount.setText(productCountOriginalText);
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

    @OnClick(R.id.fabEditButton)
    public void onEditButtonClick(View v) {

            if (!mIsEditable) {

                updateUIForEditing();

            } else {
                nameOriginalText = name.getText().toString();
                additionalInfoOriginalText = additionalInfo.getText().toString();
                productCountOriginalText = productCount.getText().toString().trim();
                descriptionOriginalText = description.getText().toString();

                mProduct.setName(nameOriginalText);
                mProduct.setAdditionalInfo(additionalInfoOriginalText);
                mProduct.setDescription(descriptionOriginalText);

                DBAsyncTask saveProductAsyncTask = new DBAsyncTask(this, getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.SAVE);
                saveProductAsyncTask.execute(mProduct);

                if(!productCountOriginalText.isEmpty())
                {
                    if(mProductCount != null && mProductCount.getProductCountId() != null)
                    {
                        mProductCount.setQuantity(Double.parseDouble(productCountOriginalText));
                        mProductCount.setCountDate(new Date());
                    }
                    else
                    {
                        mProductCount = new ProductCount();
                        mProductCount.setStockPeriodId(mStockPeriod.getStockPeriodId());
                        mProductCount.setProductId(mProduct.getProductId());
                        mProductCount.setQuantity(Double.parseDouble(productCountOriginalText));
                        mProductCount.setCountDate(new Date());
                    }

                    saveProductAsyncTask = new DBAsyncTask(this, getContentResolver(), DBAsyncTask.ObjectType.PRODUCT_COUNT, DBAsyncTask.OperationType.SAVE);
                    saveProductAsyncTask.execute(mProductCount);
                }
                else
                {
                    if(mProductCount != null && mProductCount.getProductCountId() != null)
                    {
                        mProductCount.setQuantity(null);
                        mProductCount.setCountDate(new Date());
                    }
                }

                famButton.collapse();
                editFabButton.setIcon(android.R.drawable.ic_menu_camera);
                mIsEditable = false;
                nameTextInputLayout.setHint(null);
                //additionalInfoTextInputLayout.setHint(null);
                //productCountTextInputLayout.setHint(null);
                //descriptionTextInputLayout.setHint(null);
                name.setKeyListener(null);
                additionalInfo.setKeyListener(null);
                productCount.setKeyListener(null);
                description.setKeyListener(null);
                name.setBackgroundResource(R.color.transparent);
                additionalInfo.setBackgroundResource(R.color.transparent);
                productCount.setBackgroundResource(R.color.transparent);
                description.setBackgroundResource(R.color.transparent);

                mCollapsingToolbar.setTitle(nameOriginalText);
                name.setText(nameOriginalText);
                additionalInfo.setText(additionalInfoOriginalText);
                description.setText(descriptionOriginalText);
                descriptionTextView.setText(descriptionOriginalText);

                //should only set visibility of product count after setting the text value
                if(productCountOriginalText.isEmpty()) {
                    productCount.setText(" ");
                    productCount.setVisibility(View.GONE);
                    productCountTextInputLayout.setVisibility(View.GONE);
                }
                else
                {
                    productCount.setText(productCountOriginalText);
                }

                photoFabButton.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                descriptionTextInputLayout.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.VISIBLE);
            }
    }

    private void showHideProductCount() {
        if(mProduct.IsAddingNewProduct() || mProductCount ==  null || (mProductCount !=  null && mProductCount.getQuantity() == null)) {
            productCount.setVisibility(View.GONE);
            productCountTextInputLayout.setVisibility(View.GONE);
        }
    }

    private void updateUIForEditing() {
        mIsEditable = true;

        name.setKeyListener(nameListener);
        nameTextInputLayout.setHint("product name");

        additionalInfo.setKeyListener(additionalInfoListener);
        //additionalInfoTextInputLayout.setHint("additional info");

        productCount.setVisibility(View.VISIBLE);
        productCount.setKeyListener(additionalInfoListener);
        productCountTextInputLayout.setVisibility(View.VISIBLE);
        //productCountTextInputLayout.setHint("product count");

        if(productCount.getText().toString().equals(""))
        {
            productCount.setText(" ");
        }

        description.setKeyListener(descriptionListener);
        //descriptionTextInputLayout.setHint("description");

        name.setBackground(nameDrawable);
        additionalInfo.setBackground(additionalInfoDrawable);
        productCount.setBackground(productCountDrawable);
        description.setBackground(descriptionDrawable);

        description.setVisibility(View.VISIBLE);
        descriptionTextInputLayout.setVisibility(View.VISIBLE);
        descriptionTextView.setVisibility(View.GONE);
        photoFabButton.setVisibility(View.GONE);
        editFabButton.setIcon(R.drawable.ic_star);
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

      //  toolbar.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
    }

}
