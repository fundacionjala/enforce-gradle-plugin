@deprecated
public with sharing class Class1 {
 //hello
  public Object1__c o = new Object1__c();
  public Object1__c other = new Object1__c();
  public Object1__c demos123 = new Object1__c();
  public Class1()
  {
    
    o.Field1__c = 'field1';
    insert o; 
    
  }
 @deprecated
 public void testCoverage(Integer num)
  {
      if(num > 0)
      {
          other.Field1__c = 'field1';
      }
      else
      {
          other.Field1__c = 'field0';
      }


  }
  @deprecated
  public void compareCoverages(Integer num)
  {
        if(num > 0)
        {
            other.Field1__c = 'field10';
        }
        else
        {
            other.Field1__c = 'field0';
        }
  }
}
