package SpringBootEcom.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fieldName;
    Long fieldId;
    String field;


    public ResourceNotFoundException() {

    }

    public ResourceNotFoundException(String resourceName,String field,String fieldName) {
        super(String.format("%s not found with %s : %s",resourceName,field,fieldName));
        this.fieldName = fieldName;
        this.resourceName = resourceName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s : %d",resourceName,field,fieldId));
        this.resourceName = resourceName;
        this.fieldId = fieldId;
        this.field = field;
    }
}
