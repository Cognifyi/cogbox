# GitHub Actions Secrets Configuration

This document lists all the secrets required for GitHub Actions workflows to function properly in the Cogbox repository.

## Required Secrets

### Docker Registry

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `DOCKER_TOKEN` | Docker Hub authentication token for pushing Docker images | `release.yaml`, `default_image_publish.yaml`, `pr_docker_build.yaml` |

### SDK Publishing

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `NPM_TOKEN` | NPM authentication token for publishing TypeScript packages to npm registry | `sdk_publish.yaml` |
| `PYPI_TOKEN` | PyPI authentication token for publishing Python packages to PyPI | `sdk_publish.yaml` |
| `RUBYGEMS_API_KEY` | RubyGems API key for publishing Ruby gems to RubyGems.org | `sdk_publish.yaml` |
| `MAVEN_USERNAME` | Maven Central username for publishing Java packages | `sdk_publish.yaml` |
| `MAVEN_PASSWORD` | Maven Central password for publishing Java packages | `sdk_publish.yaml` |
| `MAVEN_GPG_SIGNING_KEY` | GPG signing key for signing Java packages (base64 encoded) | `sdk_publish.yaml` |
| `MAVEN_GPG_SIGNING_PASSWORD` | GPG signing key password for Java packages | `sdk_publish.yaml` |

### GitHub Bot

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `GITHUBBOT_TOKEN` | GitHub bot token for triggering external repository workflows (e.g., Homebrew tap) | `sdk_publish.yaml` |

### NX Cloud

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `NX_CLOUD_ACCESS_TOKEN` | Nx Cloud access token for distributed caching and remote execution | `release.yaml`, `pr_checks.yaml`, `e2e_pr_tests.yaml`, `sdk_publish.yaml`, `prepare-release.yaml` |

### Documentation Search

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `PUBLIC_ALGOLIA_APP_ID` | Algolia application ID for documentation search | `release.yaml` |
| `PUBLIC_ALGOLIA_API_KEY` | Algolia API key for documentation search | `release.yaml` |
| `PUBLIC_ALGOLIA_CLI_INDEX_NAME` | Algolia index name for CLI documentation search | `release.yaml` |
| `PUBLIC_ALGOLIA_SDK_INDEX_NAME` | Algolia index name for SDK documentation search | `release.yaml` |
| `PUBLIC_ALGOLIA_API_INDEX_NAME` | Algolia index name for API documentation search | `release.yaml` |
| `PUBLIC_DOCSEARCH_APP_ID` | DocSearch application ID for documentation search | `release.yaml` |
| `PUBLIC_DOCSEARCH_API_KEY` | DocSearch API key for documentation search | `release.yaml` |
| `PUBLIC_DOCSEARCH_INDEX_NAME` | DocSearch index name for documentation search | `release.yaml` |
| `PUBLIC_DOCSEARCH_ASSISTANT_ID` | DocSearch assistant ID for documentation search | `release.yaml` |
| `PUBLIC_WEB_URL` | Public web URL for documentation | `release.yaml` |

### Translation

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `GT_API_KEY` | General Translation API key for documentation translation | `translate.yaml` |
| `GT_PROJECT_ID` | General Translation project ID for documentation translation | `translate.yaml` |

### Optional Secrets

| Secret Name | Description | Used By Workflows |
|-------------|-------------|-------------------|
| `DAYTONA_API_URL` | Daytona API URL for release builds (only if needed) | `release.yaml` |
| `DAYTONA_AUTH0_DOMAIN` | Auth0 domain for release builds (only if needed) | `release.yaml` |
| `DAYTONA_AUTH0_CLIENT_ID` | Auth0 client ID for release builds (only if needed) | `release.yaml` |
| `DAYTONA_AUTH0_CALLBACK_PORT` | Auth0 callback port for release builds (only if needed) | `release.yaml` |
| `DAYTONA_AUTH0_CLIENT_SECRET` | Auth0 client secret for release builds (only if needed) | `release.yaml` |
| `DAYTONA_AUTH0_AUDIENCE` | Auth0 audience for release builds (only if needed) | `release.yaml` |

## Setup Instructions

### 1. Docker Hub Token

1. Create a Docker Hub account at https://hub.docker.com/
2. Generate an access token at https://hub.docker.com/settings/security
3. Add the token as `DOCKER_TOKEN` secret in GitHub repository settings

### 2. NPM Token

1. Log in to npmjs.com
2. Generate an automation token at https://www.npmjs.com/settings/tokens
3. Add the token as `NPM_TOKEN` secret in GitHub repository settings

### 3. PyPI Token

1. Log in to pypi.org
2. Generate an API token at https://pypi.org/manage/account/token/
3. Add the token as `PYPI_TOKEN` secret in GitHub repository settings

### 4. RubyGems API Key

1. Log in to rubygems.org
2. Go to https://rubygems.org/profile/api_keys
3. Create a new API key with MFA enabled
4. Add the API key as `RUBYGEMS_API_KEY` secret in GitHub repository settings

### 5. Maven Central Credentials

1. Create a Sonatype account at https://central.sonatype.com/
2. Generate a user token and password
3. Generate a GPG key pair: `gpg --gen-key`
4. Export the public key and upload to key server
5. Export the private key (base64 encoded): `gpg --export-secret-keys YOUR_KEY_ID | base64`
6. Add the following secrets:
   - `MAVEN_USERNAME`: Your Sonatype username
   - `MAVEN_PASSWORD`: Your Sonatype user token
   - `MAVEN_GPG_SIGNING_KEY`: Base64 encoded private key
   - `MAVEN_GPG_SIGNING_PASSWORD`: Your GPG key password

### 6. GitHub Bot Token

1. Create a personal access token with `repo` scope
2. Add the token as `GITHUBBOT_TOKEN` secret in GitHub repository settings

### 7. Nx Cloud Token

1. Sign up for Nx Cloud at https://nx.app/
2. Generate an access token
3. Add the token as `NX_CLOUD_ACCESS_TOKEN` secret in GitHub repository settings

### 8. Algolia & DocSearch

1. Create an Algolia account at https://www.algolia.com/
2. Create an application and indices for documentation search
3. Add the credentials as secrets in GitHub repository settings

### 9. General Translation

1. Sign up for General Translation at https://generaltranslation.com/
2. Create a project and get API credentials
3. Add the credentials as secrets in GitHub repository settings

## Workflow-Specific Notes

### Release Workflow
The `release.yaml` workflow requires most secrets as it builds and publishes all artifacts (Docker images, SDKs, etc.).

### SDK Publish Workflow
The `sdk_publish.yaml` workflow requires all SDK publishing secrets (NPM, PyPI, RubyGems, Maven).

### PR Checks Workflow
The `pr_checks.yaml` workflow only requires `NX_CLOUD_ACCESS_TOKEN` for distributed caching.

### E2E Tests Workflow
The `e2e_pr_tests.yaml` workflow only requires `NX_CLOUD_ACCESS_TOKEN` for distributed caching.

### Docker Build Workflow
The `pr_docker_build.yaml` workflow only requires `DOCKER_TOKEN` for PR builds.

## Testing Without All Secrets

For development purposes, you can:
1. Comment out the secrets that aren't needed
2. Use workflow dispatch with only the secrets you have configured
3. Skip publishing steps by modifying the workflow conditions

## Security Notes

- Never commit secrets to the repository
- Use different tokens for different environments if needed
- Rotate tokens regularly
- Enable MFA where possible (RubyGems requires it)
- Use least-privilege access tokens
