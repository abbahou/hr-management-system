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
        DOCKER_USERNAME = 'dashnet13'
        DOCKER_NAMESPACE = 'dashnet13'
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

        stage('🏗️ Build Backend Services') {
            steps {
                echo "🏗️ Building backend services with Gradle..."
                dir('backend') {
                    sh '''
                        chmod +x gradlew
                        ./gradlew clean build -x test --parallel
                    '''
                }
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
            // 'branch' directive only works in Multibranch Pipelines.
            // For regular Pipeline jobs, check GIT_BRANCH env variable instead.
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' }
            }
            steps {
                echo "📤 Pushing Docker images to Docker Hub..."
                sh '''
                    . $WORKSPACE/build.properties
                    BUILD_TAG="${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                    
                    # Securely login to Docker Hub using the account username and token
                    echo $DOCKER_PASSWORD_PSW | docker login -u $DOCKER_PASSWORD_USR --password-stdin
                    
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