@echo off
setlocal enabledelayedexpansion

REM Build and Deploy Script for Task Management Application (Windows)

echo 🚀 Starting deployment process...

REM Configuration
set APP_NAME=taskmanagement
set PROFILE=%1
if "%PROFILE%"=="" set PROFILE=dev

echo 📦 Building application for %PROFILE% environment...

REM Clean and build the application
call mvn clean package -DskipTests

if !errorlevel! neq 0 (
    echo ❌ Maven build failed!
    exit /b 1
)

echo ✅ Maven build successful!

REM Check if JAR file exists
set JAR_FILE=target\taskmanagment-0.0.1-SNAPSHOT.jar
if not exist "%JAR_FILE%" (
    echo ❌ JAR file not found: %JAR_FILE%
    exit /b 1
)

echo 🐳 Building Docker image...

REM Build Docker image
docker build -t %APP_NAME%:latest .

if !errorlevel! neq 0 (
    echo ❌ Docker build failed!
    exit /b 1
)

echo ✅ Docker image built successfully!

REM Deploy based on profile
if "%PROFILE%"=="dev" (
    echo 🔧 Deploying to development environment...
    docker-compose --env-file .env.dev down
    docker-compose --env-file .env.dev up -d
) else if "%PROFILE%"=="prod" (
    echo 🔧 Deploying to production environment...
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod down
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d
) else (
    echo ❌ Unknown profile: %PROFILE%. Use 'dev' or 'prod'
    exit /b 1
)

if !errorlevel! neq 0 (
    echo ❌ Docker deployment failed!
    exit /b 1
)

echo ✅ Deployment successful!

REM Wait for services to be ready
echo ⏳ Waiting for services to be ready...
timeout /t 30 /nobreak > nul

REM Health check
echo 🔍 Performing health check...
for /l %%i in (1,1,10) do (
    curl -f http://localhost:8080/health >nul 2>&1
    if !errorlevel! equ 0 (
        echo ✅ Application is healthy!
        goto :health_check_passed
    )
    echo ⏳ Attempt %%i/10 - waiting for application to start...
    timeout /t 10 /nobreak > nul
)

echo ❌ Health check failed after 10 attempts
echo 📋 Container logs:
docker-compose logs app
exit /b 1

:health_check_passed
echo 🎉 Deployment completed successfully!
echo 📍 Application is running at: http://localhost:8080
echo 📍 API endpoints available at: http://localhost:8080/api

REM Show running containers
echo 📋 Running containers:
docker-compose ps

endlocal
