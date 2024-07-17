package de.hawhh.knap.bpm_rpa.rest;

import de.hawhh.knap.bpm_rpa.elementRepresentation.HistoricProcessInstanceRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessDefinitionRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.ProcessInstanceRepresentation;
import de.hawhh.knap.bpm_rpa.elementRepresentation.TaskRepresentation;
import de.hawhh.knap.bpm_rpa.rpa.RpaDelegate;
import de.hawhh.knap.bpm_rpa.services.ProcessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Implements the logic of specific HTTP-requests through the REST API
 * http://localhost:8080
 */
@RestController
public class ProcessRestController {

        // Logger, returns output on the console. Configuration in logback.xml
        private static final Logger LOGGER = LoggerFactory.getLogger(RpaDelegate.class);

        @Autowired
        private ProcessService processService;

        /**
         * GET request for process definitions that are deployed and executable
         * 
         * @return Status 200 with a list of ProcessDefinitionRepresentation objects
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/processes")
        public ResponseEntity<List<ProcessDefinitionRepresentation>> getDeployedProcesses() {
                try {
                        List<ProcessDefinitionRepresentation> deployedProcesses = processService.getDeployedProcesses();
                        LOGGER.info("Liste aller deployten Prozesse wird ausgegeben.");
                        return ResponseEntity.ok(deployedProcesses);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * Starts a process instance
         * 
         * @param processDefinitionKey Key of the process that should be started
         * @return Status 200 with ID of the process instance
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/start-process/{processDefinitionKey}")
        public ResponseEntity<String> startProcess(@PathVariable String processDefinitionKey) {
                try {
                        String processInstanceId = processService.startProcess(processDefinitionKey);
                        LOGGER.info("Prozess {} wurde gestartet", processInstanceId);
                        return ResponseEntity.ok(processInstanceId);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * GET request for all aktive process instances
         * 
         * @return Status 200 with a list of ProcessInstanceRepresentation objects
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/process-instances") // TODO: vielleicht noch Filteroptionen einbauen
        public ResponseEntity<List<ProcessInstanceRepresentation>> getRunningProcesses() {
                try {
                        List<ProcessInstanceRepresentation> processInstances = processService.getRunningProcesses();
                        LOGGER.info("Liste aller laufenden Prozesseinstanzen wird ausgegeben.");
                        return ResponseEntity.ok(processInstances);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * GET request for all open service tasks
         * 
         * @return Status 200 with a list of TaskRepresentation objects
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/get-all-tasks") // TODO: vielleicht noch Filteroptionen einbauen
        public ResponseEntity<List<TaskRepresentation>> getAllTasks() {
                try {
                        List<TaskRepresentation> tasks = processService.getAllTasks();
                        LOGGER.info("Liste aller offenen Tasks wird ausgegeben.");
                        return ResponseEntity.ok(tasks);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * GET request for the open user task of a specific process instance
         * 
         * @param processInstanceId ID of the process instance
         * @return Status 200 and a TaskRepresentation object
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/get-task/{processInstanceId}")
        public ResponseEntity<TaskRepresentation> getTasksForProcess(@PathVariable String processInstanceId) {
                try {
                        TaskRepresentation usertask = processService.getTaskForProcess(processInstanceId);
                        LOGGER.info("Liste aller offenen Tasks der Proressinstanz {} wird ausgegeben.",
                                        processInstanceId);
                        return ResponseEntity.ok(usertask);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * POST request to complete a user task
         * 
         * @param taskId   ID of the task that should be completed
         * @param formData Variables in JSON format that will be stored as a process
         *                 variable
         * @return Status 200 if successful
         *         Status 400 if an exception was thrown
         */
        @PostMapping("/complete-task/{taskId}")
        public ResponseEntity<Void> completeTask(@PathVariable String taskId,
                        @RequestBody Map<String, Object> formData) {
                try {
                        boolean response = processService.completeTask(taskId, formData);
                        if (!response) {
                                return ResponseEntity.notFound().build();
                        } else {
                                LOGGER.info("Task mit ID {} wurde erledigt.", taskId);
                                return ResponseEntity.ok().build();
                        }
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }

        }

        /**
         * GET request for all completed process instances
         * 
         * @return Status 200 with a list of HistoricProcessInstanceRepresentation
         *         objects
         *         Status 400 if an exception was thrown
         */
        @GetMapping("/get-completed-processes")
        public ResponseEntity<List<HistoricProcessInstanceRepresentation>> getCompletedProcesses() {
                try {
                        List<HistoricProcessInstanceRepresentation> completedProcesses = processService
                                        .getCompletedProcesses();
                        LOGGER.info("Liste aller abgeschlossenen Prozesseinstanzen wird ausgegeben.");
                        return ResponseEntity.ok(completedProcesses);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                }
        }

}
