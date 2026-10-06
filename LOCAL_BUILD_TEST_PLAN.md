# Local Build Test Plan for Cogbox

## Environment Requirements

This project uses **Nix flakes** for reproducible development environments. Before running any build commands, ensure Nix is installed:

```bash
# Install Nix with flakes enabled
curl -L https://nixos.org/nix/install | sh

# Enable flakes in ~/.config/nix/nix.conf
echo "experimental-features = nix-command flakes" >> ~/.config/nix/nix.conf
```

## Build Test Script

### 1. Go Modules Build Test

```bash
# Enter Go dev shell
nix develop .#go

# Sync workspace and build all Go modules
go work sync
go build ./...

# Test individual modules
go build ./apps/cli/...
go build ./apps/daemon/...
go build ./apps/runner/...
go build ./libs/sdk-go/...
go build ./libs/api-client-go/...
go build ./libs/toolbox-api-client-go/...
```

**Expected Results:**
- All Go modules should compile without errors
- No missing dependencies
- go.work.sum should be up to date

### 2. Node.js/TypeScript Build Test

```bash
# Enter Node.js dev shell
nix develop .#node

# Install dependencies
yarn install

# Build all projects (development)
yarn build

# Build all projects (production)
yarn build:production

# Build specific projects
npx nx build api
npx nx build dashboard
npx nx build sdk-typescript
```

**Expected Results:**
- All TypeScript projects should compile
- Output in dist/ directories
- No TypeScript errors
- API client generation should work

### 3. Python Packages Build Test

```bash
# Enter Python dev shell
nix develop .#python

# Install dependencies
poetry install

# Build Python packages
cd libs/sdk-python && poetry build
cd libs/api-client-python && poetry build
cd libs/toolbox-api-client-python && poetry build
```

**Expected Results:**
- Python wheels/tarballs should be generated
- No build errors
- Packages can be installed locally

### 4. Ruby Gems Build Test

```bash
# Enter Ruby dev shell
nix develop .#ruby

# Install dependencies
bundle install

# Build Ruby gems
cd libs/sdk-ruby && gem build cogbox.gemspec
cd libs/api-client-ruby && gem build cogbox_api_client.gemspec
cd libs/toolbox-api-client-ruby && gem build cogbox_toolbox_api_client.gemspec
```

**Expected Results:**
- .gem files should be generated
- Gems can be installed locally with `gem install`
- No build errors

### 5. Java Projects Build Test

```bash
# Enter Java dev shell
nix develop .#java

# Build Java SDK
cd libs/sdk-java && ./gradlew build

# Build API clients
cd libs/api-client-java && ./gradlew build
cd libs/toolbox-api-client-java && ./gradlew build

# Build examples
cd examples/java/exec-command && ./gradlew build
```

**Expected Results:**
- All Java projects should compile
- JAR files should be generated
- Tests should pass

### 6. Docker Image Build Test

```bash
# Enter default shell (all tools)
nix develop

# Build Docker images (production)
yarn docker:production

# Build specific Docker targets
npx nx docker api
npx nx build runner --configuration=production
npx nx docker daemon
```

**Expected Results:**
- Docker images should be built successfully
- Images can be tagged and pushed to registry
- No build context errors

### 7. API Client Generation Test

```bash
# Enter default shell
nix develop

# Generate API clients
yarn generate:api-client
```

**Expected Results:**
- API clients should be generated for all languages
- OpenAPI specs should be processed correctly
- Generated code should compile

### 8. Unit Tests

```bash
# Enter default shell
nix develop

# Run all tests
yarn test

# Run specific language tests
nix develop .#go --command bash -c "go test ./libs/sdk-go/..."
nix develop .#python --command bash -c "cd libs/sdk-python && poetry run pytest"
nix develop .#java --command bash -c "cd libs/sdk-java && ./gradlew test"
```

**Expected Results:**
- All unit tests should pass
- No test failures
- Coverage reports generated

