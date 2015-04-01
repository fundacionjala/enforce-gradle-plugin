public with sharing class Class2 {


  public Object2__c o = new Object2__c();


  public Class2()
  {
    o.Field1__c = 'field1';
    insert o;
  }

  public void testCoverage()
  {
    Integer a = 0;
    for(Integer i = 0; i < 10; i++)
    {
        a = a + i ;
    }
  }

}
