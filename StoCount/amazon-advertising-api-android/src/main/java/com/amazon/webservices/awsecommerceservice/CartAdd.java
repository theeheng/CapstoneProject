// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

@RootElement(name = "CartAdd", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class CartAdd implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "MarketplaceDomain")
	@Order(value=0)
	public String marketplaceDomain;	
	
	@Element(name = "AWSAccessKeyId")
	@Order(value=1)
	public String awsAccessKeyId;	
	
	@Element(name = "AssociateTag")
	@Order(value=2)
	public String associateTag;	
	
	@Element(name = "Validate")
	@Order(value=3)
	public String validate;	
	
	@Element(name = "XMLEscaping")
	@Order(value=4)
	public String xmlEscaping;	
	
	@Element(name = "Shared")
	@Order(value=5)
	public CartAddRequest shared;	
	
	@Element(name = "Request")
	@Order(value=6)
	public List<CartAddRequest> request;	
	
    
}