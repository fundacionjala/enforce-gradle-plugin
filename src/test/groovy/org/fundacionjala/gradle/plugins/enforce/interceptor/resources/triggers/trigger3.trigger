
/**
 * This trigger will perform a check on the AutoGenDataEndFlag object.
 */
trigger AutoGenDataEndFlag on AutoGenDataEndFlag__c (before update, after update)
{
    AutoGenDataEndFlagTrigger.Instance.ExecuteTrigger();
}