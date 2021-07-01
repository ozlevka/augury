
def dockerTag = ""
def commitId = ""

def call() {
    def podYaml = libraryResource "org/ozlevka/defaultPod.yaml"
    makeProperties()
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            stage("Get version") {
                git url: "https://github.com/ozlevka/augury.git", branch: params.BRANCH_NAME
                commitId = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
            }

            dockerTag = "${commitId}-${env.BUILD_NUMBER}"

            stage("Build and push container") {
                container("docker") {
                    sh """
                        set -e
                        cd project
                        echo \$GITHUB_TOKEN | docker login ghcr.io/ozlevka -u ozlevka --password-stdin
                        docker build -t "ghcr.io/ozlevka/augury-test:${dockerTag}" .
                        docker push ghcr.io/ozlevka/augury-test:${dockerTag}
                    """
                }
            }
        }
    }
    deployAndTest()
}

def makeProperties() {
    properties([
        parameters([
            string(name: 'BRANCH_NAME', description: 'Branch name to build', )
        ]),
        [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']]
    ])
}

def prepareTestPodYaml() {
    def podYaml = libraryResource "org/ozlevka/testPod.yaml"
    def object = readYaml text: podYaml
    
    for (container in object['spec']['containers']) {
        if (container['name'] == 'application') {
            container['image'] = "ghcr.io/ozlevka/augury-test:${dockerTag}"
            break
        }
    }

    return writeYaml(data: object, returnText: true)
}

def deployAndTest() {    
    podTemplate(yaml: prepareTestPodYaml()) {
        node(POD_LABEL) {
            stage("Test application") {
                container("test") {
                    sh """
                        curl localhost:8080/test
                    """
                }
            }
        }
    }
}

