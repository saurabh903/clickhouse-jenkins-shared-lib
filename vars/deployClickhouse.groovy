def call() {
    pipeline {
        agent any

        stages {
            stage('Deploy ClickHouse') {
                steps {
                    script {
                        def config = readProperties file: 'resources/deployClickhouse/config.properties'
                        echo "Slack Channel: ${config.SLACK_CHANNEL_NAME}"
                        echo "Environment: ${config.ENVIRONMENT}"
                    }
                }
            }

            stage('Clone Repo') {
                steps {
                    echo 'Cloning repo...'
                    // Simulate clone logic
                }
            }

            stage('User Approval') {
                steps {
                    timeout(time: 5, unit: 'MINUTES') {
                        input message: 'Do you want to proceed with deployment?'
                    }
                }
            }

            stage('Run Playbook') {
                steps {
                    echo 'Running Ansible playbook...'
                    // Run your Ansible playbook here
                }
            }

            stage('Notify') {
                steps {
                    echo "Sending notification to Slack channel: ${config.SLACK_CHANNEL_NAME}"
                    // Slack notification logic
                }
            }
        }
    }
}
