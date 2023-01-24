package id.ac.ukdw.lansmart.constructor;

public class Cart {
    private String cartId, buyerId, productId, productName, status;
    private int itemPerProduct;
    private double pricePerProduct;
    private boolean checkOut;

    public Cart(){

    }

    public Cart(String buyerId, String productId, String productName, int itemPerProduct, double pricePerProduct){
        this.cartId = buyerId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.productName = productName;
        this.itemPerProduct = itemPerProduct;
        this.pricePerProduct = pricePerProduct;
        this.status = "Unchecked";
        this.checkOut = false;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getItemPerProduct() {
        return itemPerProduct;
    }

    public void setItemPerProduct(int itemPerProduct) {
        this.itemPerProduct = itemPerProduct;
    }

    public double getPricePerProduct() {
        return pricePerProduct;
    }

    public void setPricePerProduct(double pricePerProduct) {
        this.pricePerProduct = pricePerProduct;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCheckOut() {
        return checkOut;
    }

    public void setCheckOut(boolean checkOut) {
        this.checkOut = checkOut;
    }
}
