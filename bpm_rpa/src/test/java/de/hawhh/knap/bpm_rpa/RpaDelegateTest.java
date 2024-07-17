package de.hawhh.knap.bpm_rpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.activiti.engine.ActivitiException;
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

import de.hawhh.knap.bpm_rpa.services.FormService;
import de.hawhh.knap.bpm_rpa.services.ProcessService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplication-context.xml")
public class RpaDelegateTest {

    private final String BPMN4 = "test4";
    private final String BPMN5 = "test5";
    private final String BPMN6 = "test6";
    private final String BPMN7 = "test7";

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
                .addClasspathResource("test/test4.bpmn20.xml").deploy();
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test5.bpmn20.xml").deploy();
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test6.bpmn20.xml").deploy();
        processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("test/test7.bpmn20.xml").deploy();

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
    public void executeRpaTest() {
        String id = processService.startProcess(BPMN4);
        assertNotNull(id);
        assertEquals(BPMN4, historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult()
                .getProcessDefinitionKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeRpaTestWithRobotPahNull() {
        processService.startProcess(BPMN5);
    }

    @Test(expected = ActivitiException.class)
    public void executeRpaTestWithFalsePath() {
        processService.startProcess(BPMN6);
    }

    @Test
    public void changedVariablesInRpaTest() {
        processService.startProcess(BPMN7);
    }
}
