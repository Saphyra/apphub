set -e

function __is_pod_ready() {
  [[ "$(kubectl get -n "$1" po "$2" -o 'jsonpath={.status.conditions[?(@.type=="Ready")].status}')" == 'True' ]]
}

function __pods_ready() {
  local pod

  [[ "$#" == 0 ]] && return 0

  for pod in $pods; do
    __is_pod_ready "$namespace" "$pod" || return 1
  done

  return 0
}

function __wait-until-pods-ready() {
  local period interval i pods namespace

  if [[ $# != 3 ]]; then
    echo "Usage: wait-until-pods-ready NAMESPACE PERIOD INTERVAL" >&2
    echo "" >&2
    echo "This script waits for all pods to be ready in the current namespace." >&2

    return 1
  fi

  namespace="$1"
  period="$2"
  interval="$3"


  for ((i=0; i<$period; i+=1)); do
    pods="$(kubectl -n "$namespace" get po -o 'jsonpath={.items[*].metadata.name}')"
    if __pods_ready "$namespace" "$pods"; then
      return 0
    fi

    echo "Waiting for $interval seconds for pods to be ready... Attempt: $i out of $period"
    sleep "$interval"
  done

  echo "Waited for several seconds, but all pods are not ready yet."
  return 1
}

__wait-until-pods-ready $@