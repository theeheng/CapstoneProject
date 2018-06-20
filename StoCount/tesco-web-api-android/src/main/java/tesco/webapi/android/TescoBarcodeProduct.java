package tesco.webapi.android;

import java.util.ArrayList;

/**
 * Created by htan on 17/11/2015.
 */
public class TescoBarcodeProduct {

    private String gtin;
    private String tpnc;
    private String name;
    private String description;

    /**
     *
     * @return
     * The EANBarcode
     */
    public String getEANBarcode() {
        return gtin.replaceFirst("^0+(?!$)", "") ;
    }

    /**
     *
     * @param EANBarcode
     * The EANBarcode
     */
    public void setEANBarcode(String EANBarcode) {
        this.gtin = EANBarcode;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param Name
     * The Name
     */
    public void setName(String Name) {
        this.name = Name;
    }
    /**
     *
     * @return
     * The Name
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param Description
     * The Name
     */
    public void setDescription(String Description) {
        this.description = Description;
    }

    public void setProductId(String id) {
        this.tpnc = id;
    }

    public String getProductId() {
        return this.tpnc;
    }

}
