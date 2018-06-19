package tesco.webapi.android;

import java.util.ArrayList;

import sun.security.krb5.internal.crypto.Des;

/**
 * Created by htan on 17/11/2015.
 */
public class TescoProduct {

    private String BaseProductId;
    private String gtin;
    private String CheaperAlternativeProductId;
    private String CookingAndUsage;
    private String ExtendedDescription;
    private String HealthierAlternativeProductId;
    private String image;
    private Double MaximumPurchaseQuantity;
    private String name;
    private ArrayList<String> description;
    private String OfferPromotion;
    private String OfferValidity;
    private String OfferLabelImagePath;
    private String ShelfCategory;
    private String ShelfCategoryName;
    private Float price;
    private String PriceDescription;
    private String id;
    private String ProductType;
    private Double Rating;
    private String StorageInfo;
    private Double UnitPrice;
    private String UnitType;
    private String ExtendedInfoUrl;

    /**
     *
     * @return
     * The BaseProductId
     */
    public String getBaseProductId() {
        return BaseProductId;
    }

    /**
     *
     * @param BaseProductId
     * The BaseProductId
     */
    public void setBaseProductId(String BaseProductId) {
        this.BaseProductId = BaseProductId;
    }

    /**
     *
     * @return
     * The EANBarcode
     */
    public String getEANBarcode() {
        return gtin ;
    }

    /**
     *
     * @param EANBarcode
     * The EANBarcode
     */
    public void setEANBarcode(String EANBarcode) {
        this.gtin = EANBarcode;
    }

    /**
     *
     * @return
     * The CheaperAlternativeProductId
     */
    public String getCheaperAlternativeProductId() {
        return CheaperAlternativeProductId;
    }

    /**
     *
     * @param CheaperAlternativeProductId
     * The CheaperAlternativeProductId
     */
    public void setCheaperAlternativeProductId(String CheaperAlternativeProductId) {
        this.CheaperAlternativeProductId = CheaperAlternativeProductId;
    }

    /**
     *
     * @return
     * The CookingAndUsage
     */
    public String getCookingAndUsage() {
        return CookingAndUsage;
    }

    /**
     *
     * @param CookingAndUsage
     * The CookingAndUsage
     */
    public void setCookingAndUsage(String CookingAndUsage) {
        this.CookingAndUsage = CookingAndUsage;
    }

    /**
     *
     * @return
     * The ExtendedDescription
     */
    public String getExtendedDescription() {
        return ExtendedDescription;
    }

    /**
     *
     * @param ExtendedDescription
     * The ExtendedDescription
     */
    public void setExtendedDescription(String ExtendedDescription) {
        this.ExtendedDescription = ExtendedDescription;
    }

    /**
     *
     * @return
     * The HealthierAlternativeProductId
     */
    public String getHealthierAlternativeProductId() {
        return HealthierAlternativeProductId;
    }

