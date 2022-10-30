[ "$(whoami)" != "root" ] && exec sudo -- "$0" "$@"

chmod +x "./tools/control-center.sh"
chmod +x "./java/jre/bin/java"
chmod +x "./tools/policy-validator/node"
if [ "$1" = "-s" ]; then
  "./tools/control-center.sh" -start "$2" -with-version-check
else
  "./tools/control-center.sh" -start -ui -with-version-check
fi
