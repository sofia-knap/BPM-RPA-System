package de.hawhh.knap.bpm_rpa.customTaskServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.io.FileUtils;

/**
 * Class that implements the logic for the servive task "Bewerbungseingang
 * prüfen" of the process "Bewerbungsprozess"
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class BewerbungseingangPruefenDelegate implements JavaDelegate {

    /**
     * directory where the unprocessed applications are stored
     */
    private String sourcePath = "src/main/resources/bewerbung";

    /**
     * directory where the processed applications are stored
     */
    private String destPath = "src/main/resources/bewerbung/alt/";

    @Override
    public void execute(DelegateExecution execution) {
        try {
            File folder = new File(sourcePath);
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    String name = file.getName();
                    String end = name.substring(name.lastIndexOf('.') + 1);
                    if (end.equals("json")) {
                        String path = destPath + execution.getProcessInstanceId() + ".json";
                        File destFile = new File(path);
                        FileUtils.copyFile(listOfFiles[1], destFile);
                        listOfFiles[1].delete();
                        execution.setVariable("bewerbung", 1);
                        return;
                    }
                }
                execution.setVariable("bewerbung", 0);
            }
        } catch (FileNotFoundException | IndexOutOfBoundsException e) {
            execution.setVariable("bewerbung", 0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
