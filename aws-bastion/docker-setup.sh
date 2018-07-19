ansible nodes -i hosts -m yum -a "name=docker state=present"
cat <<EOF >docker-storage-setup
DEVS=/dev/xvdb
VG=docker-vg
EOF
ansible nodes -i hosts -m copy -a "src=docker-storage-setup dest=/etc/sysconfig/docker-storage-setup force=yes"
ansible nodes -i hosts -m shell -a "rm -rf /var/lib/docker" -b
ansible nodes -i hosts -a "/usr/bin/docker-storage-setup" -b
ansible nodes -i hosts -m service -a "name=docker enabled=yes state=started" -b

