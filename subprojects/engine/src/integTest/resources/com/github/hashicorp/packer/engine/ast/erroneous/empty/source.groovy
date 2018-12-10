import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.InterpolableObject
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
abstract class Empty implements InterpolableObject<Empty> {
}