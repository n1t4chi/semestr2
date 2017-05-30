#/bin/bash
#zad3.sh "https://www.random.org/integers/?num=100&min=1&max=100&col=5&base=10&format=html&rnd=new" 10
declare -i it=0
mkdir -p "webtrace"
cd "webtrace"
git init -q
until [ ]; do
	if [ -e "current" ]; then
		cat ./current > ./previous
	fi
	links -dump $1 > "current"
	if [ -e "previous" ]; then
		dif=`diff -q current previous`;
		if [ -n "$dif" ]; then
			git commit -q -am "ver $it"
			#diff -y --suppress-common-lines current previous
			git --no-pager diff HEAD^ HEAD "current"
		fi	
	fi
	it=$it+1	
	sleep $2
done
