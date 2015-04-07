trigger Trigger1 on Object1__c (before insert) {

    // Delegate the trigger work to an Apex class that encapsulates behavior relating to the Account object
    if(Trigger.isInsert){
    }
}