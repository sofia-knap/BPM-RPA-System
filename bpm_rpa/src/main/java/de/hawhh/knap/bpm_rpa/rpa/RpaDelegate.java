package de.hawhh.knap.bpm_rpa.rpa;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface class to communicate with the RPA engine Robot Framework.
 * Initiated if a service task refers this class in its XML as follows:
 * 'activiti:class="de.hawhh.knap.bpm_rpa.RpaDelegate"'
 * 
 * @author Sofia Knap
 * @version 1.0
 */

public class RpaDelegate implements JavaDelegate {

    // Logger, returns output on the console. Configuration in logback.xml
    private static final Logger LOGGER = LoggerFactory.getLogger(RpaDelegate.class);

    // Name of the robot file stored in the process XML as an Activiti field in an
    // extention element
    private Expression robotPath;

    // Directory where the robot files are stored
    private String robotDirectory = "src/main/resources/processes/robots/";

    private Process robotProcess;
    private String processInstanceId;

    @Override
    public void execute(DelegateExecution execution) throws ActivitiException {

        String robotName = (String) robotPath.getValue(execution);
        if (robotName == null) {
            LOGGER.error("Robot script path is null. Ensure the variable 'robotScriptPath' is set in the process.");
            execution.setVariable("taskStatus", "ERROR");
            throw new IllegalArgumentException("Robot script path cannot be null");
        }

        // get full path of the robot file and check iff it exists
        String robotFilePath = robotDirectory + robotName + ".robot";
        File robotFile = new File(robotFilePath);

        if (!robotFile.exists()) {
            LOGGER.error("Robot file not found: {}", robotFilePath);
            execution.setVariable("taskStatus", "ERROR");
            throw new ActivitiException("Robot file not found. See log for details.");
        }

        try {
            // execute robot file
            this.processInstanceId = execution.getProcessInstanceId();
            String robotVariable = (String) execution.getVariable("robotVariable");
            ProcessBuilder pb = new ProcessBuilder("robot", "--exitonfailure",
                    "--variable", "PROCESS_INSTANCE_ID:" + processInstanceId,
                    "--variable", "CUSTOM_VARIABLE:" + robotVariable, robotFilePath);
            Process robotProcess = pb.start();

            // Log of the robot execution in the console
            LOGGER.info("Executing Robot Framework task for: {}", robotName);
            LOGGER.info("Robot file path: {}", robotFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(robotProcess.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(robotProcess.getErrorStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
                output.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                LOGGER.error(line);
            }

            /*
             * Wait for robot execution through Robot Framework to end
             * exitCode 0 if execution was successful
             */
            int exitCode = robotProcess.waitFor();

            if (exitCode == 0) {
                execution.setVariable("taskStatus", "SUCCESS");
                LOGGER.info("Robot task {} completed successfully.", robotName);

                // Parse the output to get updated variables
                Map<String, Object> updatedVariables = parseRobotOutput(output.toString());

                // Set the updated variables in Activiti
                for (Map.Entry<String, Object> entry : updatedVariables.entrySet()) {
                    execution.setVariable(entry.getKey(), entry.getValue());
                }
            } else {
                // Stop process if robot execution was not successful
                execution.setVariable("taskStatus", "FAILURE");
                LOGGER.error("Robot task {} failed with exit code: {}", robotName, exitCode);
                throw new ActivitiException("Robot task failed. See log for details.");
            }
        } catch (InterruptedException e) {
            robotProcess.destroyForcibly();
            LOGGER.info("Robot process for instance {} canceled.", processInstanceId);
        } catch (Exception e) {
            execution.setVariable("taskStatus", "ERROR");
            LOGGER.error("Exception while executing Robot task {}: ", robotName, e);
            throw new ActivitiException("Exception while executing Robot task. See log for details.");
        }
    }

    private Map<String, Object> parseRobotOutput(String output) {
        Map<String, Object> updatedVariables = new HashMap<>();
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (isJson(value)) {
                        JSONObject jsonObj = new JSONObject(value);
                        updatedVariables.put(key, jsonObj.toMap());
                    } else {
                        updatedVariables.put(key, value);
                    }
                }
            }
        }
        return updatedVariables;
    }

    private boolean isJson(String value) {
        try {
            new JSONObject(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setRobotPath(Expression robotPath) {
        this.robotPath = robotPath;
    }

}