*** Settings ***
Library     RPA.Excel.Application
Library    OperatingSystem
Library    RPA.FileSystem
Library    RPA.Browser.Selenium

*** Tasks ***
RPA task
    Open Browser    https://stackoverflow.com/
    Close Browser