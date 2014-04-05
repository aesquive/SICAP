package db.pojos;
// Generated 5/04/2014 12:20:41 AM by Hibernate Tools 3.6.0


import java.util.HashSet;
import java.util.Set;

/**
 * Moneda generated by hbm2java
 */
public class Moneda  implements java.io.Serializable {


     private Integer idMoneda;
     private String desMoneda;
     private Set cuentas = new HashSet(0);

    public Moneda() {
    }

	
    public Moneda(String desMoneda) {
        this.desMoneda = desMoneda;
    }
    public Moneda(String desMoneda, Set cuentas) {
       this.desMoneda = desMoneda;
       this.cuentas = cuentas;
    }
   
    public Integer getIdMoneda() {
        return this.idMoneda;
    }
    
    public void setIdMoneda(Integer idMoneda) {
        this.idMoneda = idMoneda;
    }
    public String getDesMoneda() {
        return this.desMoneda;
    }
    
    public void setDesMoneda(String desMoneda) {
        this.desMoneda = desMoneda;
    }
    public Set getCuentas() {
        return this.cuentas;
    }
    
    public void setCuentas(Set cuentas) {
        this.cuentas = cuentas;
    }




}


