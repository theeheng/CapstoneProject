// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

@RootElement(name = "CartGetResponse", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class CartGetResponse implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "OperationRequest")
	@Order(value=0)
	public OperationRequest operationRequest;	
	
	@Element(name = "Cart")
	@Order(value=1)
	public List<Cart> cart;	
	
    
}