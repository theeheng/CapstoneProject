// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import com.amazon.webservices.awsecommerceservice.item.ImageSets;
import com.amazon.webservices.awsecommerceservice.item.AlternateVersions;
import java.util.List;
import com.amazon.webservices.awsecommerceservice.item.Subjects;
import com.amazon.webservices.awsecommerceservice.item.VariationAttributes;

@RootElement(name = "Item", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class Item implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "ASIN")
	@Order(value=0)
	public String asin;	
	
	@Element(name = "ParentASIN")
	@Order(value=1)
	public String parentASIN;	
	
	@Element(name = "Errors")
	@Order(value=2)
	public Errors errors;	
	
	@Element(name = "DetailPageURL")
	@Order(value=3)
	public String detailPageURL;	
	
	@Element(name = "ItemLinks")
	@Order(value=4)
	public ItemLinks itemLinks;	
	
	@Element(name = "SalesRank")
	@Order(value=5)
	public String salesRank;	
	
	@Element(name = "SmallImage")
	@Order(value=6)
	public Image smallImage;	
	
	@Element(name = "MediumImage")
	@Order(value=7)
	public Image mediumImage;	
	
	@Element(name = "LargeImage")
	@Order(value=8)
	public Image largeImage;	
	
	@Element(name = "ImageSets")
	@Order(value=9)
	public List<ImageSets> imageSets;	
	
	@Element(name = "ItemAttributes")
	@Order(value=10)
	public ItemAttributes itemAttributes;	
	
	@Element(name = "VariationAttributes")
	@Order(value=11)
	public VariationAttributes variationAttributes;	
	
	@Element(name = "RelatedItems")
	@Order(value=12)
	public List<RelatedItems> relatedItems;	
	
	@Element(name = "Collections")
	@Order(value=13)
	public Collections collections;	
	
	@Element(name = "Subjects")
	@Order(value=14)
	public Subjects subjects;	
	
	@Element(name = "OfferSummary")
	@Order(value=15)
	public OfferSummary offerSummary;	
	
	@Element(name = "Offers")
	@Order(value=16)
	public Offers offers;	
	
	@Element(name = "VariationSummary")
	@Order(value=17)
	public VariationSummary variationSummary;	
	
	@Element(name = "Variations")
	@Order(value=18)
	public Variations variations;	
	
	@Element(name = "CustomerReviews")
	@Order(value=19)
	public CustomerReviews customerReviews;	
	
	@Element(name = "EditorialReviews")
	@Order(value=20)
	public EditorialReviews editorialReviews;	
	
	@Element(name = "SimilarProducts")
	@Order(value=21)
	public SimilarProducts similarProducts;	
	
	@Element(name = "Accessories")
	@Order(value=22)
	public Accessories accessories;	
	
	@Element(name = "Tracks")
	@Order(value=23)
	public Tracks tracks;	
	
	@Element(name = "BrowseNodes")
	@Order(value=24)
	public BrowseNodes browseNodes;	
	
	@Element(name = "AlternateVersions")
	@Order(value=25)
	public AlternateVersions alternateVersions;	
	
    
}