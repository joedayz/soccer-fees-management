package com.soccerfees.micronaut

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import jakarta.inject.Singleton

@Singleton
class JacksonConfig : BeanCreatedEventListener<ObjectMapper> {
    override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
        return event.bean.registerModule(JavaTimeModule())
    }
}
