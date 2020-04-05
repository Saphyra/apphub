mvn -T 6 clean package
rc=$?
if [[ "$rc" -ne 0 ]]; then
  exit 1
fi
