# Releasing

## Docker image registry
We use DockerHub to publish our Sling Starter docker image.

## Releasing version
Once we release Sling Starter and create tag, we must prepare the Sling Starter docker image. We 
use GitHub Actions to push our images to DockerHub. From the [GitHub Actions dashboard](https://github.com/apache/sling-org-apache-sling-starter/actions)
we run the `Push Sling Starter Docker images` workflow specifying

- `git_tag` - git tag name e.g `org.apache.sling.starter-13`
- `image_tag` - docker image tag name e.g `13`

to build and push Docker images using codebase from the specified tag.