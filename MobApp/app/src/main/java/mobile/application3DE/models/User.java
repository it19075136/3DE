package mobile.application3DE.models;

import java.io.Serializable;

public class User implements Serializable {

    String fName,lName,dob,gender,guardianName,guardianMob,guardianMail;

    public User(String fName,String lName,String dob,String gender){
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        this.gender = gender;
    }

    public String get_dob() {
        return dob;
    }

    public String get_fName() {
        return fName;
    }

    public String get_gender() {
        return gender;
    }

    public String get_lName() {
        return lName;
    }

    public void set_fName(String fName) {
        this.fName = fName;
    }

    public void set_lName(String lName) {
        this.lName = lName;
    }

    public void set_dob(String dob) {
        this.dob = dob;
    }

    public void set_gender(String gender) {
        this.gender = gender;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianMob() {
        return guardianMob;
    }

    public String getGuardianMail() {
        return guardianMail;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public void setGuardianMob(String guardianMob) {
        this.guardianMob = guardianMob;
    }

    public void setGuardianMail(String guardianMail) {
        this.guardianMail = guardianMail;
    }

}
