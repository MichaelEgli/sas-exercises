# Bookstore Application

This project hosts a Docker Compose configuration to run multiple microservices that simulate a bookstore application.

![Deployment Architecture](deployment.png)

## Prerequisites

- Docker and Docker Compose installed on your system
- GitLab account with access to the BFH container registry
- Personal Access Token with `read_registry` scope

## Setup

### 1. GitLab Container Registry Access

Before you can pull the microservice images, you need to authenticate with the GitLab container registry.

#### Create a Personal Access Token

1. Navigate to the GitLab Personal Access Tokens page:
   - [Create Read Registry Token](https://gitlab.ti.bfh.ch/-/user_settings/personal_access_tokens?name=My+Read+Registry+Token&scopes=read_registry)
2. Set a token name (e.g., "My Read Registry Token")
3. Select the `read_registry` scope
4. Click "Create personal access token"
5. **Important:** Copy the token immediately - you won't be able to see it again!

#### Login to GitLab Container Registry

Set your GitLab username and the token you just created as environment variables, then log in to the registry:

**Linux/macOS/WSL (Bash):**

```bash
# Set your GitLab credentials
GITLAB_USER=<USERNAME>
GITLAB_TOKEN=<TOKEN>

# Login to the GitLab container registry
echo "$GITLAB_TOKEN" | docker login registry.gitlab.ti.bfh.ch -u $GITLAB_USER --password-stdin
```

**Windows (Command Prompt):**

```cmd
REM Set your GitLab credentials
set GITLAB_USER=<USERNAME>
set GITLAB_TOKEN=<TOKEN>

REM Login to the GitLab container registry
echo %GITLAB_TOKEN% | docker login registry.gitlab.ti.bfh.ch -u %GITLAB_USER% --password-stdin
```

**Windows (PowerShell):**

```powershell
# Set your GitLab credentials
$env:GITLAB_USER = "<USERNAME>"
$env:GITLAB_TOKEN = "<TOKEN>"

# Login to the GitLab container registry
$env:GITLAB_TOKEN | docker login registry.gitlab.ti.bfh.ch -u $env:GITLAB_USER --password-stdin
```

Replace `<USERNAME>` with your GitLab username and `<TOKEN>` with your personal access token.

#### Verify Access (Optional)

You can verify your access by pulling a test image:

```bash
# Pull an example image from the registry
docker pull registry.gitlab.ti.bfh.ch/cas-sad-hs25/project/bookstore6/shipping:latest
```

#### Logout (Optional)

When you're done working with the registry, you can log out:

```bash
docker logout registry.gitlab.ti.bfh.ch
```

### 2. Running the Application

Once you're authenticated with the GitLab container registry, you can start all microservices using Docker Compose.

> **Note:** The Docker Compose commands are the same across all platforms (Linux, macOS, and Windows).

#### Start all services

```bash
docker compose up
```

Or run in detached mode (background):

```bash
docker compose up -d
```

#### Stop all services

```bash
docker compose down
```

#### View logs

```bash
# View logs from all services
docker compose logs

# View logs from a specific service
docker compose logs <service-name>

# Follow logs in real-time
docker compose logs -f
```

#### Rebuild and restart services

```bash
# Pull latest images and restart
docker compose pull
docker compose up -d

# Force recreate containers
docker compose up -d --force-recreate
```

### 3. Updating to Latest Images

To pull the latest versions of all microservice images:

```bash
# Pull latest images for all services
docker compose pull

# Restart services with the updated images
docker compose up -d
```

Alternatively, you can do both steps at once:

```bash
# Pull and restart in one command
docker compose pull && docker compose up -d
```

To verify which image versions are currently running:

```bash
# List running containers with their image details
docker compose ps
```

## Troubleshooting

### Authentication Issues

If you encounter authentication errors when pulling images:

1. Verify your token has the `read_registry` scope
2. Check that your token hasn't expired
3. Ensure you're using the correct username and token
4. Try logging out and logging back in:
   ```bash
   docker logout registry.gitlab.ti.bfh.ch
   echo "$GITLAB_TOKEN" | docker login registry.gitlab.ti.bfh.ch -u $GITLAB_USER --password-stdin
   ```

### Container Issues

If containers fail to start:

1. Check the logs: `docker compose logs`
2. Verify all required environment variables are set
3. Ensure no port conflicts with other running services
4. Try removing all containers and volumes, then restart:
   ```bash
   docker compose down -v
   docker compose up
   ```

## Project Structure

```
.
├── docker compose.yml    # Docker Compose configuration (to be added)
└── README.md            # This file
```

## Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [GitLab Container Registry Documentation](https://docs.gitlab.com/ee/user/packages/container_registry/)
- [BFH GitLab](https://gitlab.ti.bfh.ch/)

## License

This project is part of the CAS SAD HS25 course at BFH.

