#!/bin/bash
set -a

HOST_INFO_FILE=relatorios/$BENCHMARK_ID/host-info.md
echo -e '# Informações do Host\n\n' > $HOST_INFO_FILE

function dump() {
    echo '```bash' >> $HOST_INFO_FILE
    echo $@ >> $HOST_INFO_FILE
    echo '```' >> $HOST_INFO_FILE
    echo '```' >> $HOST_INFO_FILE
    $@ >> $HOST_INFO_FILE 2>>$HOST_INFO_FILE
    echo '```' >> $HOST_INFO_FILE
    echo '---' >> $HOST_INFO_FILE
    echo >> $HOST_INFO_FILE
    echo >> $HOST_INFO_FILE
}

 dump date
 dump uname -a
 dump arch
 dump cat /etc/os-release
 dump dd if=/dev/zero of=/tmp/test1.img bs=1G count=1 oflag=dsync     #storage write throughput
 dump dd if=/dev/zero of=/tmp/test2.img bs=512 count=1000 oflag=dsync #storage write latency
# sudo /sbin/sysctl -w vm.drop_caches=3                                     #drop caches
# dump dd if=/tmp/test1.img of=/dev/null bs=1M count=1024              #storage read throughput
 rm -rvf /tmp/test{1,2}.img                                                    #remove temp files
 dump lscpu
 dump docker --version
 dump docker info
