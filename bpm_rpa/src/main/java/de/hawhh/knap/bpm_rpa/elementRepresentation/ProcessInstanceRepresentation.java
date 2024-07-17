package de.hawhh.knap.bpm_rpa.elementRepresentation;

import java.util.Date;

/**
 * Represents a process instance that is active and running.
 * Is used to translate an object of ProcessInstance to be returned to the REST
 * API
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class ProcessInstanceRepresentation {

    private String processInstanceId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private boolean isSuspended;
    private Date startTime;
    private String currentTask;

    /**
     * Constrructor
     * 
     * @param processInstanceId     ID of the process instance
     * @param processDefinitionName name of the process definition
     * @param processDefinitionKey  key of the process definition
     * @param isSuspended           is true when process suspended
     * @param startTime             start time of the instance
     */
    public ProcessInstanceRepresentation(String processInstanceId, String processDefinitionName,
            String processDefinitionKey, boolean isSuspended, Date startTime, String currentTask) {
        this.processInstanceId = processInstanceId;
        this.processDefinitionName = processDefinitionName;
        this.processDefinitionKey = processDefinitionKey;
        this.isSuspended = isSuspended;
        this.startTime = startTime;
        this.currentTask = currentTask;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public String getIsSuspended() {
        return String.valueOf(isSuspended);
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getCurrentTask() {
        return currentTask;
    }
}
