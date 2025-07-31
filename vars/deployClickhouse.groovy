def call(String configPath = "resources/deployClickhouse/config.properties") {
    def utils = new org.example.Utils(this)
    def config = utils.loadConfig(configPath)

    stage('Clone Repo') {
        checkout scm
    }

    if (config.KEEP_APPROVAL_STAGE?.toBoolean()) {
        stage('User Approval') {
            input message: "Do you want to proceed with ${config.ENVIRONMENT} deployment?", ok: "Yes"
        }
    }

    stage('Run Ansible Playbook') {
        sh """
            ansible-playbook ${config.CODE_BASE_PATH}/clickhouse-deploy.yml -e env=${config.ENVIRONMENT}
        """
    }

    stage('Notify') {
        slackSend(channel: "#${config.SLACK_CHANNEL_NAME}", message: "${config.ACTION_MESSAGE}")
    }
}

