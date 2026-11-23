pipeline {
    agent any

    stages {
        stage('Checkout Public Repo') {
            steps {
                git branch: 'trunk', url: 'https://github.com/sofmillan/assessmentTournamentsV2-CI.git'
            }
        }
        
        stage('Build bootJar') {
            steps {
                sh '''
                    echo "Build jar"
                    chmod +x gradlew
                    ./gradlew :app:bootJar
                '''
            }
        }

        stage('Move jar file') {
            steps {
                sh '''
                   cp applications/app-service/build/libs/TournamentsProject.jar deployment/
                '''
            }
        }

        stage('Start compose') {
            steps {
                sh 'docker compose down -v'
                sh 'docker compose up -d --build'
            }
        }

        stage('Check containers') {
            steps {
                sleep 20
                sh 'docker compose ps'
            }
        }
        
        stage('Test HTTP GET') {
            steps {
                sleep 10
                sh '''
                    STATUS=$(curl -sS -o /tmp/response.json -w "%{http_code}" \
                      -X GET "http://host.docker.internal:8081/events/tournaments/1" \
                      -H "Content-Type: application/json")
        
                    echo "HTTP Status: $STATUS"
                    cat /tmp/response.json
                    echo ""
        
                    test "$STATUS" -eq 200
                '''
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v --remove-orphans || true'
        }
    }
}
