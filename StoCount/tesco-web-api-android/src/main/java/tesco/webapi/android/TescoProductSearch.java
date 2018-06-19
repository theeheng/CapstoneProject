package tesco.webapi.android;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 17/11/2015.
 */
public class TescoProductSearch {

    private TescoSeachResult uk;
    private List<TescoBarcodeProduct> products = new ArrayList<TescoBarcodeProduct>();

    public void setUk(TescoSeachResult value) {
        uk = value;
    }

    public TescoSeachResult getUk() {
        return uk;
    }

    /**
     *
     * @return
     * The Products
     */
    public List<TescoProduct> getProducts() {
        if(this.uk != null && this.uk.ghs != null & this.uk.ghs.products != null)
            return this.uk.ghs.products.results;
        else
            return null;
    }

    public List<TescoBarcodeProduct> getBarcodeProduct() {
        if(products != null)
            return products;
        else
            return null;
    }


    public class TescoProductSearchTotal{
        private Integer all;

        public void setAll(Integer value) { all = value; }
        public Integer getAll() { return all; }

    }

    public class TescoSeachResult {
        private TescoSearchResultGhs ghs;

        public void setGhs(TescoSearchResultGhs value) {
            ghs = value;
        }

        public TescoSearchResultGhs getGhs() {
            return ghs;
        }
    }

    public class TescoSearchResultGhs {
        private TescoSearchResultProduct products;

        public void setProducts(TescoSearchResultProduct value) {
            products = value;
        }

        public TescoSearchResultProduct getProducts() {
            return products;
        }
    }

    public class TescoSearchResultProduct {
        private TescoProductSearchTotal totals;
        private List<TescoProduct> results = new ArrayList<TescoProduct>();

        public void setTotals(TescoProductSearchTotal value) {
            totals = value;
        }

        public TescoProductSearchTotal getTotals() {
            return totals;
        }

        /**
         *
         * @return
         * The Products
         */
        public List<TescoProduct> getProducts() {
            return results;
        }

        /**
         *
         * @param Products
         * The Products
         */
        public void setProducts(List<TescoProduct> Products) {
            this.results = Products;
        }

    }


}