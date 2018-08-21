package com.github.hashicorp.packer.template.common.annotations

import org.junit.Test
class DefaultTest {
  @Test
  void testAnnotation() {
    assertScript'''\
      import groovy.transform.CompileStatic
      import com.github.hashicorp.packer.common.types.InterpolableString
      import com.github.hashicorp.packer.common.annotations.Default
      
      @CompileStatic
      class Person {
        @Default(value = 'first')
        InterpolableString firstName
      }
      
      Person person = new Person()
      person.interpolatedFirstName == 'first'
    '''
  }

}
