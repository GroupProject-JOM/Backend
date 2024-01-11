package org.jom.Model;

import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.SupplierDAO;
import org.jom.Dao.UserDAO;

public class EmployeeModel extends UserModel{
    private int eId;
    private String dob;
    private String nic;
    private String photo;
    private String gender;
    private int emp;

    public EmployeeModel() {
    }

    public EmployeeModel(int eId) {
        this.eId = eId;
    }

    public EmployeeModel(int id, int eId) {
        super(id);
        this.eId = eId;
    }

    public EmployeeModel(int id, String first_name, String phone, String add_line_3, String role) {
        super(id, first_name, phone, add_line_3, role);
    }

    public EmployeeModel(String first_name, String last_name, String phone, String add_line_1, String add_line_2, String add_line_3, String role, int eId, String dob, String nic, String gender, int emp) {
        super(first_name, last_name, phone, add_line_1, add_line_2, add_line_3, role);
        this.eId = eId;
        this.dob = dob;
        this.nic = nic;
        this.gender = gender;
        this.emp = emp;
    }

    public EmployeeModel(String first_name, String last_name, String email, String password, String phone, String add_line_1, String add_line_2, String add_line_3, String role, String dob, String nic, String gender, int emp) {
        super(first_name, last_name, email, password, phone, add_line_1, add_line_2, add_line_3, role);
        this.dob = dob;
        this.nic = nic;
        this.gender = gender;
        this.emp = emp;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getEmp() {
        return emp;
    }

    public void setEmp(int emp) {
        this.emp = emp;
    }

    @Override
    public void EmployeeRegister() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        this.eId = employeeDAO.register(this);
    }

    @Override
    public boolean updateEmployee(){
        EmployeeDAO employeeDAO = new EmployeeDAO();
        return employeeDAO.updateEmployee(this);
    }

    public void getUserId(){
        EmployeeDAO employeeDAO = new EmployeeDAO();
        this.setId(employeeDAO.getUserId(this.eId));
    }

    public boolean NICExists(){
        EmployeeDAO employeeDAO = new EmployeeDAO();
        boolean status = employeeDAO.nicExists(this.nic);
        return  status;
    }

    public int getEId(){
        EmployeeDAO employeeDAO = new EmployeeDAO();
        return employeeDAO.getEId(this.nic);
    }

    public void getEIdById(){
        EmployeeDAO employeeDAO = new EmployeeDAO();
        this.eId = employeeDAO.getEIdById(this.getId());
    }
}
