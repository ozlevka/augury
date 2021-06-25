
def call() {
    def podYaml = libraryResource "org/ozlevka/defaultPod.yaml"
    makeProperties()
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            stage("Run shell hello") {
                container("ubuntu-test") {
                    sh "echo ${params.BRANCH_NAME}"
                }
            }
        }
    }
}

def makeProperties() {
    properties([
        parameters([
            string(name: 'BRANCH_NAME', description: 'Branch name to build', )
        ])
    ])
}