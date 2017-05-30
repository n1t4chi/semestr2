#/bin/bash
function translate_transfer () {
    speedLvl="B";
    speedStr="$1";
    if [ $1 -ge 1024 ]; then
        speedLvl="KiB";
        speedStr="$speedStr/1024";
        if [ $1 -ge $((1024*1024)) ]; then
            speedLvl="MiB";
            speedStr="$speedStr/1024";
            if [ $1 -ge $((1024*1024*1024)) ]; then
                speedLvl="GiB";
                speedStr="$speedStr/1024";
            fi
        fi
    fi   
    echo "`echo "scale=2; $speedStr" | bc -l` ${speedLvl}ps";
}

clear;



curr=`tail -n +3 /proc/net/dev | tr -s ' ' | cut -d' ' -f 2,3,11`;
prev=$curr;

init_upt=`cut -d' ' -f 1 "/proc/uptime"`;
first_run="T";

until [ ];
do
    #uptime
    upt=`cut -d' ' -f 1 "/proc/uptime"`;   
    cupt=$upt; #kopia dla avg speed
   
    #NIC speeds
    declare -i it=1;
    output="";
    while [ $it -le $(echo "$curr" | wc -l) ];
    do
        declare -i up=`echo -n "$curr" | sed "$it"'q;d' | cut -d' ' -f 2` ;
        declare -i dl=`echo -n "$curr" | sed "$it"'q;d' | cut -d' ' -f 3` ;
        if [ $first_run = "T" ]; then
            up_arr[($it-1)]=$up;
            dl_arr[($it-1)]=$dl;
            cupt="$init_upt+1";
        fi
        declare -i up_avg=`echo "scale=0;($up-${up_arr[$it-1]})/($cupt-$init_upt)" | bc -l`;
        declare -i dl_avg=`echo "scale=0;($dl-${dl_arr[$it-1]})/($cupt-$init_upt)" | bc -l`;
        up=$up-`echo -n "$prev" | sed "$it"'q;d' | cut -d' ' -f 2` ;
        dl=$dl-`echo -n "$prev" | sed "$it"'q;d' | cut -d' ' -f 3` ;

        output="$output$(echo -n "$curr" | sed "$it"'q;d' | cut -d' ' -f 1)";    #interface name
        output="$output\tup:`translate_transfer $up`  av:`translate_transfer $up_avg`\e[$it;45Hdl:`translate_transfer $dl`  av:`translate_transfer $dl_avg`\n";

        it=$it+1;
    done
    prev=$curr;
    curr=`tail -n +3 /proc/net/dev | tr -s ' ' | cut -d' ' -f 2,3,11`;
   
    #system uptime
    upt_d=`echo "scale=0;$upt/24/60/60" | bc -l`;
    upt_h=`echo "scale=0;$upt/60/60-$upt_d*24*60" | bc -l`;
    upt_m=`echo "scale=0;$upt/60-$upt_d*24*60-$upt_h*60" | bc -l`;
    upt_s=`echo ";$upt-$upt_d*24*60*60-$upt_h*60*60-$upt_m*60" | bc -l`;
    output="${output}System uptime: $upt_d days $upt_h hours $upt_m minutes and $upt_s seconds\n";
   

    #battery
    output="${output}Battery:` cat /sys/class/power_supply/BAT0/uevent | grep "CAPACITY=" | cut -c23-24 `%\n"


    echo -en "\e[2J \e[0;0H$output";

    first_run="N"
    script_it=$script_it+1
    fupt=`cut -d' ' -f 1 "/proc/uptime"`
    d=`echo "($upt-$fupt)*100" | bc -l `;   
    if [ $(echo "$d < 0" | bc) -eq 1 ]; then
        sleep 0;   
    else
        sleep $d;   
    fi
done
