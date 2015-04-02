public with sharing class Class1 {

  public Object1__c o = new Object1__c();
  public Object1__c other = new Object1__c();
  public Object1__c demo777 = new Object1__c();

  public Class1()
  {
    
    o.Field1__c = 'field1';
    insert o; 
    
  }

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
    /*
    This is a comment
    This is a comment
    This is a comment
    */
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





      /*
      This is another comment
      This is another comment
      This is another comment
      */
  public void compare(Integer num)
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

 public void methodInline(Integer num) { other.Field1__c = 'field1'; }
}
