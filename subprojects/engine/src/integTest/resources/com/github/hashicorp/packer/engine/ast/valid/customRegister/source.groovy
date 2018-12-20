import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

// declaration
@AutoImplement
@CompileStatic
abstract class CustomRegisterTest implements InterpolableObject<CustomRegisterTest> {
  @Input
  @Default({ 1L })
  abstract InterpolableLong getSingleField()

  static final void register(Engine e) {
    // do some stuff
    new Random().nextInt()
  }
}
