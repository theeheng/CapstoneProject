// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice.cartmodifyrequest.items;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.math.BigInteger;

public class Item implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "Action")
	@Order(value=0)
	public String action;	
	
	@Element(name = "CartItemId")
	@Order(value=1)
	public String cartItemId;	
	
	@Element(name = "Quantity")
	@Order(value=2)
	public BigInteger quantity;	
	
    
}