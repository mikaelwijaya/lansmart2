package id.ac.ukdw.lansmart.constructor;

public class Seller extends id.ac.ukdw.lansmart.constructor.User {

    private String name, position, status;

    public Seller() {

    }

    public Seller(String userName, String name, String password, String createBy, String editBy) {
        super.setUserName(userName);
        super.setPassword(password);
        super.setRole(2);
        super.setCreateBy(createBy);
        super.setEditBy(editBy);
        this.name = name;
        this.position = "Seller";
        this.status = "Aktif";
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
}