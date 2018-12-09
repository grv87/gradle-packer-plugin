package groovy.test

import org.codehaus.groovy.control.CompilerConfiguration

class GroovyAssertExt extends GroovyAssert {
  /**
   * Asserts that the script runs without any exceptions
   *
   * @param script the script that should pass without any exception thrown
   * @param compilerConfiguration Compiler configuration to use
   */
  static void assertScript(final String script, CompilerConfiguration compilerConfiguration) throws Exception {
    GroovyShell shell = new GroovyShell(compilerConfiguration)
    shell.evaluate(script, genericScriptName())
  }
}
