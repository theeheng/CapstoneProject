// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice.item.alternateversions;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

public class AlternateVersion implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "ASIN")
	@Order(value=0)
	public String asin;	
	
	@Element(name = "Title")
	@Order(value=1)
	public String title;	
	
	@Element(name = "Binding")
	@Order(value=2)
	public String binding;	
	
    
}