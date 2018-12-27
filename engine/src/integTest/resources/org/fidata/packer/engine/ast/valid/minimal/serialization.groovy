package org.fidata.packer.engine.ast.valid.minimal

import org.fidata.packer.engine.Engine
import org.fidata.packer.engine.Mutability
import org.fidata.packer.engine.types.InterpolableLong

Engine engine = new Engine()
MinimalTest.register engine

Long longValue = new Random().nextLong()
InterpolableLong l = new InterpolableLong.Raw(longValue)
MinimalTest source = new MinimalTest.Impl(engine, l)

Engine.ObjectMapperFacade objectMapperFacade = engine.getObjectMapperFacade(Mutability.MUTABLE)

String json = objectMapperFacade.writeValueAsString(source)

MinimalTest actual = objectMapperFacade.readValue(json, MinimalTest)
assert MinimalTest.Impl.isInstance(actual)
assert actual.singleField.raw == longValue
