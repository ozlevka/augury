package org.ozlevka


class Test {

    def helloA(steps) {
        steps.stage("Hello A") {
            steps.echo "Hello A stage hello"
        }
    }
}