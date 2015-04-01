<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <rules>
        <fullName>ObjRule2</fullName>
        <active>false</active>
        <formula>false</formula>
        <criteriaItems>
            <field>User.CompanyName</field>
            <operation>equals</operation>
            <value>something</value>
        </criteriaItems>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
