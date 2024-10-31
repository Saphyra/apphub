ENV=$1

if [[ $ENV == "production" || $ENV == "preprod" ]]; then
  echo "Generating key"
else
  echo "Env must be 'production' or 'preprod'"
  exit 1
fi

keytool -genkey -alias apphub -storetype JKS -keyalg RSA -keysize 2048 -validity 365 -keystore src/main/resources/apphub-$1.jks