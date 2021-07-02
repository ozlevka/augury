
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

    if (params.BRANCH_NAME == "release") {
        deployToStaging()
    }
}

def deployToStaging() {
    def podYaml = libraryResource "org/ozlevka/deployToStaging.yaml"
    podTemplate(yaml: podYaml) {
        node(POD_LABEL) {
            stage("Prepare yaml") {
                prepareDeployYaml()
            }
            stage("Deploy pod to staging") {
                container("deploy") {
                    sh """
                        kubectl apply -f ./deploy/deployment.yaml
                    """
                }
            }
            
            stage("Wait for deployment ready") {
                container("deploy") {
                    int amount = 0
                    int count = 1
                    while(amount != 3 || count <= 10) {
                        def a = sh script: """#!/bin/bash
                            kubectl -n staging get deployment | grep augury | awk '{print $4}'
                        """, returnStdout: true
                        amount = Integer.parseInt(a.toString())
                        if (amount == 3) {
                            break;
                        }
                        sleep 20
                        count += 1
                    }
                }
            }

            def nodeIp = ""
            stage("Find node ip") {
                container("deploy") {
                    nodeIp = sh script: """#!/bin/bash
                        kubectl get node -o wide | grep worker | head -1 | awk '{print $6}'
                    """, returnStdout: true
                }
            }

            stage("Test deployment") {
                testApplication("${nodeIp}:30808")
            }
        }
    }
}


def prepareDeployYaml() {
    def deployData = readYaml file: "./deploy/deployment.yaml"
    deployData['spec']['template']['spec']['containers'][0]['image'] = "ghcr.io/ozlevka/augury-test:${dockerTag}"
    writeYaml data: deployData, file: "./deploy/deployment.yaml"
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

def testApplication(address) {
    stage("Test application") {
        container("test") {
            sh """
                STATUSCODE=\$(curl --silent --output /dev/stderr --write-out "%{http_code}" http://${address}/test)

                if test \$STATUSCODE -ne 200; then
                    exit 1
                fi
            """
        }
    }
}

def deployAndTest() {    
    podTemplate(yaml: prepareTestPodYaml()) {
        node(POD_LABEL) {
            testApplication("localhost:8080")
        }
    }
}

