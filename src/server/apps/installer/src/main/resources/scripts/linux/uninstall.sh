[ "$(whoami)" != "root" ] && exec sudo -- "$0" "$@"
chmod +x "./tools/control-center.sh"

read -p "Do you want to uninstall NextLabs Control Center (y - Yes / n - No)? " -r
if [[ "$REPLY" == "y" ]] || [[ "$REPLY" == "Y" ]] || [[ "$REPLY" == "yes" ]]; then
  "./tools/control-center.sh" -uninstall
  if [ -f "action-cc-delete.txt" ]; then
    NEXTLABS_CC_DIRECTORY=$(<"action-cc-delete.txt")
    rm -f "action-cc-delete.txt"
    if [ ! -z "$NEXTLABS_CC_DIRECTORY" ]; then
      echo
      echo "Before deleting the Control Center directory, verify the path to ensure that the directory is not shared with another instance of Control Center."
      echo
      read -p "Do you want to delete the directory $NEXTLABS_CC_DIRECTORY (y - Yes / n - No)? " -r
      if [[ "$REPLY" == "y" ]] || [[ "$REPLY" == "Y" ]] || [[ "$REPLY" == "yes" ]]; then
        cd "$NEXTLABS_CC_DIRECTORY/.."
        rm -rf "$NEXTLABS_CC_DIRECTORY"
        echo "Control Center directory has deleted."
      fi
    fi
  fi
fi
