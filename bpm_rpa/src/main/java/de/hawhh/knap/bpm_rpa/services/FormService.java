package de.hawhh.knap.bpm_rpa.services;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Help class to extract the Form Properties from a activity from the BPMN in
 * the Activiti database
 * 
 * @author Sofia Knap
 * @version 1.0
 */
@Service
public class FormService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    public List<FormProperty> getFormPropertiesFromBpmn(String processInstanceId, String taskId) {

        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(process.getProcessDefinitionId());
        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            System.out.println("taskid XXX is not existed.");
            return null;
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        for (FlowElement flowElement : flowElementCollection) {
            if ("UserTask".equals(flowElement.getClass().getSimpleName())
                    && taskDefinitionKey.equals(flowElement.getId())) {
                return ((UserTask) flowElement).getFormProperties();
            }
        }
        return null;
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
}