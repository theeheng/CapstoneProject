// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

@RootElement(name = "CustomerReviews", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class CustomerReviews implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "IFrameURL")
	@Order(value=0)
	public String iFrameURL;	
	
	@Element(name = "HasReviews")
	@Order(value=1)
	public Boolean hasReviews;	
	
    
}