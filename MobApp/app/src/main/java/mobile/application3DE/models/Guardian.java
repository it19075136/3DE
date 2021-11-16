package mobile.application3DE.models;

import java.io.Serializable;

public class Guardian implements Serializable {

    private String guardianName,guardianMail,guardianMob;

    public Guardian(String guardianName, String guardianMail, String guardianPhone) {
        this.guardianName = guardianName;
        this.guardianMail = guardianMail;
        this.guardianMob = guardianPhone;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianMail() {
        return guardianMail;
    }

    public void setGuardianMail(String guardianMail) {
        this.guardianMail = guardianMail;
    }

    public String getGuardianPhone() {
        return guardianMob;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianMob = guardianPhone;
    }
}
