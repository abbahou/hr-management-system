pipeline {
    agent any

    triggers {
        // Enable GitHub webhook-triggered builds
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }

    environment {
        DOCKER_REGISTRY = 'docker.io'
        // Your specific Docker Hub username/registry name
        DOCKER_NAMESPACE = 'hr_managemt' 
        // The credential ID for your Docker Hub access token
        DOCKER_PASSWORD = credentials('dockerhubtoken') 
        GITHUB_REPO = 'https://github.com/abbahou/hr-management-system.git'
    }

    stages {
        stage('📦 Checkout Code') {
            steps {
                echo "🔍 Checking out main branch from: ${GITHUB_REPO}"
                checkout([
                    $class: 'GitSCM',
                    // UPDATED: Now only looking at the main branch
                    branches: [[name: '*/main']], 
                    extensions: [
                        [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false]
                    ],
                    userRemoteConfigs: [[
                        credentialsId: 'githubtoken', 
                        url: "${GITHUB_REPO}"
                    ]]
                ])
                sh '''
                    # Save the short commit hash to tag the Docker images uniquely
                    GIT_COMMIT_SHORT=$(git rev-parse --short HEAD)
                    echo "GIT_COMMIT_SHORT=${GIT_COMMIT_SHORT}" >> $WORKSPACE/build.properties
                '''
            }
        }

        stage('🐳 Docker - Build Images') {
            steps {
                echo "🐳 Building Docker images for HR microservices..."
                sh '''
                    . $WORKSPACE/build.properties
                    BUILD_TAG="${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                    
                    echo "Building IAM Service (Tag: ${BUILD_TAG})..."
                    docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-iam-service:${BUILD_TAG} -f backend/iam-service/Dockerfile backend/
                        
                    echo "Building Recruitment Service..."
                    docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-recruitment-service:${BUILD_TAG} -f backend/recruitment-service/Dockerfile backend/
                        
                    echo "Building Employee Service..."
                    docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-employee-service:${BUILD_TAG} -f backend/employee-service/Dockerfile backend/
                        
                    echo "Building API Gateway..."
                    docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-api-gateway:${BUILD_TAG} -f backend/api-gateway/Dockerfile backend/
                        
                    echo "Building Frontend..."
                    docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-frontend:${BUILD_TAG} -f frontend/Dockerfile frontend/
                '''
            }
        }

        stage('📤 Docker - Push Images') {
            // This ensures pushing only happens on main (redundant now, but good for safety)
            when {
                branch 'main'
            }
            steps {
                echo "📤 Pushing Docker images to Docker Hub..."
                sh '''
                    . $WORKSPACE/build.properties
                    BUILD_TAG="${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                    
                    # Securely login to Docker Hub using the namespace and token
                    echo $DOCKER_PASSWORD | docker login -u ${DOCKER_NAMESPACE} --password-stdin
                    
                    echo "Pushing images..."
                    docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-iam-service:${BUILD_TAG}
                    docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-recruitment-service:${BUILD_TAG}
                    docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-employee-service:${BUILD_TAG}
                    docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-api-gateway:${BUILD_TAG}
                    docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/hr-frontend:${BUILD_TAG}
                    
                    # Logout to clear credentials
                    docker logout
                '''
            }
        }
    }

    post {
        always {
            echo "🧹 Cleaning workspace..."
            cleanWs()
        }
        success {
            echo "✅ Images successfully built and pushed to Docker Hub!"
        }
        failure {
            echo "❌ Pipeline failed - check the console output."
        }
    }
}