# Task Management Application - Docker Deployment

This document provides instructions for deploying the Task Management application using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+ 
- Docker Compose 2.0+
- Maven 3.6+
- Java 9+ (for local development)

## Project Structure

```
taskmanagment/
├── src/                          # Application source code
├── target/                       # Maven build output
├── init-scripts/                 # Database initialization scripts
├── Dockerfile                    # Docker image definition
├── docker-compose.yml           # Main Docker Compose configuration
├── docker-compose.prod.yml      # Production overrides
├── nginx.conf                   # Nginx reverse proxy configuration
├── .env.dev                     # Development environment variables
├── .env.prod                    # Production environment variables
├── deploy.sh                    # Linux/Mac deployment script
├── deploy.bat                   # Windows deployment script
└── .dockerignore                # Docker ignore file
```

## Environment Profiles

### Development Profile (`application.properties`)
- Uses H2 in-memory database
- Port: 8081
- Debug logging enabled

### Production Profile (`application-prod.properties`)
- Uses MySQL database
- Port: 8080
- Optimized logging and performance settings
- Database connection pooling
- Security headers

## Quick Start

### Development Environment

1. **Build and run with development profile:**
   ```bash
   # Linux/Mac
   ./deploy.sh dev
   
   # Windows
   deploy.bat dev
   ```

2. **Manual deployment:**
   ```bash
   mvn clean package -DskipTests
   docker-compose --env-file .env.dev up -d
   ```

### Production Environment

1. **Build and run with production profile:**
   ```bash
   # Linux/Mac
   ./deploy.sh prod
   
   # Windows
   deploy.bat prod
   ```

2. **Manual deployment:**
   ```bash
   mvn clean package -DskipTests
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d
   ```

## Services

### Application Service (`app`)
- **Port:** 8080
- **Health Check:** `http://localhost:8080/health`
- **API Base:** `http://localhost:8080/api`

### Database Service (`db`)
- **Type:** MySQL 8.0
- **Port:** 3306
- **Database:** `taskmanagement_db`
- **Default User:** `taskuser` / `taskpass`

### Reverse Proxy (`nginx`)
- **HTTP Port:** 80
- **HTTPS Port:** 443
- **Features:** SSL termination, rate limiting, compression

## API Endpoints

- `POST /api/tasks` - Create a new task
- `PUT /api/tasks` - Update a task
- `DELETE /api/tasks` - Delete multiple tasks
- `DELETE /api/tasks/{id}` - Delete a single task
- `GET /api/tasks/search` - Search tasks by description

- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/{userId}/tasks` - Get tasks for a user

## Environment Variables

### Database Configuration
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `MYSQL_ROOT_PASSWORD` - MySQL root password
- `MYSQL_DATABASE` - Database name

### Application Configuration
- `SPRING_PROFILES_ACTIVE` - Active Spring profile (dev/prod)
- `JAVA_OPTS` - JVM options for the application

## Monitoring and Logs

### Application Logs
```bash
# View application logs
docker-compose logs app

# Follow logs in real-time
docker-compose logs -f app
```

### Database Logs
```bash
# View database logs
docker-compose logs db
```

### Health Checks
```bash
# Check application health
curl http://localhost:8080/health

# Check all services status
docker-compose ps
```

## Scaling

### Scale Application Instances
```bash
# Scale to 3 application instances
docker-compose up -d --scale app=3
```

### Resource Limits (Production)
- **App Container:** 1.5GB RAM, 1 CPU
- **DB Container:** 1GB RAM, 0.5 CPU
- **Nginx Container:** 256MB RAM, 0.25 CPU

## Security Features

### Production Security
- Non-root user in containers
- SSL/TLS encryption (Nginx)
- Security headers (X-Frame-Options, X-XSS-Protection, etc.)
- Rate limiting (10 requests/second)
- Database secrets management

### SSL Configuration
Place your SSL certificates in the `ssl/` directory:
```
ssl/
├── server.crt
└── server.key
```

## Backup and Recovery

### Database Backup
```bash
# Create database backup
docker exec taskmanagement_db mysqldump -u root -p taskmanagement_db > backup.sql

# Restore database backup
docker exec -i taskmanagement_db mysql -u root -p taskmanagement_db < backup.sql
```

### Volume Backup
```bash
# Backup persistent volumes
docker run --rm -v taskmanagment_db_data:/data -v $(pwd):/backup alpine tar czf /backup/db_backup.tar.gz /data
```

## Troubleshooting

### Common Issues

1. **Port conflicts:**
   ```bash
   # Check what's using port 8080
   netstat -tulpn | grep 8080
   ```

2. **Database connection issues:**
   ```bash
   # Check database connectivity
   docker-compose exec app nc -zv db 3306
   ```

3. **Memory issues:**
   ```bash
   # Check container memory usage
   docker stats
   ```

### Reset Environment
```bash
# Stop and remove all containers, networks, and volumes
docker-compose down -v
docker system prune -f

# Rebuild and restart
./deploy.sh dev
```

## Development

### Local Development (without Docker)
```bash
# Run with H2 database
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run tests
mvn test

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

## Production Deployment Checklist

- [ ] Update production passwords in `.env.prod`
- [ ] Configure SSL certificates
- [ ] Set up log rotation
- [ ] Configure monitoring
- [ ] Set up automated backups
- [ ] Configure firewall rules
- [ ] Update resource limits
- [ ] Test health checks
- [ ] Verify security headers
- [ ] Test disaster recovery

## Support

For issues and questions, please check:
1. Container logs: `docker-compose logs`
2. Application health: `curl http://localhost:8080/health`
3. Database connectivity: `docker-compose exec app nc -zv db 3306`

## License

This project is licensed under the MIT License.