### 9. Linting

```bash
# Enter default shell
nix develop

# Lint all languages
yarn lint

# Lint TypeScript
yarn lint:ts

# Lint Python
yarn lint:py

# Lint Go
nix develop .#go --command bash -c "golangci-lint run ./apps/cli/..."
```

**Expected Results:**
- No linting errors
- Code follows project style guidelines
- Format issues fixed automatically

### 10. E2E Tests

```bash
# Enter default shell
nix develop

# Run E2E tests
yarn test:e2e
```

**Expected Results:**
- E2E tests should pass
- Services start correctly
- API and runner integration works

## Migration-Specific Validation

After the cogbox migration, verify:

### 1. Package Names
- TypeScript: `@cognifyi/sdk`, `@cognifyi/api-client`, etc.
- Python: `cogbox`, `cogbox_api_client`, etc.
- Ruby: `cogbox`, `cogbox_api_client`, etc.
- Java: `io.cognifyi:sdk`, `io.cognifyi:api-client`, etc.
- Go: `github.com/Cognifyi/cogbox/libs/sdk-go`

### 2. Import Paths
- Check generated code uses new package names
- Verify cross-language dependencies reference correct packages

### 3. Repository URLs
- All README.md and documentation should reference Cognifyi/cogbox
- Package.json files should have correct repository URLs

### 4. Docker Images
- Images should be tagged with `${{ github.repository_owner }}` instead of `daytonaio`
- Image names should be dynamic based on repository

## Known Issues After Migration

### Module Path Conflicts
After renaming packages, you may encounter:
- Import statement errors in generated code
- Dependency resolution issues
- Package name conflicts during local testing

**Resolution:**
- Regenerate API clients with `yarn generate:api-client`
- Clear caches: `yarn cache clean`, `poetry cache purge`, etc.
- Update local package installations

### Go Module References
Go work sync may fail if:
- Module paths are inconsistent
- go.sum files are out of date

**Resolution:**
```bash
nix develop .#go --command bash -c "go work sync && touch go.work.sum"
```

### Java Package References
Gradle may fail to resolve dependencies if:
- Maven local repository has old versions
- Project coordinates are not updated

**Resolution:**
```bash
nix develop .#java --command bash -c "cd libs/api-client-java && ./gradlew publishToMavenLocal -x test"
```

## CI/CD Validation

After pushing changes, monitor GitHub Actions:

1. **PR Checks Workflow** (`pr_checks.yaml`)
   - Should pass all linting checks
   - Should build all projects
   - Should run tests

2. **Docker Build Workflow** (`pr_docker_build.yaml`)
   - Should build Docker images
   - Should not require Docker Hub token for PRs

3. **Release Workflow** (`release.yaml`)
   - Requires all secrets to be configured
   - Should build and publish all artifacts

## Rollback Plan

If migration breaks critical functionality:

1. **Revert branch**
   ```bash
   git checkout main
   git branch -D feature/migrate-to-cogbox
   ```

2. **Fix specific issues**
   - Address individual module build failures
   - Regenerate problematic code
   - Update references incrementally

3. **Test incrementally**
   - Fix one language at a time
   - Test each fix before proceeding
   - Commit and push changes separately

## Recommended Testing Order

1. **Go modules** (simplest, fewest dependencies)
2. **Node.js/TypeScript** (core platform)
3. **Python packages** (SDK clients)
4. **Ruby gems** (SDK clients)
5. **Java projects** (SDK clients)
6. **Docker images** (integration)
7. **Full build** (end-to-end)
8. **Tests** (validation)

## Success Criteria

- All language SDKs build successfully
- Docker images build and can be tagged
- Unit tests pass
- Linting passes
- Package names are correctly updated to cogbox branding
- Repository URLs are updated to Cognifyi/cogbox
- GitHub Actions workflows run successfully (with secrets configured)
