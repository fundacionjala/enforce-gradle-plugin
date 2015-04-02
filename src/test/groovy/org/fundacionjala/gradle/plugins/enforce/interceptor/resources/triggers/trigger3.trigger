
/**
 * This trigger will perform a check on the AutoGenDataEndFlag object.
 *
 * @author Noe Castillo
 */
trigger AutoGenDataEndFlag on AutoGenDataEndFlag__c (before update, after update)
{
    AutoGenDataEndFlagTrigger.Instance.ExecuteTrigger();
}