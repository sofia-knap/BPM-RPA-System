package de.hawhh.knap.bpm_rpa.elementRepresentation;

/**
 * Represents a process definition.
 * Is used to translate an object of ProcessDefinition to be returned to the
 * REST API
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class ProcessDefinitionRepresentation {

    private String name;
    private String key;
    private String resourceName;

    /**
     * Constructor
     * 
     * @param name         name of the process
     * @param key          ID of the process
     * @param resourceName directory of the process file
     */
    public ProcessDefinitionRepresentation(String name, String key, String resourceName) {
        this.name = name;
        this.key = key;
        this.resourceName = resourceName;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getResourceName() {
        return resourceName;
    }
}
