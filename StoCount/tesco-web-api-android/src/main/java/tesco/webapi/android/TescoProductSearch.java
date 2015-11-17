package tesco.webapi.android;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 17/11/2015.
 */
public class TescoProductSearch {

    private Integer StatusCode;
    private String StatusInfo;
    private Integer PageNumber;
    private Integer TotalPageCount;
    private Integer TotalProductCount;
    private Integer PageProductCount;
    private List<TescoProduct> Products = new ArrayList<TescoProduct>();

    /**
     *
     * @return
     * The StatusCode
     */
    public Integer getStatusCode() {
        return StatusCode;
    }

    /**
     *
     * @param StatusCode
     * The StatusCode
     */
    public void setStatusCode(Integer StatusCode) {
        this.StatusCode = StatusCode;
    }

    /**
     *
     * @return
     * The StatusInfo
     */
    public String getStatusInfo() {
        return StatusInfo;
    }

    /**
     *
     * @param StatusInfo
     * The StatusInfo
     */
    public void setStatusInfo(String StatusInfo) {
        this.StatusInfo = StatusInfo;
    }

    /**
     *
     * @return
     * The PageNumber
     */
    public Integer getPageNumber() {
        return PageNumber;
    }

    /**
     *
     * @param PageNumber
     * The PageNumber
     */
    public void setPageNumber(Integer PageNumber) {
        this.PageNumber = PageNumber;
    }

    /**
     *
     * @return
     * The TotalPageCount
     */
    public Integer getTotalPageCount() {
        return TotalPageCount;
    }

    /**
     *
     * @param TotalPageCount
     * The TotalPageCount
     */
    public void setTotalPageCount(Integer TotalPageCount) {
        this.TotalPageCount = TotalPageCount;
    }

    /**
     *
     * @return
     * The TotalProductCount
     */
    public Integer getTotalProductCount() {
        return TotalProductCount;
    }

    /**
     *
     * @param TotalProductCount
     * The TotalProductCount
     */
    public void setTotalProductCount(Integer TotalProductCount) {
        this.TotalProductCount = TotalProductCount;
    }

    /**
     *
     * @return
     * The PageProductCount
     */
    public Integer getPageProductCount() {
        return PageProductCount;
    }

    /**
     *
     * @param PageProductCount
     * The PageProductCount
     */
    public void setPageProductCount(Integer PageProductCount) {
        this.PageProductCount = PageProductCount;
    }

    /**
     *
     * @return
     * The Products
     */
    public List<TescoProduct> getProducts() {
        return Products;
    }

    /**
     *
     * @param Products
     * The Products
     */
    public void setProducts(List<TescoProduct> Products) {
        this.Products = Products;
    }
}