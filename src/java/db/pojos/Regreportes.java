package db.pojos;
// Generated 5/05/2014 10:56:00 PM by Hibernate Tools 3.6.0



/**
 * Regreportes generated by hbm2java
 */
public class Regreportes  implements java.io.Serializable {


     private Integer idRegReportes;
     private String desReportes;
     private String ruta;

    public Regreportes() {
    }

    public Regreportes(String desReportes, String ruta) {
       this.desReportes = desReportes;
       this.ruta = ruta;
    }
   
    public Integer getIdRegReportes() {
        return this.idRegReportes;
    }
    
    public void setIdRegReportes(Integer idRegReportes) {
        this.idRegReportes = idRegReportes;
    }
    public String getDesReportes() {
        return this.desReportes;
    }
    
    public void setDesReportes(String desReportes) {
        this.desReportes = desReportes;
    }
    public String getRuta() {
        return this.ruta;
    }
    
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }




}


