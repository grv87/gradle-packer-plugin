import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.types.base.InterpolableObject
import groovy.transform.CompileStatic

@AutoImplement
@CompileStatic
class NonAbstractClass implements InterpolableObject<NonAbstractClass> { }
