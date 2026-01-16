package com.soccerfees.micronaut

import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .args(*args)
            .packages("com.soccerfees.micronaut")
            .start()
    }
}
