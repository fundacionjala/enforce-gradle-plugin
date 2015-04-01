public with sharing class ClassContent {

  public Object1__c o = new Object1__c();
  
  public Class1()
  {
    
    o.Field1__c = 'field1';
    insert o; 
    
  }
}
