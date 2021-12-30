package io.github.gsteckman.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class HelloWorldLambda : RequestHandler<Map<String, String>, String> {
    override fun handleRequest(input: Map<String, String>?, context: Context?): String {
        context?.apply {
            logger.log("Hello, world!")
        }

        return "Hello, world!"
    }
}