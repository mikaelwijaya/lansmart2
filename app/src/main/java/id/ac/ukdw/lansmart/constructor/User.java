package id.ac.ukdw.lansmart.constructor;

public class User {
    private String userName, name, password, createBy, editBy;
    private int role;

    public User(){

    }

    public User(String userName, String name, String password, String createBy, String editBy, int role) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.createBy = createBy;
        this.editBy = editBy;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
