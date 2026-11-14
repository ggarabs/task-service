pipeline {
    agent any

    parameters {
        booleanParam(name: 'DEPLOY_TO_AWS', defaultValue: false, description: 'Cria/inicia a EC2 e publica o task-service.')
    }

    environment {
        AWS_DEFAULT_REGION = 'us-east-1'
        AWS_PAGER = ''
        SOURCE_DIR = '/workspace/task-service'
    }

    stages {
        stage('Validar acesso AWS') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                    string(credentialsId: 'aws-session-token', variable: 'AWS_SESSION_TOKEN')
                ]) { sh 'aws sts get-caller-identity' }
            }
        }

        stage('Compilar e testar') {
            steps {
                sh 'docker run --rm --volumes-from "$HOSTNAME" -w "$SOURCE_DIR" maven:3.9-eclipse-temurin-21 mvn -B verify'
            }
        }

        stage('Garantir EC2') {
            when { expression { params.DEPLOY_TO_AWS } }
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                    string(credentialsId: 'aws-session-token', variable: 'AWS_SESSION_TOKEN')
                ]) {
                    sh '''
                        USER_SERVICE_URL="http://$(/workspace/jenkins/scripts/service-ip.sh user-service private):8080"
                        test "$USER_SERVICE_URL" != 'http://None:8080'
                        /workspace/jenkins/scripts/ensure-ec2.sh task-service > .ec2.env
                        printf 'USER_SERVICE_URL=%s\n' "$USER_SERVICE_URL" >> .ec2.env
                    '''
                }
            }
        }

        stage('Publicar') {
            when { expression { params.DEPLOY_TO_AWS } }
            steps {
                sshagent(credentials: ['ec2-deploy-key']) {
                    sh '. ./.ec2.env && cd "$SOURCE_DIR" && /workspace/jenkins/scripts/deploy-compose.sh task-service "$PUBLIC_IP" "http://localhost:3000" "$USER_SERVICE_URL"'
                }
            }
        }
    }
}
