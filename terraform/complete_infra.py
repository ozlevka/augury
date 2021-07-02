import os
import subprocess


current_ip = os.getenv("CURRENT_IP")


def find_k8s_public():
    out = subprocess.check_output("doctl compute firewall list", shell=True).decode("ascii")
    for o in out.split('\n'):
        if "k8s-public-access" in o:
            return o.split()[0]

    return None

def run_final_scripts(k8s_public_id):
    subprocess.check_output(f"doctl compute firewall add-rules {k8s_public_id} --inbound-rules protocol:tcp,ports:1-65535,address:10.133.0.0/24,address:{current_ip}", shell=True)
    

def main():
    k8s_public_id = find_k8s_public()
    if not k8s_public_id is None:
        run_final_scripts(k8s_public_id)



if __name__ == "__main__":
    main()