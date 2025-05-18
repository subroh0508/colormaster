#!/bin/sh

set -e

rm -f /app/data/color_master.db
litestream restore -if-replica-exists -config /etc/litestream.yml /app/data/color_master.db
litestream replicate -exec "java -jar /app/colormaster.jar" -config /etc/litestream.yml
