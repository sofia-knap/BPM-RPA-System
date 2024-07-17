package de.hawhh.knap.bpm_rpa.elementRepresentation;

import java.util.List;

import org.activiti.bpmn.model.FormValue;

/**
 * Represents the properties of a form stored in the Activiti engine database
 * 
 * @author Sofia Knap
 * @version 1.0
 */
public class FormPropertyRepresentation {

    private String id;
    private String name;
    private String variable;
    private String expression;
    private boolean required;
    private List<FormValue> formValues;

    public FormPropertyRepresentation(String id, String name, String variable, String expression, boolean required,
            List<FormValue> formValues) {
        this.id = id;
        this.name = name;
        this.variable = variable;
        this.expression = expression;
        this.required = required;
        this.formValues = formValues;
    }

    public List<FormValue> getFormValues() {
        return formValues;
    }

    public String getName() {
        return name;
    }

    public String getVariable() {
        return variable;
    }

    public boolean getRequired() {
        return required;
    }

    public String getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }
}
