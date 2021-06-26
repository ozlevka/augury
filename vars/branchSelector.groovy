

def call() {
    node("master") {
        echo "Current branch is ${env.BRANCH_NAME}"
        if(env.BRANCH_NAME != "pipeline") {
            build job: 'user-build', params: [
                string(name:"BRANCH_NAME", value: env.BRANCH_NAME)
            ]
        }
    }
}