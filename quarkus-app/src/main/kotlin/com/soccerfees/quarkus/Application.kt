package com.soccerfees.quarkus

import io.quarkus.runtime.Quarkus

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Quarkus.run(*args)
    }
}
