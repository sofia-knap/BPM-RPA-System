package de.hawhh.knap.bpm_rpa.elementRepresentation;

import java.util.List;

/**
 * Represents a task.
 * Is used to translate an object of Task to be returned to the REST API
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class TaskRepresentation {

    private String id;
    private String name;
    private String processInstanceId;
    private String owner;
    private String assignee;
    List<FormPropertyRepresentation> formProperties;

    /**
     * Constructor
     * 
     * @param id                ID of the task
     * @param name              name of the task
     * @param processInstanceId ID of the associated process instance
     * @param owner             person that is responsible for that task
     * @param assignee          persen that the task was assigned to
     */
    public TaskRepresentation(String id, String name, String processInstanceId, String owner, String assignee) {
        this.id = id;
        this.name = name;
        this.processInstanceId = processInstanceId;
        this.owner = owner;
        this.assignee = assignee;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getOwner() {
        return owner;
    }

    public List<FormPropertyRepresentation> getFormProperties() {
        return formProperties;
    }

    public void setFormProperties(List<FormPropertyRepresentation> formProperties) {
        this.formProperties = formProperties;
    }

}
