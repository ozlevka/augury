

def call(Map selectorParams) {
    node("master") {
        echo "Current branch is ${selectorParams.branch}"
        if(selectorParams.branch != "pipeline") {
            build job: 'user-build', params: [
                string(name:"BRANCH_NAME", value: selectorParams.branch)
            ]
        }
    }
}