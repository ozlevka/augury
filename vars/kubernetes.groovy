
def call() {
    def podYaml = libraryResource "org/ozlevka/defaultPod.yaml"
    
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            stage("Run shell hello") {
                container("ubuntu-test") {
                    sh "echo Hello container"
                }
            }
        }
    }
}