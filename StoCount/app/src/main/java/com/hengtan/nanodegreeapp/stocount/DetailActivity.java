package com.hengtan.nanodegreeapp.stocount;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncCallBack;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncTask;
import com.hengtan.nanodegreeapp.stocount.data.DbImportExport;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.leansoft.nano.soap11.Detail;

import java.io.File;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements DBAsyncCallBack, ImageChooserListener, ImageChooserDialogFragmentCallBack {

    private static final String TAG = DetailActivity.class.getSimpleName();

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

    @InjectView(R.id.fabScanButton)
    protected FloatingActionButton scanFabButton;

    @InjectView(R.id.fabEditButton)
    protected FloatingActionButton editFabButton;

    private String nameOriginalText;
    private String additionalInfoOriginalText;
    private String productCountOriginalText;
    private String descriptionOriginalText;

    private boolean mPreivousStockPeriod = false;
    private boolean mIsEditable = false;
    private boolean mIsStockCountEntry = false;
    private String mVoiceSearchQuantity;
    public static final String PRODUCT_PARCELABLE = "PRODUCTPARCELABLE";
    public static final String PRODUCT_COUNT_PARCELABLE = "PRODUCTCOUNTPARCELABLE";
    public static final String IS_PREVIOUS_STOCK_PERIOD = "ISPREVIOUSSTOCKPERIOD";
    public static final String IS_STOCK_ENTRY_EXTRA = "ISSTOCKENTRYEXTRA";
    public static final String VOICE_SEARCH_QUANTITY_EXTRA = "VOICESEARCHQUANTITY";

    private Product mProduct;
    private ProductCount mProductCount;
    private StockPeriod mStockPeriod;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String chooserImageFilePath;
    private String chooserThumbnailFilePath;

    private String mProductNameHintStr;
    private String mAdditionalInfoHintStr;
    private String mProductCountHintStr;
    private String mDescriptionHintStr;
    private String mErrorBarcodeStr;
    private String mCancelledBarcodeStr;
    private String mScannedBarcodeStr;
    private String mSaveSuccessfulToast;
    private String mFailedSaveToast;
    private String mSaveFabStr;
    private String mEditFabStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        initToolbar();

        Resources res = getResources();

        mErrorBarcodeStr = res.getString(R.string.error_barcode_text);
        mCancelledBarcodeStr = res.getString(R.string.cancelled_barcode_text);
        mScannedBarcodeStr = res.getString(R.string.scanned_barcode_log_text);
        mProductNameHintStr = res.getString(R.string.cd_product_name);
        mAdditionalInfoHintStr = res.getString(R.string.cd_additional_info);
        mProductCountHintStr = res.getString(R.string.cd_stock_count);
        mDescriptionHintStr = res.getString(R.string.cd_description);
        mSaveSuccessfulToast = res.getString(R.string.save_successful_toast_text);
        mFailedSaveToast = res.getString(R.string.failed_save_toast_text);
        mSaveFabStr = res.getString(R.string.save_fab_text);
        mEditFabStr = res.getString(R.string.edit_fab_text);

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

            mPreivousStockPeriod =  intent.getBooleanExtra(IS_PREVIOUS_STOCK_PERIOD, false);

            mIsStockCountEntry = intent.getBooleanExtra(IS_STOCK_ENTRY_EXTRA, false);
            mVoiceSearchQuantity = intent.getStringExtra(VOICE_SEARCH_QUANTITY_EXTRA);


        }

        if(mProduct != null) {
            name.setText(mProduct.getName());
            mCollapsingToolbar.setTitle(mProduct.getName());
            additionalInfo.setText(mProduct.getAdditionalInfo());
            description.setText(mProduct.getDescription());
            descriptionTextView.setText(mProduct.getDescription());

            if(mProduct.getLargeImage().isEmpty())
            {
                Glide.with(this).load(R.mipmap.no_image).fitCenter().into(image);
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    postponeEnterTransition();
                }
                Glide.with(this).load(mProduct.getLargeImage()).listener(new GlideLoaderListener<String, GlideDrawable>(this, R.mipmap.no_image, image)).fitCenter().into(image);
            }
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

        additionalInfoTextInputLayout.setHint(mAdditionalInfoHintStr);
        productCountTextInputLayout.setHint(mProductCountHintStr);
        descriptionTextInputLayout.setHint(mDescriptionHintStr);

        showHideProductCount();
        ToogleFabButton();

        //Only update for editing at after getting all previous state of editText control
        if(mProduct!= null && (mProduct.IsAddingNewProduct()|| mIsStockCountEntry))
        {
            famButton.expand();
            updateUIForEditing();
        }

        if(mVoiceSearchQuantity != null && !mVoiceSearchQuantity.isEmpty())
        {
            productCount.setText(mVoiceSearchQuantity);
        }

        famButton.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuCollapsed() {

                        if (mIsEditable) {
                            showHideProductCount();
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
                            description.setVisibility(View.GONE);
                            descriptionTextInputLayout.setVisibility(View.GONE);
                            descriptionTextView.setVisibility(View.VISIBLE);
                        }

                        ToogleFabButton();
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

                if(chooserImageFilePath != null)
                {
                    mProduct.setLargeImage(chooserImageFilePath);
                }

                if(chooserThumbnailFilePath != null)
                {
                    mProduct.setThumbnailImage(chooserThumbnailFilePath);
                }

                DBAsyncTask saveProductAsyncTask = new DBAsyncTask(getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.SAVE, this);

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

                    saveProductAsyncTask.setObjectType(DBAsyncTask.ObjectType.PRODUCT_COUNT);
                    saveProductAsyncTask.execute(mProduct,mProductCount);
                }
                else
                {
                    if(mProductCount != null && mProductCount.getProductCountId() != null)
                    {
                        mProductCount.setQuantity(null);
                        mProductCount.setCountDate(new Date());
                        saveProductAsyncTask.setObjectType(DBAsyncTask.ObjectType.PRODUCT_COUNT);
                        saveProductAsyncTask.execute(mProduct, mProductCount);
                    }
                    else
                    {
                        saveProductAsyncTask.execute(mProduct);
                    }
                }

                famButton.collapse();
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

                description.setVisibility(View.GONE);
                descriptionTextInputLayout.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.VISIBLE);
                ToogleFabButton();
            }
    }

    @OnClick(R.id.fabScanButton)
    public void onScanBtnClick(View v) {
        try {
            IntentIntegrator intentIntegrator = new IntentIntegrator(DetailActivity.this);
            //intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        } catch (Exception ex) {
            Log.e(TAG, mErrorBarcodeStr + ex.getMessage());
        }
    }

    @OnClick(R.id.photo)
    public void onPhotoClick(View v) {

        if (mIsEditable) {

            ImageChooserDialogFragment dialog = new ImageChooserDialogFragment();
            dialog.setImageChooserDialogFragmentCallBack(this);
            dialog.show(getSupportFragmentManager(), "ImageChooserDialogFragment");

            //chooseImage();
        }
    }

    private void showHideProductCount() {
        if(mProduct.IsAddingNewProduct() || mProductCount ==  null || (mProductCount !=  null && mProductCount.getQuantity() == null)) {
            productCount.setVisibility(View.GONE);
            productCountTextInputLayout.setVisibility(View.GONE);
        }
    }

    private void ToogleFabButton() {

            if (mProduct.IsAddingNewProduct()) {

                scanFabButton.setEnabled(false);

                if(mIsEditable) {
                    editFabButton.setIcon(android.R.drawable.ic_menu_save);
                    editFabButton.setTitle(mSaveFabStr);
                }
                else {
                    editFabButton.setIcon(android.R.drawable.ic_menu_edit);
                    editFabButton.setTitle(mEditFabStr);
                }

            } else if(mPreivousStockPeriod) {
                famButton.setEnabled(false);
                famButton.setVisibility(View.GONE);
                editFabButton.setEnabled(false);
                scanFabButton.setEnabled(false);
            }
            else{

                if(mIsEditable) {
                    editFabButton.setIcon(android.R.drawable.ic_menu_save);
                    editFabButton.setTitle(mSaveFabStr);
                    scanFabButton.setEnabled(false);
                }
                else {
                    editFabButton.setIcon(android.R.drawable.ic_menu_edit);
                    scanFabButton.setEnabled(true);
                    editFabButton.setTitle(mEditFabStr);
                }
            }
    }

    private void updateUIForEditing() {
        mIsEditable = true;

        name.setKeyListener(nameListener);
        nameTextInputLayout.setHint(mProductNameHintStr);

        additionalInfo.setKeyListener(additionalInfoListener);
        //additionalInfoTextInputLayout.setHint("additional info");

        if(!mProduct.IsAddingNewProduct())
        {
            productCount.setVisibility(View.VISIBLE);
            productCountTextInputLayout.setVisibility(View.VISIBLE);
        }

        productCount.setKeyListener(additionalInfoListener);
        productCount.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);

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
        ToogleFabButton();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            String barcodeResult = result.getContents();

            if (barcodeResult == null) {
                Log.d(TAG, mCancelledBarcodeStr);
                Toast.makeText(this, mCancelledBarcodeStr, Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, mScannedBarcodeStr + barcodeResult);

                mProduct.setBarcode(barcodeResult);
                mProduct.setBarcodeFormat(result.getFormatName());

                DBAsyncTask saveProductAsyncTask = new DBAsyncTask(getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.SAVE, this);
                saveProductAsyncTask.execute(mProduct);

                famButton.collapse();
            }
        }else if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
        else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void CallBackOnSuccessfull() {
        productCount.setVisibility(View.VISIBLE);
        productCountTextInputLayout.setVisibility(View.VISIBLE);

        Toast.makeText(this, mSaveSuccessfulToast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void CallBackOnFail() {
        Toast.makeText(this, mFailedSaveToast, Toast.LENGTH_SHORT).show();
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(chooserImageFilePath);
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Chosen Image: O - " + chosenImage.getFilePathOriginal());
                Log.i(TAG, "Chosen Image: T - " + chosenImage.getFileThumbnail());
                Log.i(TAG, "Chosen Image: Ts - " + chosenImage.getFileThumbnailSmall());
                //isActivityResultOver = true;
                //originalFilePath = image.getFilePathOriginal();
                //thumbnailFilePath = image.getFileThumbnail();
                //thumbnailSmallFilePath = image.getFileThumbnailSmall();
                //progressBar.setVisibility(View.GONE);
                if (image != null) {
                    Log.i(TAG, "Chosen Image: Is not null");
                    chooserImageFilePath = chosenImage.getFilePathOriginal();
                    chooserThumbnailFilePath = chosenImage.getFileThumbnail();
                    Glide.with(DetailActivity.this).load(chosenImage.getFilePathOriginal()).fitCenter().into(image);

                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }
        });
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "OnError: " + reason);
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void CallChooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, DbImportExport.DEFAULT_IMAGE_BACKUP_DIRECTORY, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            //progressBar.setVisibility(View.VISIBLE);
            imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CallTakePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, DbImportExport.DEFAULT_IMAGE_BACKUP_DIRECTORY, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            //progressBar.setVisibility(View.VISIBLE);
            imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



