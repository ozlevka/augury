
def call() {
    def podYaml = libraryResource "org/ozlevka/defaultPod.yaml"
    makeProperties()
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            stage("Get version") {
                git url: "https://github.com/ozlevka/augury.git", branch: params.BRANCH_NAME
            }

            stage("Build and push container") {
                container("docker") {
                    sh """
                        set -e
                        cd project
                        echo $GITHUB_TOKEN | docker login ghcr.io/ozlevka -u ozlevka --password-stdin
                        docker build -t ghcr.io/ozlevka/augury-test:tmp-${env.BUILD_NUMBER}-${env.GIT_COMMIT} .
                        docker push ghcr.io/ozlevka/augury-test:tmp-${env.BUILD_NUMBER}-${env.GIT_COMMIT}
                    """
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