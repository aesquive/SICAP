package db.pojos;
// Generated 29/03/2014 08:56:02 AM by Hibernate Tools 3.6.0



/**
 * User generated by hbm2java
 */
public class User  implements java.io.Serializable {


     private Integer iduser;
     private Tipousuario tipousuario;
     private String user;
     private String password;

    public User() {
    }

	
    public User(String user) {
        this.user = user;
    }
    public User(Tipousuario tipousuario, String user, String password) {
       this.tipousuario = tipousuario;
       this.user = user;
       this.password = password;
    }
   
    public Integer getIduser() {
        return this.iduser;
    }
    
    public void setIduser(Integer iduser) {
        this.iduser = iduser;
    }
    public Tipousuario getTipousuario() {
        return this.tipousuario;
    }
    
    public void setTipousuario(Tipousuario tipousuario) {
        this.tipousuario = tipousuario;
    }
    public String getUser() {
        return this.user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }




}


