package com.github.hashicorp.packer.engine.ast.valid.minimal

import com.github.hashicorp.packer.engine.Engine
import com.github.hashicorp.packer.engine.Mutability
import com.github.hashicorp.packer.engine.types.InterpolableLong

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
