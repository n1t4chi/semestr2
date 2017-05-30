#/bin/bash
declare -i it=0;
list="";
for fil in `find $1 -type f` 
do
	list="$list`md5sum $fil`\n";
done;
list=`echo -e "$list" | tr -s ' '`
output=""
#echo -e $list | cut -d' ' -f 1 | sort | uniq -d
for md5 in `echo -e "$list" | cut -d' ' -f 1 | sort | uniq -d` ; do
	innerlist=`echo -e "$list" | grep $md5 | cut -d' ' -f 2`
	declare -i it1=1;
	declare -i list_len=`echo -e "$innerlist" | wc -l`;
	#echo -e "checking md5:$md5\nlist length: $list_len\ninner list:\n$innerlist\n\n"
	while [ $it1 -le $list_len ]; do
		declare -i it2=$it1+1;
		fil1=`echo -e "$innerlist" | sed "${it1}q;d"`;
		#echo "[$it1]FILE1: $fil1"
		fil_output="";
		while [ $it2 -le $list_len ]; do
			#echo -n "$fil1 and $fil2 are "
			fil2=`echo -e "$innerlist" | sed "${it2}q;d"`;
			#echo "[$it1][$it2]FILE2: $fil2"
			diff=`diff -s $fil1 $fil2`;
			#echo -e "\ndiff -s $fil1 $fil2\n"
			if [ -n "$diff" ]; then
				fil_output="$fil_output\t$fil2"

				#echo -e "\n\nBEFORE deleting $fil2\n $innerlist "
				innerlist=`echo -e "$innerlist" | sed "${it2}d"`

				#echo -e "AFTER\n $innerlist \n"
				it2=$it2-1;
	 			list_len=$list_len-1
				#echo "the same ";
			#else
				#echo "different";
			fi
			it2=$it2+1;
		done;
		if [ -n "$fil_output" ];then
			output="$output`du -b $fil1`\t$fil1$fil_output\n"
		fi
		it1=$it1+1;	
	done;
done;
echo -e $output | sort -g -r |  cut -f 2-
#echo "du echo "$output" | cut -d' ' -f 1 | tr '\n' ' '"


<<comment1
		while [ $it2 -le $list_len ]; do
			#echo -n "$fil1 and $fil2 are "
			fil2=`echo -e "$innerlist" | sed "${it2}q;d"`;
			#echo "[$it1][$it2]FILE2: $fil2"
			diff=`diff -s $fil1 $fil2`;
			#echo -e "\ndiff -s $fil1 $fil2\n"
			if [ -n "$diff" ]
			then
				output="$output$fil1 $fil2\n"
				#echo "the same ";
			#else
				#echo "different";
			fi
			it2=$it2+1;
		done;
comment1
