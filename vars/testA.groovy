#!/usr/bin/env groovy
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