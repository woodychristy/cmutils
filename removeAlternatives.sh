alternatives --display hadoop-conf|grep ^/etc/hadoop  |awk '{system ("alternatives --remove hadoop-conf " $1 ) }';

