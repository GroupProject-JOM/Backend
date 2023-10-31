package org.jom.Model;

import org.jom.Dao.EmployeeDAO;

public class EmployeeModel extends UserModel{
    private int eId;
    private String dob;
    private String nic;
    private String photo;

    public EmployeeModel() {
    }

    public EmployeeModel(int id, String first_name, String phone, String add_line_3, String role) {
        super(id, first_name, phone, add_line_3, role);
    }

    public EmployeeModel(String first_name, String last_name, String email, String password, String phone, String add_line_1, String add_line_2, String add_line_3, String role, String dob, String nic) {
        super(first_name, last_name, email, password, phone, add_line_1, add_line_2, add_line_3, role);
        this.dob = dob;
        this.nic = nic;
    }

    public int geteId() {
        return eId;
    }

    public void seteId(int eId) {
        this.eId = eId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public void EmployeeRegister() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        this.eId = employeeDAO.register(this);
    }
}
