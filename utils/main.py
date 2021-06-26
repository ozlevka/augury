import os
import sys
from kubernetes import client, config
import yaml
import argparse

deploy_container = "ghcr.io/ozlevka/augury-test"

def applyArguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("--tag", required=True, help="Tag of docker image to deploy into pod")
    parser.add_argument("--delete", action="store_true", help="Used as delete flag")
    parser.add_argument("--apply", action="store_true", help="Create pod in cluster")

    return parser.parse_args()


def deployTest(tag):
    with open('./deploy.yaml', mode="r") as file:
        deploy = yaml.safe_load(file)

    deploy["spec"]["containers"][0]["image"] = f"{deploy_container}:{tag}"

    k8s = client.CoreV1Api()
    k8s.create_namespaced_pod(namespace="default", body=deploy)

def deleteTest(tag):
    k8s = client.CoreV1Api()
    pods = k8s.list_namespaced_pod("default")

    for pod in pods.items:
        for container in pod.spec.containers:
            if container.image == f"{deploy_container}:{tag}":
                k8s.delete_namespaced_pod(pod.metadata.name, namespace="default")

def main(dev):
    args = applyArguments()
    
    if dev:
        config.load_kube_config()
    else:
        config.load_incluster_config()
    
    if args.apply:
        deployTest(args.tag)
    
    if args.delete:
        deleteTest(args.tag)

if __name__ == "__main__":
    dev = bool(os.getenv("DEV_ENV", "False"))
    main(dev) 