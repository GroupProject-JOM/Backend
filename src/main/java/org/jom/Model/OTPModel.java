package org.jom.Model;

import org.jom.Dao.OTPDAO;

public class OTPModel {
    private int id;
    private int userId;
    private String userEmail;
    private int otp;
    private int validity;

    public OTPModel() {
    }

    public OTPModel(int userId, String userEmail, int otp) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.otp = otp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getOtp() {
        return otp;
    }

    public int getValidity() {
        return validity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public void setValidity(int validity) {
        this.validity = validity;
        OTPDAO otpDao = new OTPDAO();
        otpDao.setValidity(this.id);
    }
    public void saveOTP(){
        OTPDAO otpDao = new OTPDAO();
        this.id = otpDao.saveOTP(this);

    }
}
