ansible all -i hosts -m "shell" -a "sudo yum install subscription-manager -y " -b
ansible all -i hosts -m "shell" -a "subscription-manager register --username='mlacours@redhat.com' --password='Or@nge99' " -b
ansible all -i hosts -m "shell" -a "subscription-manager attach --pool=8a85f98c60c2c2b40160c32447481b48 " -b
ansible all -i hosts -m "shell" -a 'subscription-manager repos --disable="*"' -b
ansible all -i hosts -m "shell" -a 'subscription-manager repos --enable="rhel-7-server-rpms" --enable="rhel-7-server-extras-rpms" --enable="rhel-7-server-ose-3.7-rpms" --enable="rhel-7-fast-datapath-rpms"' -b
ansible all -i hosts -m "shell" -a 'yum install wget git net-tools bind-utils yum-utils iptables-services bridge-utils bash-completion kexec-tools sos psacct -y' -b
ansible all -i hosts -m "shell" -a 'yum update -y' -b

