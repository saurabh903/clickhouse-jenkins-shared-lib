def call() {
    pipeline {
        agent any

        stages {
            stage('Load Config') {
                steps {
                    script {
                        def props = libraryResource('deployClickhouse/config.properties')
                        def config = readProperties text: props

                        SLACK_CHANNEL_NAME  = config['SLACK_CHANNEL_NAME']
                        ENVIRONMENT         = config['ENVIRONMENT']
                        CODE_BASE_PATH      = config['CODE_BASE_PATH']
                        ACTION_MESSAGE      = config['ACTION_MESSAGE']
                        KEEP_APPROVAL_STAGE = config['KEEP_APPROVAL_STAGE'].toBoolean()
                    }
                }
            }

            stage('Clone Repo') {
                steps {
                    echo "Cloning repo to path: ${CODE_BASE_PATH}"
                    // simulate git clone
                }
            }

            stage('User Approval') {
                when {
                    expression { return KEEP_APPROVAL_STAGE }
                }
                steps {
                    timeout(time: 2, unit: 'MINUTES') {
                        input message: "Do you want to deploy to ${ENVIRONMENT}?"
                    }
                }
            }

            stage('Execute Playbook') {
                steps {
                    echo "Executing Ansible playbook for ClickHouse in environment: ${ENVIRONMENT}"
                    // ansible-playbook command here
                }
            }

            stage('Notification') {
                steps {
                    echo "Sending notification to Slack: ${SLACK_CHANNEL_NAME}"
                    echo "${ACTION_MESSAGE}"
                    // You can integrate actual slackSend if configured
                }
            }
        }

        post {
            failure {
                echo 'Deployment failed.'
            }
            success {
                echo 'Deployment successful.'
            }
        }
    }
}
