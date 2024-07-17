*** Variables ***
${EXAMPLE_VARIABLE}    InitialValue

*** Test Cases ***
Example Test Case
    Log    Starting the test case
    Set Variable    ${EXAMPLE_VARIABLE}    UpdatedValue
    Log    EXAMPLE_VARIABLE=${EXAMPLE_VARIABLE}

    # Output in JSON format
    ${complex_var}=    Create Dictionary    key1=val1    key2=val2
    ${json_output}=    Evaluate    json.dumps($complex_var)    modules=json
    Log    COMPLEX_VARIABLE=${json_output}
