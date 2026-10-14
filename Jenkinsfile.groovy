Groovy


pipeline {
    agent any

    environment {
        APP_NAME = 'simple-journey-app'
        IMAGE_TAG = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'go test -v ./...'
            }
        }

        stage('Build Image') {
            steps {
                script {
                    docker.build("${env.APP_NAME}:${env.IMAGE_TAG}", "--build-arg VERSION=${env.IMAGE_TAG} .")
                }
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh "docker tag ${env.APP_NAME}:${env.IMAGE_TAG} myregistry/${env.APP_NAME}:${env.IMAGE_TAG}"
                    sh "docker push myregistry/${env.APP_NAME}:${env.IMAGE_TAG}"
                }
            }
        }

        stage('Deploy') {
            steps {
                // Memicu mekanisme hotfix / swap binary secara otomatis
                sh "echo 'Deploying version ${env.IMAGE_TAG} via binary swap...'"
                // Contoh perintah real di server target via SSH/Docker socket:
                // sh "CGO_ENABLED=0 go build -ldflags=\"-X main.version=${env.IMAGE_TAG}\" -o myapp ."
                // sh "docker cp myapp app-container:/root/myapp"
                // sh "docker restart app-container"
            }
        }
    }

    post {
        failure {
            echo 'Pipeline gagal! Pastikan tes lulus dan konfigurasi benar.'
        }
    }
}