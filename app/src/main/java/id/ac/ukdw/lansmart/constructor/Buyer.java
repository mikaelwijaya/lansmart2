package id.ac.ukdw.lansmart.constructor;

public class Buyer extends id.ac.ukdw.lansmart.constructor.User {

    private String name, position, cartId, status, address, codePos;

    public Buyer(){

    }

    public Buyer(String userName, String name, String password, String createBy, String editBy, String address, String codePos) {
        super.setUserName(userName);
        super.setPassword(password);
        super.setRole(3);
        super.setCreateBy(createBy);
        super.setEditBy(editBy);
        this.name = name;
        this.position = "Buyer";
        this.cartId = userName;
        this.status = "Aktif";
        this.address = address;
        this.codePos = codePos;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCodePos() {
        return codePos;
    }

    public void setCodePos(String codePos) {
        this.codePos = codePos;
    }
}

