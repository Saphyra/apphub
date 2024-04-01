while IFS="" read -r service_data || [ -n "$service_data" ]
do
  IFS=' ' read -r -a array <<< "$service_data"
  port=${array[3]}
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${port}")"

  if [[ $port != "null" ]]; then
    ./infra/deployment/script/release_port.sh "$TRIMMED" &
  fi
done < ./infra/deployment/service/service_list

./infra/deployment/script/release_port.sh 8072 #Shutting down the integration server