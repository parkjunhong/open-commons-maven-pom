#!/bin/bash

usage(){
	echo
	echo ">>> CALLED BY [[ $1 ]]"
	echo
	echo "[Usage]"
	echo
	echo "./stop.sh -c <configuration>"
	echo
	echo "[Option]"
	echo " -c, --config: (optional) 설정파일 경로. 기본값: service.propertites"
	echo " -h, --help  : 도움말"
}

CONFIG_FILE="service.properties"
## 파라미터 읽기
while [ "$1" != "" ]; do
	case $1 in
		-c | --config)
			shift
			CONFIG_FILE=$1
			;;
		-h | --help) 
			usage "--help"
			exit 0
			;;
		*)
			usage "Invalid option. option: $1"
			exit 1
			;;
	esac
	shift
done

## 설정파일이 전달되지 않은 경우 종료
if [ ! -f "$CONFIG_FILE" ];
then
	echo "[Configurations] $CONFIG_FILE NOT FOUND!"
	
	exit 2
fi

# Pattern: ${...}
REGEX_PROP_REF="\\\$\{([^\}]+)\}"
GLOBAL_REMATCH=""

# $1 {string} string
# $2 {string} regular expression
global_rematch() {
	GLOBAL_REMATCH=""
	local str="$1" regex="$2"
	
	while [[ $str =~ $regex ]];
	do
		if [ -z "$GLOBAL_REMATCH" ];
		then
			GLOBAL_REMATCH="${BASH_REMATCH[1]}"
		else
			GLOBAL_REMATCH="$GLOBAL_REMATCH ${BASH_REMATCH[1]}"
		fi
		str=${str#*"${BASH_REMATCH[1]}"}
	done
}

## 설정파일 읽기
# $1 {string} file
# $2 {string} prop_name
# $3 {any} default_value
prop(){
	local property=""
	# 1. profile 에 기반한 설정부터 조회 
	if [ ! -z "$PROFILE" ];
	then
		local property=$(grep -v -e "^#" ${1} | grep -e "^${2}\.$PROFILE=" | cut -d"=" -f2-)
	fi
	
	# 2. profile에 기반한 설정이 없는 경우 기본 설정조회
	if [ -z "$property" ];
	then
		local property=$(grep -v -e "^#" ${1} | grep -e "^${2}=" | cut -d"=" -f2-)
		
		# 3. 기본설정이 없고 함수 호출시 기본값이 있는 경우
		if [ -z "$property" ] && [ ! -z "$3" ];
		then
			echo $3
		else
			echo $property
		fi
	else
		echo $property
	fi
}

# $1 {string} absolute file path.
# $2 {string} prop_name
# $3 {any} default_value
read_prop(){
	local property=$(prop "$1" "$2" "$3")
	global_rematch "$property" "$REGEX_PROP_REF"
	
	if [ -z "$GLOBAL_REMATCH" ];
	then
		echo $property
	else
		local references=($(echo $GLOBAL_REMATCH))
		for ref in "${references[@]}";
		do
			local ref_value=$(read_prop "$1" "$ref")
			if [ ! -z "$ref_value" ];
			then
				property=${property//\$\{$ref\}/$ref_value}
			fi
		done
		echo $property
	fi
}

# 설치  디렉토리로 이동
DIR=$(read_prop "$CONFIG_FILE" "install.dir")
cd $DIR

# Build 프로파일 읽기
if [ -f "$DIR/.profile" ];
then
	PROFILE=$(cat ./.profile)
fi

# 프로그램 정보
APP_NAME=$(read_prop "$CONFIG_FILE" "application.name")
PS_NAME="["${APP_NAME:0:1}"]"${APP_NAME:1}
# PID 선택 관련 'grep' 기반 필터
CUSTOMIZE_FILTER="grep java" 

#  프로세스 조회 명령어
PS_CMD="ps -aef | grep $PS_NAME | ${CUSTOMIZE_FILTER} | awk '{print \$2}'"

# $1: target PID
isAlive(){
	pids=$(eval $PS_CMD)
	
	tpid=""
	for pid in $pids;
	do
		if [ $1 -eq $pid ];
		then
			tpid=$1
			break
		fi
	done

	if [ $tpid ];
	then
		return 1
	else
		return 0
	fi
}

PIDS=$(eval $PS_CMD)
# kill -l => 15:  SIGTERM
# '-s SIGTERM'과 동일
SIGNAL=-15
for PID in $PIDS
do
	kill $SIGNAL $PID
done

for PID in $PIDS
do
	isAlive $PID
	IS_ALIVE=$?

	while [ $IS_ALIVE == 1 ]
	do
		isAlive $PID
		IS_ALIVE=$?
		sleep 1
	done

	echo "Process is killed. PID: $PID"
done

exit 0
