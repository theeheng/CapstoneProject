// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.math.BigInteger;

public class NonNegativeIntegerWithUnits implements Serializable {

    private static final long serialVersionUID = -1L;

	@Value
	@Order(value=0)
	public BigInteger value;	
	
	@Attribute(name = "Units")
	@Order(value=1)
	public String units;	
	
    
}