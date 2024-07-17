package de.hawhh.knap.bpm_rpa.customTaskServices;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.github.cliftonlabs.json_simple.Jsoner;

/**
 * Class that implements the logic for the servive task "Bewerbung einordnen" of
 * the process "Bewerbungsprozess"
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class BewerbungEinordnenDelegate implements JavaDelegate {

    /**
     * Directory where the processed application files are stored
     */
    private static String path = "src/main/resources/bewerbung/alt/";

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // get the application
            File file = new File(path + execution.getProcessInstanceId() + ".json");
            FileReader reader = new FileReader(file);
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);

            // extract the graduation type and the grade
            String abschluss = (String) jsonObject.get("abschluss");
            BigDecimal temp = (BigDecimal) jsonObject.get("note");
            Double note = temp.doubleValue();

            // classify the graduation type and upgrade the grade
            double extra = getExtraPoints(abschluss);
            note -= extra;

            /*
             * classify the application by the grade
             * 1, the application is realy good
             * 2, the application is mediokre
             * 3, the application is bad
             */
            int var;
            if (note <= 1.5) {
                var = 1;
            } else if (note > 1.5 && note <= 2.2) {
                var = 2;
            } else {
                var = 3;
            }

            // return the classification as a variable to the process
            execution.setVariable("schnitt", var);

        } catch (JsonException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Classification of the graduation of the applicant
     * 
     * @param abschluss, graduation
     * @return extra points that improve the grade of the applicant
     */
    private double getExtraPoints(String abschluss) {
        double extra = 0;
        if (abschluss.equals("Abitur")) {
            extra = 0.0;
        } else if (abschluss.equals("Ausbildung")) {
            extra = 0.1;
        } else if (abschluss.equals("Bachelor")) {
            extra = 0.2;
        } else if (abschluss.equals("Master")) {
            extra = 0.3;
        } else if (abschluss.equals("Diplom")) {
            extra = 0.4;
        }
        return extra;
    }

}
