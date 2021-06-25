#!/usr/bin/env groovy

def call() {
    node("master") {
        stage("First") {
            echo "Hello first"
        }

        stage("Second") {
            sh """
                echo "Hello second"
            """
        }
    } 
}