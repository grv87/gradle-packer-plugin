package org.fidata.packer.engine.ast.valid.minimal

import org.fidata.packer.engine.AbstractEngine
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.types.InterpolableLong

AbstractEngine engine = new AbstractEngine()
MinimalTest.register engine

Long longValue = new Random().nextLong()
InterpolableLong l = new InterpolableLong.Raw(longValue)
MinimalTest source = new MinimalTest.Impl(engine, l)

AbstractEngine.ObjectMapperFacade objectMapperFacade = engine.getObjectMapperFacade(Mutability.MUTABLE)

String json = objectMapperFacade.writeValueAsString(source)

MinimalTest actual = objectMapperFacade.readValue(json, MinimalTest)
assert MinimalTest.Impl.isInstance(actual)
assert actual.singleField.raw == longValue
