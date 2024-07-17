package de.hawhh.knap.bpm_rpa.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hawhh.knap.bpm_rpa.elementRepresentation.FormPropertyRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.HistoricProcessInstanceRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessDefinitionRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessInstanceRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.TaskRepresentation;

/**
 * Service class that communicates with the Activiti process engine
 * 
 * @author Sofia Knap
 * @version 1.0
 */
@Service
public class ProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FormService formService;

    /**
     * Gets all deployed process definitions that can be executed
     * 
     * @return list of ProcessDefinitionRepresentation objects
     */
    public List<ProcessDefinitionRepresentation> getDeployedProcesses() {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        return processDefinitions.stream().map(this::convertToPDR).collect(Collectors.toList());
    }

    /**
     * Starts a process instance of a specific process
     * 
     * @param processDefinitionKey Key of the process definition
     * @return ID of the created and active process instance
     */
    public String startProcess(String processDefinitionKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        return processInstance.getId();
    }

    /**
     * Gets all active process instances
     * 
     * @return list of ProcessInstanceRepresentation objects
     */
    public List<ProcessInstanceRepresentation> getRunningProcesses() {
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery().list();
        return instances.stream().map(this::convertToPIR).collect(Collectors.toList());
    }

    /**
     * Gets all open user tasks
     * 
     * @return a list of TaskRepresentation objects
     */
    public List<TaskRepresentation> getAllTasks() {
        List<Task> usertasks = taskService.createTaskQuery().list();
        return usertasks.stream().map(this::convertToTR).collect(Collectors.toList());
    }

    /**
     * Gets the open user task for a specific process instance
     * 
     * @param processInstanceId ID of the process instance
     * @return TaskRepresentation object
     */
    public TaskRepresentation getTaskForProcess(String processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        Task usertask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (usertask == null) {
            return null;
        }
        TaskRepresentation task = convertToTR(usertask);
        List<FormProperty> formProperties = formService.getFormPropertiesFromBpmn(processInstanceId, usertask.getId());

        List<FormPropertyRepresentation> formRep = new ArrayList<FormPropertyRepresentation>();
        for (FormProperty property : formProperties) {
            formRep.add(new FormPropertyRepresentation(property.getId(), property.getName(), property.getVariable(),
                    property.getExpression(),
                    property.isRequired(), property.getFormValues()));
        }

        // Add the user task form defined in the XML to the representation of the task
        task.setFormProperties(formRep);
        return task;
    }

    /**
     * Completes a user task
     * 
     * @param taskId   ID of the task that should be completed
     * @param formData Variables in JSON format that will be stored as a process
     *                 variable
     * @return true if successful
     *         false if an exception was thrown
     */
    public boolean completeTask(String taskId, Map<String, Object> formData) {
        if (taskId == null) {
            return false;
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return false;
        }

        // Validate required form properties
        List<FormProperty> formProperties = formService.getFormPropertiesFromBpmn(task.getProcessInstanceId(), taskId);
        for (FormProperty property : formProperties) {
            if (property.isRequired() && !formData.containsKey(property.getId())) {
                return false;
            }
        }

        try {
            taskService.complete(taskId, formData);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Gets all copleted process instances
     * 
     * @return list of HistoricProcessInstanceRepresentation objects
     */
    public List<HistoricProcessInstanceRepresentation> getCompletedProcesses() {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceEndTime().desc().list();
        return historicProcessInstances.stream().map(this::convertToHPIR).collect(Collectors.toList());
    }

    /**
     * Converts ProcessDefinition into ProcessDefinitionRepresentation
     * 
     * @param processDefinition ProcessDefinition object that should be converted
     * @return ProcessDefinitionRepresentation object
     */
    private ProcessDefinitionRepresentation convertToPDR(ProcessDefinition processDefinition) {
        return new ProcessDefinitionRepresentation(
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getResourceName());
    }

    /**
     * Converts processInstance into ProcessInstanceRepresentation
     * 
     * @param processInstance processInstance object that should be converted
     * @return ProcessInstanceRepresentation object
     */
    private ProcessInstanceRepresentation convertToPIR(ProcessInstance processInstance) {
        Task currentTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        return new ProcessInstanceRepresentation(
                processInstance.getProcessInstanceId(),
                processInstance.getProcessDefinitionName(),
                processInstance.getProcessDefinitionKey(),
                processInstance.isSuspended(),
                processInstance.getStartTime(),
                currentTask != null ? currentTask.getName() : "No active task");
    }

    /**
     * Converts Task into TaskRepresentation
     * 
     * @param task Task object that should be converted
     * @return TaskRepresentation object
     */
    private TaskRepresentation convertToTR(Task task) {
        return new TaskRepresentation(
                task.getId(),
                task.getName(),
                task.getProcessInstanceId(),
                task.getOwner(),
                task.getAssignee());
    }

    /**
     * Converts HistoricProcessInstance into HistoricProcessInstanceRepresentation
     * 
     * @param historicProcessInstance HistoricProcessInstance object that should be
     *                                converted
     * @return HistoricProcessInstanceRepresentation object
     */
    private HistoricProcessInstanceRepresentation convertToHPIR(HistoricProcessInstance historicProcessInstance) {
        return new HistoricProcessInstanceRepresentation(
                historicProcessInstance.getId(),
                historicProcessInstance.getProcessDefinitionName(),
                historicProcessInstance.getProcessDefinitionKey(),
                historicProcessInstance.getStartTime(),
                historicProcessInstance.getEndTime(),
                historicProcessInstance.getDurationInMillis(),
                historicProcessInstance.getEndActivityId(),
                historicProcessInstance.getStartUserId(),
                historicProcessInstance.getStartActivityId(),
                historicProcessInstance.getDeleteReason(),
                historicProcessInstance.getName());
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setFormService(FormService formService) {
        this.formService = formService;
    }
}