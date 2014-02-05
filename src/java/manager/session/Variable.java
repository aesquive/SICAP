
package manager.session;

/**
 * Clase que se encarga de almacenar una variable dado un nombre , valor y clase (el tipo
 * de instancia de la que es el valor)
 * 
 * @author zorin
 */
public class Variable {
    
   private String name;
   private Object value;
   private Class valueClass;
   
   public Variable(String name , Object value , Class valueClass){
       this.name=name;
       this.value=value;
       this.valueClass=valueClass;
   }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the valueClass
     */
    public Class getValueClass() {
        return valueClass;
    }

    /**
     * @param valueClass the valueClass to set
     */
    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }
}
