#!/usr/bin/env groovy

import org.ozlevka.Test

def call() {
        def test = new Test()

        test.helloA()
        
        stage("First") {
            echo "Hello first"
        }

        stage("Second") {
            sh """
                echo "Hello second"
            """
        }
}