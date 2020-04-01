# Experimental CompositeNodeStore setup

This setup is an example of how the CompositeNodeStore can be used with
Apache Sling. It is intended as an example that passes a smoke test, and not
as something that is robust or supported.

## High-level flow

The process used to setup the CompositeNodeStore is the following

1. Create a _composite-seed_ instance that is used to populate the `/libs` and
   `/apps` mounts
1. Copy the resulting segmentstore under a well-known location for the
   _composite-run_ instance so that it can be used as a read-only mount
1. Launch the _composite-run_ instance, which picks up the mount repository and
   uses the Oak `InitialContentMigrator` to populate the default mount as well

## Steps to launch a composite setup

Note 1: all work is done under the `sling` directory, and the `oak.txt` provisioning model
file has some hardcoded paths. These could be removed with some effort.

Note 2: as of 2020-04-01, this depends on the unreleased `org.apache.sling.jcr.oak.server`
bundle at version 1.2.4. You can rebuild it from the tag or download it from
the staging repository at https://repository.apache.org/content/groups/staging/org/apache/sling/org.apache.sling.jcr.oak.server/1.2.4/ .

Prepare the _composite-seed_ instance

```
$ ./create-seed.sh
```

Once Sling has started, shut it down.

Prepare the _composite-run_ directory structure

```
$ ./prepare_composite.sh
```

Launch the composite instance

```
$ ./run_composite.sh
```

You should be running a CompositeNodeStore-backed instance at this point. You can validate this by
accessing Composum and trying to create a node under `/libs` - it will fail.

## Troubleshooting

In addition to verifying the logs, the `explore_repo.sh` script will allow you to inspect the content
of a local SegmentNodeStore. Usage

```
$ ./explore_repo.sh segment_store_dir
# example: ./explore_repo.sh sling/sling-composite/repository-libs/segmentstore-composite-mount-libs/
```