// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;

@RootElement(name = "SimilarityLookupResponse", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class SimilarityLookupResponse implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "OperationRequest")
	@Order(value=0)
	public OperationRequest operationRequest;	
	
	@Element(name = "Items")
	@Order(value=1)
	public List<Items> items;	
	
    
}