    /**
     *
     * @param HealthierAlternativeProductId
     * The HealthierAlternativeProductId
     */
    public void setHealthierAlternativeProductId(String HealthierAlternativeProductId) {
        this.HealthierAlternativeProductId = HealthierAlternativeProductId;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImagePath() {
        return image;
    }

    /**
     *
     * @param ImagePath
     * The ImagePath
     */
    public void setImagePath(String ImagePath) {
        this.image = ImagePath;
    }

    /**
     *
     * @return
     * The MaximumPurchaseQuantity
     */
    public Double getMaximumPurchaseQuantity() {
        return MaximumPurchaseQuantity;
    }

    /**
     *
     * @param MaximumPurchaseQuantity
     * The MaximumPurchaseQuantity
     */
    public void setMaximumPurchaseQuantity(Double MaximumPurchaseQuantity) {
        this.MaximumPurchaseQuantity = MaximumPurchaseQuantity;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param Name
     * The Name
     */
    public void setName(String Name) {
        this.name = Name;
    }
    /**
     *
     * @return
     * The Name
     */
    public ArrayList<String> getDescription() {
        return description;
    }

    /**
     *
     * @param Description
     * The Name
     */
    public void setDescription(ArrayList<String> Description) {
        this.description = Description;
    }

    /**
     *
     * @return
     * The OfferPromotion
     */
    public String getOfferPromotion() {
        return OfferPromotion;
    }

    /**
     *
     * @param OfferPromotion
     * The OfferPromotion
     */
    public void setOfferPromotion(String OfferPromotion) {
        this.OfferPromotion = OfferPromotion;
    }

    /**
     *
     * @return
     * The OfferValidity
     */
    public String getOfferValidity() {
        return OfferValidity;
    }

    /**
     *
     * @param OfferValidity
     * The OfferValidity
     */
    public void setOfferValidity(String OfferValidity) {
        this.OfferValidity = OfferValidity;
    }

    /**
     *
     * @return
     * The OfferLabelImagePath
     */
    public String getOfferLabelImagePath() {
        return OfferLabelImagePath;
    }

    /**
     *
     * @param OfferLabelImagePath
     * The OfferLabelImagePath
     */
    public void setOfferLabelImagePath(String OfferLabelImagePath) {
        this.OfferLabelImagePath = OfferLabelImagePath;
    }

    /**
     *
     * @return
     * The ShelfCategory
     */
    public String getShelfCategory() {
        return ShelfCategory;
    }

    /**
     *
     * @param ShelfCategory
     * The ShelfCategory
     */
    public void setShelfCategory(String ShelfCategory) {
        this.ShelfCategory = ShelfCategory;
    }

    /**
     *
     * @return
     * The ShelfCategoryName
     */
    public String getShelfCategoryName() {
        return ShelfCategoryName;
    }

    /**
     *
     * @param ShelfCategoryName
     * The ShelfCategoryName
     */
    public void setShelfCategoryName(String ShelfCategoryName) {
        this.ShelfCategoryName = ShelfCategoryName;
    }

    /**
     *
     * @return
     * The Price
     */
    public Float getPrice() {
        return price;
    }

    /**
     *
     * @param Price
     * The Price
     */
    public void setPrice(Float Price) {
        this.price = Price;
    }

    /**
     *
     * @return
     * The PriceDescription
     */
    public String getPriceDescription() {
        return this.price.toString();
    }

    /**
     *
     * @param PriceDescription
     * The PriceDescription
     */
    public void setPriceDescription(String PriceDescription) {
        this.PriceDescription = PriceDescription;
    }

    /**
     *
     * @return
     * The ProductId
     */
    public String getProductId() {
        return id;
    }

    /**
     *
     * @param ProductId
     * The ProductId
     */
    public void setProductId(String ProductId) {
        this.id = ProductId;
    }

    /**
     *
     * @return
     * The ProductType
     */
    public String getProductType() {
        return ProductType;
    }

    /**
     *
     * @param ProductType
     * The ProductType
     */
    public void setProductType(String ProductType) {
        this.ProductType = ProductType;
    }

    /**
     *
     * @return
     * The Rating
     */
    public Double getRating() {
        return Rating;
    }

    /**
     *
     * @param Rating
     * The Rating
     */
    public void setRating(Double Rating) {
        this.Rating = Rating;
    }

    /**
     *
     * @return
     * The StorageInfo
     */
    public String getStorageInfo() {
        return StorageInfo;
    }

    /**
     *
     * @param StorageInfo
     * The StorageInfo
     */
    public void setStorageInfo(String StorageInfo) {
        this.StorageInfo = StorageInfo;
    }

    /**
     *
     * @return
     * The UnitPrice
     */
    public Double getUnitPrice() {
        return UnitPrice;
    }

    /**
     *
     * @param UnitPrice
     * The UnitPrice
     */
    public void setUnitPrice(Double UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    /**
     *
     * @return
     * The UnitType
     */
    public String getUnitType() {
        return UnitType;
    }

    /**
     *
     * @param UnitType
     * The UnitType
     */
    public void setUnitType(String UnitType) {
        this.UnitType = UnitType;
    }


    /**
     *
     * @return
     * The ExtendedInfoUrl
     */
    public String getExtendedInfoUrl() {
        return ExtendedInfoUrl;
    }

    /**
     *
     * @param ExtendedInfoUrl
     * The ExtendedInfoUrl
     */
    public void setExtendedInfoUrl(String ExtendedInfoUrl) {
        this.ExtendedInfoUrl = ExtendedInfoUrl;
    }


}
