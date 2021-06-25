#!/usr/bin/env groovy

import org.ozlevka.Test

def call() {
        stage("First") {
            echo "Hello first"
        }

        stage("Second") {
            sh """
                echo "Hello second"
            """
        }
}