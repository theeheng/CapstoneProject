// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

@RootElement(name = "Cart", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class Cart implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "Request")
	@Order(value=0)
	public Request request;	
	
	@Element(name = "CartId")
	@Order(value=1)
	public String cartId;	
	
	@Element(name = "HMAC")
	@Order(value=2)
	public String hmac;	
	
	@Element(name = "URLEncodedHMAC")
	@Order(value=3)
	public String urlEncodedHMAC;	
	
	@Element(name = "PurchaseURL")
	@Order(value=4)
	public String purchaseURL;	
	
	@Element(name = "MobileCartURL")
	@Order(value=5)
	public String mobileCartURL;	
	
	@Element(name = "SubTotal")
	@Order(value=6)
	public Price subTotal;	
	
	@Element(name = "CartItems")
	@Order(value=7)
	public CartItems cartItems;	
	
	@Element(name = "SavedForLaterItems")
	@Order(value=8)
	public SavedForLaterItems savedForLaterItems;	
	
	@Element(name = "SimilarProducts")
	@Order(value=9)
	public SimilarProducts similarProducts;	
	
	@Element(name = "TopSellers")
	@Order(value=10)
	public TopSellers topSellers;	
	
	@Element(name = "NewReleases")
	@Order(value=11)
	public NewReleases newReleases;	
	
	@Element(name = "SimilarViewedProducts")
	@Order(value=12)
	public SimilarViewedProducts similarViewedProducts;	
	
	@Element(name = "OtherCategoriesSimilarProducts")
	@Order(value=13)
	public OtherCategoriesSimilarProducts otherCategoriesSimilarProducts;	
	
    
}