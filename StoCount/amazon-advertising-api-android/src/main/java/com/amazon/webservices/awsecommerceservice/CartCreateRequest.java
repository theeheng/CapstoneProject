// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import com.amazon.webservices.awsecommerceservice.cartcreaterequest.Items;
import java.util.List;

public class CartCreateRequest implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "MergeCart")
	@Order(value=0)
	public String mergeCart;	
	
	@Element(name = "Items")
	@Order(value=1)
	public Items items;	
	
	@Element(name = "ResponseGroup")
	@Order(value=2)
	public List<String> responseGroup;	
	
    
}