package io.github.gsteckman.deploy

import software.amazon.awscdk.core.App
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.core.StackProps
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime

class CdkStack {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = App()
            LambdaStack(app, "lambdastack")
            app.synth()
        }
    }
}

class LambdaStack @JvmOverloads constructor(scope: Construct?, id: String?, props: StackProps? = null) :
    Stack(scope, id, props) {

    init {
        val asset = Code.fromAsset("../lambda/build/distributions/lambda-1.0-SNAPSHOT.zip")

        Function.Builder.create(this, "helloLambda")
            .functionName("helloWorld")
            .code(asset)
            .runtime(Runtime.PROVIDED_AL2)
            .memorySize(128)
            .handler(io.github.gsteckman.lambda.HelloWorldLambda::class.qualifiedName!!)
            .build()
    }
}
