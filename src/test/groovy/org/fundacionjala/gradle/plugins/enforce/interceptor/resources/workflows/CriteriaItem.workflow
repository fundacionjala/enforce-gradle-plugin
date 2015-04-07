<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <rules>
        <fullName>ObjRule1</fullName>
        <active>false</active>
        <criteriaItems>
            <field>User.Phone</field>
            <operation>equals</operation>
            <value>752121332</value>
        </criteriaItems>
        <criteriaItems>
            <field>User.Name</field>
            <operation>equals</operation>
            <value>Joe</value>
        </criteriaItems>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
