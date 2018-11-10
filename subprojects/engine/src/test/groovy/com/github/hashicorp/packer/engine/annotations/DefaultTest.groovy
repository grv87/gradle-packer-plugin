package com.github.hashicorp.packer.engine.annotations

import groovy.transform.CompileStatic
import org.junit.Test

@CompileStatic
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
