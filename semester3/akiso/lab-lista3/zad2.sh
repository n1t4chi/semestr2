#/bin/bash

function shorten () {
	str="$1";
	declare -i s="$1";
	siz="B";
	if [ $s -ge 1024 ]; then
		siz="KB";
		str="$str/1024";
		if [ $s -ge $((1024*1024)) ]; then
			siz="MB";
			str="$str/1024";
			if [ $s -ge $((1024*1024*1024)) ]; then
				siz="GB";
				str="$str/1024";
			fi
		fi
	fi	
	echo "`echo "scale=2; $str" | bc -l`${siz}";
}

output="PID\tPPID\tState\tnice\tThreads\tVSize\t\tRSS\t\tName\n"
for pid in `ls /proc | grep [0-9]`;
do
	if [ -e "/proc/$pid/stat" ]; then
		stat=`cat "/proc/$pid/stat"`
		name=`echo "$stat" | cut -d' ' -f 2`
		state=`echo "$stat" | cut -d' ' -f 3`
		ppid=`echo "$stat" | cut -d' ' -f 4`
		tty=`echo "$stat" | cut -d' ' -f 6`
		nice=`echo "$stat" | cut -d' ' -f 19`
		vsize=`echo "$stat" | cut -d' ' -f 23`
		vsize=`shorten $vsize`
		rss=`echo "$stat" | cut -d' ' -f 24`
		rss=`shorten $rss`
		if [ `echo "$vsize" | wc -m` -le "8" ]; then
			vsize="$vsize\t"
		fi
		if [ `echo "$rss" | wc -m` -le "8" ]; then
			rss="$rss\t"
		fi
		threads=`echo "$stat" | cut -d' ' -f 20`
		output="${output}${pid}\t${ppid}\t${state}\t${nice}\t${threads}\t${vsize}\t${rss}\t${name}\n"	
	fi

done
echo -e "$output" | sort -g

