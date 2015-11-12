// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import com.amazon.webservices.awsecommerceservice.cartitem.MetaData;

public class CartItem implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "CartItemId")
	@Order(value=0)
	public String cartItemId;	
	
	@Element(name = "ASIN")
	@Order(value=1)
	public String asin;	
	
	@Element(name = "SellerNickname")
	@Order(value=2)
	public String sellerNickname;	
	
	@Element(name = "Quantity")
	@Order(value=3)
	public String quantity;	
	
	@Element(name = "Title")
	@Order(value=4)
	public String title;	
	
	@Element(name = "ProductGroup")
	@Order(value=5)
	public String productGroup;	
	
	@Element(name = "MetaData")
	@Order(value=6)
	public MetaData metaData;	
	
	@Element(name = "Price")
	@Order(value=7)
	public Price price;	
	
	@Element(name = "ItemTotal")
	@Order(value=8)
	public Price itemTotal;	
	
    
}