
def call() {
    podTemplate(yaml: libraryResource "org/ozlevka/defaultPod.yaml") {
    node(POD_LABEL) {
        stage("Run shell hello") {
            container("ubuntu-test") {
                sh "echo Hello container"
            }
        }
    }
  }
}