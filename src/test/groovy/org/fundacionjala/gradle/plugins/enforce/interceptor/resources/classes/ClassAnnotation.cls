@deprecated
public with sharing class ClassAnnotation {

  public Object1__c o = new Object1__c();
  
  public ClassAnnotation()
  {
    o.Field1__c = 'field1';
    insert o;
  }
  @Deprecated
  public Method2()
  {

    o.Field1__c = 'field1';
    insert o;

  }

}
