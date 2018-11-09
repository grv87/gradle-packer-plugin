package com.github.hashicorp.packer.engine.annotations

import org.junit.Test

class DefaultTest {
  @Test
  void testAnnotation() {
    /*assertScript '''\
      import groovy.transform.CompileStatic
      import com.github.hashicorp.packer.engine.types.InterpolableString
      import Default
      
      @CompileStatic
      class Person {
        @Default(value = 'first')
        InterpolableString firstName
      }

      Person person = new Person()
      person.interpolatedFirstName == 'first'
    '''*/
  }

}
