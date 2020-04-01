#!/bin/sh -ex
composite_libs_mount=sling/sling-composite/repository-libs/segmentstore-composite-mount-libs
rm -rf sling/sling-composite
mkdir -p ${composite_libs_mount}
cp -R sling/sling-seed/repository-libs/segmentstore/* ${composite_libs_mount}
