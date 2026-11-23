pipeline {
    agent any

    stages {
        stage('Checkout Public Repo') {
            steps {
                git branch: 'trunk', url: 'https://github.com/sofmillan/assessmentTournamentsV2-CI.git'
            }
        }
        
        stage('Build BootJar') {
            steps {
                // For a multi-module project, use ':your-app-module:bootJar' (or './gradlew bootJar' for root/single-module)
                sh '''
                    chmod +x gradlew
                    ./gradlew :app:bootJar
                '''
            }
        }

        stage('Move Artifact') {
            steps {
                sh '''
                   cp applications/app-service/build/libs/TournamentsProject.jar deployment/
                '''
            }
        }

        stage('Start Compose Stack') {
            steps {
                // Adjust -f path if your compose file is in a subdirectory, e.g. compose/docker-compose.yml
                sh 'docker compose down -v'
                sh 'docker compose up -d --build'
            }
        }

        stage('Check Services') {
            steps {
                sleep 25
                sh 'docker compose ps'
            }
        }
        
        stage('Test HTTP POST') {
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
            // Optional: tear down containers/networks after test runs
            sh 'docker compose down -v --remove-orphans || true'
        }
    }
}
