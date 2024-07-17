*** Settings ***
Library       RPA.JSON
Library       RPA.Email.ImapSmtp    smtp_server=smtp-mail.outlook.com    smtp_port=587    imap_server=outlook.office365.com
Library       RPA.JSON
Library       OperatingSystem
Library       Collections
Task Setup    Authorize    account=ba_bpm-rpa@outlook.de    password=Passwort12345


*** Variables ***
${PROCESS_INSTANCE_ID}    ${PROCESS_INSTANCE_ID}
${PATH}    ${CURDIR}/../../bewerbung/alt
${FILE}
${MAIL}

*** Tasks ***
Get Application File
    @{files}=    List Files In Directory    ${PATH}   ${PROCESS_INSTANCE_ID}.json
    ${tmp} =     Get From List   ${files}    0
    Set Global Variable    ${FILE}    ${tmp}
    
Get Mail Adress
    ${json_object}=    Load JSON from file    ${PATH}\\${FILE}
    ${mail}=    Get value from JSON    ${json_object}    $.mail
    Set Global Variable    ${MAIL}    ${mail}
Sending Email
    Send Message    sender=ba_bpm-rpa@outlook.de    recipients=${MAIL} 
    ...        subject=Annahme der Bewerbung
    ...        body=Sehr geehrter Bewerber, mit Freuden teilen wir Ihnen mit, dass Ihre Bewerbung angenommen wurde. Mit freundlichen Grüßen, Ihr Institut