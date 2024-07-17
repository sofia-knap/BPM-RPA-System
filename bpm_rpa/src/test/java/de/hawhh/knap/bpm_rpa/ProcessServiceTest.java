package de.hawhh.knap.bpm_rpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessDefinitionRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessInstanceRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.TaskRepresentation;
import de.hawhh.knap.bpm_rpa.services.FormService;
import de.hawhh.knap.bpm_rpa.services.ProcessService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplication-context.xml")
public class ProcessServiceTest {

    private final String BPMN1 = "test1";
    private final String BPMN2 = "test2";
    private final String BPMN3 = "test3";

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    private FormService formService;
    private ProcessService processService;

    @Before
    public void setup() {
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test1.bpmn20.xml").deploy();
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test2.bpmn20.xml").deploy();
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test3.bpmn20.xml").deploy();

        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        runtimeService = processEngine.getRuntimeService();
        repositoryService = processEngine.getRepositoryService();

        formService = new FormService();
        formService.setRepositoryService(repositoryService);
        formService.setRuntimeService(runtimeService);
        formService.setTaskService(taskService);

        processService = new ProcessService();
        processService.setHistoryService(historyService);
        processService.setRepositoryService(repositoryService);
        processService.setRuntimeService(runtimeService);
        processService.setTaskService(taskService);
        processService.setFormService(formService);
    }

    @After
    public void closeProcessEngine() {
        processEngine.close();
    }

    @Test
    public void testGetDeployedProcesses() {
        List<ProcessDefinitionRepresentation> definition = processService.getDeployedProcesses();
        assertTrue(definition.size() >= 2);
        assertEquals(BPMN1, definition.get(0).getKey());
        assertEquals(BPMN2, definition.get(1).getKey());
    }

    @Test
    public void testStartProcess() {
        String processInstanceId = processService.startProcess(BPMN2);
        assertNotNull(processInstanceId);
        assertEquals(BPMN2, runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult().getProcessDefinitionKey());
        String taskID = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
        taskService.complete(taskID);
    }

    @Test
    public void testGetRunningProcesses() {
        String id1 = processService.startProcess(BPMN2);
        List<ProcessInstanceRepresentation> instances = processService.getRunningProcesses();
        assertEquals(1, instances.size());

        String id2 = processService.startProcess(BPMN2);
        instances = processService.getRunningProcesses();
        assertEquals(2, instances.size());

        String taskID = taskService.createTaskQuery().processInstanceId(id2).singleResult().getId();
        taskService.complete(taskID);
        instances = processService.getRunningProcesses();
        assertEquals(1, instances.size());

        taskID = taskService.createTaskQuery().processInstanceId(id1).singleResult().getId();
        taskService.complete(taskID);
        instances = processService.getRunningProcesses();
        assertEquals(0, instances.size());
    }

    @Test
    public void testGetAllTasks() {
        String id1 = processService.startProcess(BPMN2);
        List<TaskRepresentation> instances = processService.getAllTasks();
        assertEquals(1, instances.size());
        assertEquals(id1, instances.get(0).getProcessInstanceId());
        assertEquals("A", instances.get(0).getName());

        String id2 = processService.startProcess(BPMN2);
        instances = processService.getAllTasks();
        assertEquals(2, instances.size());
        assertEquals(id2, instances.get(1).getProcessInstanceId());
        assertEquals("A", instances.get(0).getName());
        assertEquals("A", instances.get(1).getName());

        String taskID = taskService.createTaskQuery().processInstanceId(id2).singleResult().getId();
        taskService.complete(taskID);
        instances = processService.getAllTasks();
        assertEquals(1, instances.size());

        taskID = taskService.createTaskQuery().processInstanceId(id1).singleResult().getId();
        taskService.complete(taskID);
        instances = processService.getAllTasks();
        assertEquals(0, instances.size());
    }

    @Test
    public void testGetTaskForProcess() {
        TaskRepresentation instance = processService.getTaskForProcess(null);
        assertNull(instance);

        String processID = processService.startProcess(BPMN2);
        instance = processService.getTaskForProcess(processID);
        assertNotNull(instance);
        assertEquals("A", instance.getName());

        processID = processService.startProcess(BPMN3);
        instance = processService.getTaskForProcess(processID);
        assertNotNull(instance);
        assertEquals("B", instance.getName());

        assertNull(processService.getTaskForProcess("xxx"));
    }

    @Test
    public void testCompleteTask() {
        String processInstanceId = processService.startProcess(BPMN2);
        String taskID = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
        processService.completeTask(taskID, null);
        assertEquals(processInstanceId, historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult().getId());

        processInstanceId = processService.startProcess(BPMN2);
        taskID = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
        Map<String, Object> formData = new HashMap<>();
        formData.put("accepted", "true");
        formData.put("text", "hello");
        processService.completeTask(taskID, formData);
        assertEquals(processInstanceId, historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult().getId());

        processInstanceId = processService.startProcess(BPMN3);
        taskID = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
        formData = new HashMap<>();
        assertFalse(processService.completeTask(taskID, formData));
        formData.put("text", "hello");
        assertFalse(processService.completeTask(taskID, formData));
        formData.put("accepted", "true");
        processService.completeTask(taskID, formData);
        assertEquals(processInstanceId, historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult().getId());

        assertFalse(processService.completeTask(null, formData));
        assertFalse(processService.completeTask("xxx", formData));
    }

    @Test
    public void testGetCompletedProcesses() {
        assertEquals(0, processService.getCompletedProcesses().size());
        processService.startProcess(BPMN1);
        assertEquals(1, processService.getCompletedProcesses().size());

        processService.startProcess(BPMN1);
        assertEquals(2, processService.getCompletedProcesses().size());

        processService.startProcess(BPMN1);
        assertEquals(3, processService.getCompletedProcesses().size());
    }

}