package db.pojos;
// Generated 5/04/2014 12:20:41 AM by Hibernate Tools 3.6.0



/**
 * Regcuentauser generated by hbm2java
 */
public class Regcuentauser  implements java.io.Serializable {


     private Integer idRegCuentaUser;
     private Regcuenta regcuenta;
     private User user;

    public Regcuentauser() {
    }

    public Regcuentauser(Regcuenta regcuenta, User user) {
       this.regcuenta = regcuenta;
       this.user = user;
    }
   
    public Integer getIdRegCuentaUser() {
        return this.idRegCuentaUser;
    }
    
    public void setIdRegCuentaUser(Integer idRegCuentaUser) {
        this.idRegCuentaUser = idRegCuentaUser;
    }
    public Regcuenta getRegcuenta() {
        return this.regcuenta;
    }
    
    public void setRegcuenta(Regcuenta regcuenta) {
        this.regcuenta = regcuenta;
    }
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }




}


