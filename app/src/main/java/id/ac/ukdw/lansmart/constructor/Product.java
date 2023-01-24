package id.ac.ukdw.lansmart.constructor;

public class Product {
    private String sellerId, productId, nameProduct, status, category, editBy, url;
    private double price;
    private int available;

    public Product(){

    }

    public Product(String sellerId, String productId, String nameProduct, double price, int available, String status, String category, String url){
        this.sellerId = sellerId;
        this.productId = productId;
        this.nameProduct = nameProduct;
        this.price = price;
        this.available = available;
        this.status = status;
        this.category = category;
        this.editBy = sellerId;
        this.url = url;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
