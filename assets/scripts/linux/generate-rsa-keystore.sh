#/bin/sh

KEYSTORE_NAME=boku.jks
KEYSTORE_PATH=$(dirname $(dirname $(readlink -f $0))/../../../)/security
KEYSTORE_FILE=$KEYSTORE_PATH/$KEYSTORE_NAME

if [ -f $KEYSTORE_FILE ]; then
  echo "$KEYSTORE_FILE exists. Generation not needed"
  exit 0
fi

if ! [ -x "$(command -v pwgen)" ]; then
  echo 'Error: pwgen is not installed.' >&2
  exit 1
fi

KEY_VALIDTITY=3650
GENERATED_STORE_PASS=$(pwgen -s -1 15)
GENERATED_KEY_PASS=$(pwgen -s -1 15)

mkdir -p $KEYSTORE_PATH

$JAVA_HOME/bin/keytool -genkeypair -alias boku-key -keyalg RSA \
  -keysize 2048 -validity $KEY_VALIDTITY \
  -dname "CN=boku,O=boonlogic,L=Singapore,C=SG" \
  -keystore $KEYSTORE_FILE -storepass $GENERATED_STORE_PASS -keypass $GENERATED_KEY_PASS

echo "keystore_password=$GENERATED_STORE_PASS" > $KEYSTORE_PATH/generated_password.log
echo "key_password=$GENERATED_KEY_PASS" >> $KEYSTORE_PATH/generated_password.log
