def call() {
    pipeline {
        agent any

        environment {
            SLACK_CHANNEL_NAME  = credentials('SLACK_CHANNEL_NAME') // if secret, or load via props file
            ENVIRONMENT         = ''
            CODE_BASE_PATH      = ''
            ACTION_MESSAGE      = ''
            KEEP_APPROVAL_STAGE = ''
        }

        stages {
            stage('Read Config') {
                steps {
                    script {
                        def props = readProperties file: "resources/deployClickhouse/config.properties"
                        ENVIRONMENT         = props['ENVIRONMENT']
                        CODE_BASE_PATH      = props['CODE_BASE_PATH']
                        ACTION_MESSAGE      = props['ACTION_MESSAGE']
                        KEEP_APPROVAL_STAGE = props['KEEP_APPROVAL_STAGE']
                        SLACK_CHANNEL_NAME  = props['SLACK_CHANNEL_NAME']
                    }
                }
            }

            stage('Clone Repo') {
                steps {
                    git url: 'https://github.com/your-org/your-clickhouse-repo.git', branch: "${ENVIRONMENT}"
                }
            }

            stage('User Approval') {
                when {
                    expression { return KEEP_APPROVAL_STAGE.toBoolean() }
                }
                steps {
                    input message: "Do you want to deploy to ${ENVIRONMENT}?"
                }
            }

            stage('Run Ansible Playbook') {
                steps {
                    sh 'ansible-playbook deploy-clickhouse.yml -i inventory/${ENVIRONMENT}/hosts'
                }
            }

            stage('Send Notification') {
                steps {
                    echo "Sending Slack notification to channel: ${SLACK_CHANNEL_NAME}"
                    echo "Message: ${ACTION_MESSAGE}"
                    // Optional: Add real Slack integration if needed
                }
            }
        }

        post {
            success {
                echo "Deployment completed successfully."
            }
            failure {
                echo "Deployment failed."
            }
        }
    }
}
