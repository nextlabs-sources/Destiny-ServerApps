package com.nextlabs.destiny.console.dto.policymgmt.porting;

public class ImportConflictDTO {

    private String type;

    private String name;

    private String message;

    public ImportConflictDTO(String elementType, String elementName, String message) {
        super();
        this.type = elementType;
        this.name = elementName;
        this.message = message;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
