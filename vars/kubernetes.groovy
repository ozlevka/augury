
def call() {
    def podYaml = libraryResource "org/ozlevka/defaultPod.yaml"
    
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            makeProperties()
            stage("Run shell hello") {
                container("ubuntu-test") {
                    sh "echo Hello container"
                }
            }
        }
    }
}

def makeProperties() {
    // properties([
    //     parameters([
    //         string(name: 'SERVICE_NAME', 
    //         ], description: 'The target environment', )
    //     ])
    // ])
    stage("The Test") {
        echo "The branch ${env.BRANCH_NAME}"
    }
}