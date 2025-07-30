#!/bin/bash

# Build and Deploy Script for Task Management Application

set -e

echo "🚀 Starting deployment process..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="taskmanagement"
PROFILE=${1:-dev}  # Default to dev if no profile specified

echo -e "${YELLOW}📦 Building application for ${PROFILE} environment...${NC}"

# Clean and build the application
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Maven build failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Maven build successful!${NC}"

# Check if JAR file exists
JAR_FILE="target/taskmanagment-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}❌ JAR file not found: $JAR_FILE${NC}"
    exit 1
fi

echo -e "${YELLOW}🐳 Building Docker image...${NC}"

# Build Docker image
docker build -t ${APP_NAME}:latest .

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Docker build failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker image built successfully!${NC}"

# Deploy based on profile
case $PROFILE in
    "dev")
        echo -e "${YELLOW}🔧 Deploying to development environment...${NC}"
        docker-compose --env-file .env.dev down
        docker-compose --env-file .env.dev up -d
        ;;
    "prod")
        echo -e "${YELLOW}🔧 Deploying to production environment...${NC}"
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod down
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d
        ;;
    *)
        echo -e "${RED}❌ Unknown profile: $PROFILE. Use 'dev' or 'prod'${NC}"
        exit 1
        ;;
esac

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Docker deployment failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Deployment successful!${NC}"

# Wait for services to be ready
echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
sleep 30

# Health check
echo -e "${YELLOW}🔍 Performing health check...${NC}"
for i in {1..10}; do
    if curl -f http://localhost:8080/health &>/dev/null; then
        echo -e "${GREEN}✅ Application is healthy!${NC}"
        break
    fi
    if [ $i -eq 10 ]; then
        echo -e "${RED}❌ Health check failed after 10 attempts${NC}"
        echo -e "${YELLOW}📋 Container logs:${NC}"
        docker-compose logs app
        exit 1
    fi
    echo -e "${YELLOW}⏳ Attempt $i/10 - waiting for application to start...${NC}"
    sleep 10
done

echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo -e "${YELLOW}📍 Application is running at: http://localhost:8080${NC}"
echo -e "${YELLOW}📍 API endpoints available at: http://localhost:8080/api${NC}"

# Show running containers
echo -e "${YELLOW}📋 Running containers:${NC}"
docker-compose ps
