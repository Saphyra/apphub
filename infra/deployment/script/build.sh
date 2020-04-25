mvn -T 16 clean install
rc=$?
if [[ "$rc" -ne 0 ]]; then
  exit 1
fi
