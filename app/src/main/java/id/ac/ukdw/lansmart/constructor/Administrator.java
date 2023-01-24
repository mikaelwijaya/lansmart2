package id.ac.ukdw.lansmart.constructor;

public class Administrator extends User{
    private String name, position;

    public Administrator(){

    }

    public Administrator(String userName, String name, String password, String createBy, String editBy) {
        super.setUserName(userName);
        super.setPassword(password);
        super.setRole(1);
        super.setCreateBy(createBy);
        super.setEditBy(editBy);
        this.name = name;
        this.position = "Admin";
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
}
