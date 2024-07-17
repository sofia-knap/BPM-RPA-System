package de.hawhh.knap.bpm_rpa.elementRepresentation;

import java.util.Date;

/**
 * Represents a process instance that is not active anymore.
 * Is used to translate an object of HistoricProcessInstance to be returned to
 * the REST API
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class HistoricProcessInstanceRepresentation {

    private String processInstanceId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String endActivityId;
    private String startUserId;
    private String startActivityId;
    private String deleteReason;
    private String name;

    /**
     * Constructor
     * 
     * @param processInstanceId     ID of the process instance
     * @param processDefinitionName name of the process definition
     * @param processDefinitionKey  key of the process definition
     * @param startTime             start time of the process
     * @param endTime               time when the process ended
     * @param durationInMillis      duration of the execution
     * @param endActivityId         ID of the activity where the process ended
     * @param startUserId           ID of the user that started the process instance
     * @param startActivityId       ID of the activity where the process started
     * @param deleteReason          reason why the instance was deleted
     * @param name                  name of the process instance
     */
    public HistoricProcessInstanceRepresentation(String processInstanceId, String processDefinitionName,
            String processDefinitionKey, Date startTime, Date endTime, Long durationInMillis, String endActivityId,
            String startUserId, String startActivityId, String deleteReason, String name) {

        this.processInstanceId = processInstanceId;
        this.processDefinitionName = processDefinitionName;
        this.processDefinitionKey = processDefinitionKey;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInMillis = durationInMillis;
        this.endActivityId = endActivityId;
        this.startUserId = startUserId;
        this.startActivityId = startActivityId;
        this.deleteReason = deleteReason;
        this.name = name;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public String getStartActivityId() {
        return startActivityId;
    }

    public String getName() {
        return name;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getEndActivityId() {
        return endActivityId;
    }

    public Long getDurationInMillis() {
        return durationInMillis;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

}