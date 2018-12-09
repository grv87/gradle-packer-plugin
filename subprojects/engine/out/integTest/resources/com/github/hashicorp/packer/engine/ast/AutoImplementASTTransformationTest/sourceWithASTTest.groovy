package com.github.hashicorp.packer.engine.ast

// import com.fasterxml.jackson.annotation.JsonValue
import com.github.hashicorp.packer.engine.annotations.AutoImplement
import com.github.hashicorp.packer.engine.annotations.Default
import com.github.hashicorp.packer.engine.annotations.IgnoreIf
import com.github.hashicorp.packer.engine.annotations.PostProcess
import com.github.hashicorp.packer.engine.types.InterpolableBoolean
// import com.github.hashicorp.packer.engine.types.InterpolableEnum
import com.github.hashicorp.packer.engine.types.InterpolableLong
import com.github.hashicorp.packer.engine.types.InterpolableObject
import com.github.hashicorp.packer.engine.types.InterpolableString
import com.google.common.base.Charsets
import com.google.common.io.Resources
import groovy.inspect.swingui.AstNodeToScriptAdapter
import groovy.transform.ASTTest
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.builder.AstAssert
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.tools.WideningCategories
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.SourceUnit

// import groovy.transform.InheritConstructors

import org.gradle.api.tasks.Input

@ASTTest(phase = CompilePhase.INSTRUCTION_SELECTION, value = {
  /*List<ASTNode> expected = new AstBuilder().buildFromString(Resources.toString(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/expectedAST.groovy'), Charsets.UTF_8))
  println(((ClassNode)node).innerClasses)*/
  CompilerConfiguration compilerConfiguration = new CompilerConfiguration(/*TODO*/)
  compilerConfiguration.parameters = true
  compilerConfiguration.debug = true
  ErrorCollector errorCollector = new ErrorCollector(compilerConfiguration)
  GroovyClassLoader groovyClassLoader = new GroovyClassLoader()
  // groovyClassLoader.de
  // ModuleNode expected = new ModuleNode(new SourceUnit(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/expectedAST.groovy'), compilerConfiguration, new GroovyClassLoader(), errorCollector)) // .getAST()
  // println errorCollector.errors
  // println expected.classes TODO

  expected = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, Resources.toString(Resources.getResource('com/github/hashicorp/packer/engine/ast/AutoImplementASTTransformationTest/expectedAST.groovy'), Charsets.UTF_8))
  AstAssert.assertSyntaxTree(expected.findAll { ASTNode e -> InnerClassNode.isInstance(e) }, node.module.classes.findAll { s -> InnerClassNode.isInstance(s) })
  expectedClasses = expected.findAll { ASTNode e -> ClassNode.isInstance(e) && !InnerClassNode.isInstance(e) }
  actualClasses = node.module.classes.findAll { ASTNode e -> ClassNode.isInstance(e) && !InnerClassNode.isInstance(e) }
  actualClasses*.annotations*.removeAll { AnnotationNode a -> WideningCategories.implementsInterfaceOrSubclassOf(a.classNode, ClassHelper.make(ASTTest)) }
  AstAssert.assertSyntaxTree(expectedClasses, actualClasses)
})
@AutoImplement
@CompileStatic
interface BlockDevice extends InterpolableObject<BlockDevice> {
  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableBoolean getDeleteOnTermination()

  @Input
  InterpolableString getDeviceName()

  @Input
  @Default({ Boolean.FALSE })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated || snapshotId.interpolated })
  InterpolableBoolean getEncrypted()

  @Input
  @IgnoreIf({ volumeType.interpolated != /*VolumeType.IO1*/ 'io1' /* TODO: Bug in either Packer or AWS documentation. This should be supported for gp2 volumes too */ })
  InterpolableLong getIops()

  @Input
  @Default({ Boolean.FALSE })
  InterpolableBoolean getNoDevice()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableString getSnapshotId()

  @Input
  @IgnoreIf({ noDevice.interpolated })
  @PostProcess({ String interpolated -> interpolated.startsWith('ephemeral') ? null : interpolated })
  InterpolableString getVirtualName()

  @Input
  @Default({ /*VolumeType.STANDARD*/ 'standard' })
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  /*InterpolableVolumeType*/ InterpolableString getVolumeType()

  @Input
  // @Default() TODO: If you're creating the volume from a snapshot and don't specify a volume size, the default is the snapshot size.
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  @PostProcess({ Long interpolated -> interpolated > 0 ? interpolated : null})
  InterpolableLong getVolumeSize()

  @Input
  @IgnoreIf({ noDevice.interpolated || virtualName.interpolated })
  InterpolableString getKmsKeyId()

  /*enum VolumeType {
    STANDARD,
    IO1,
    GP2,
    SC1,
    ST1

    @JsonValue
    @Override
    String toString() {
      this.name().toLowerCase()
    }
  }

  interface InterpolableVolumeType extends InterpolableEnum<VolumeType, InterpolableVolumeType> {
    @InheritConstructors
    final class ImmutableRaw extends InterpolableEnum.ImmutableRaw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Raw extends InterpolableEnum.Raw<VolumeType, InterpolableVolumeType, Interpolated, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class Interpolated extends InterpolableEnum.Interpolated<VolumeType, InterpolableVolumeType, AlreadyInterpolated> implements InterpolableVolumeType { }

    @InheritConstructors
    final class AlreadyInterpolated extends InterpolableEnum.AlreadyInterpolated<VolumeType, InterpolableVolumeType> implements InterpolableVolumeType { }
  }*/
}
