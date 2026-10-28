def requiredEnvironment = { String name ->
    String value = System.getenv(name)

    if (!value) {
        throw new IllegalStateException("${name} is required")
    }

    value
}

def backendRepositoryUrl = requiredEnvironment('BACKEND_REPOSITORY_URL')
def frontendRepositoryUrl = requiredEnvironment('FRONTEND_REPOSITORY_URL')
def fastApiRepositoryUrl = requiredEnvironment('FASTAPI_REPOSITORY_URL')
def deployRepositoryUrl = requiredEnvironment('DEPLOY_REPOSITORY_URL')

def pipelines = [
    [
        name: 'mirizoom-backend-ci',
        description: 'MiriZoom Backend CI/CD Pipeline',
        repositoryUrl: backendRepositoryUrl,
        scriptPath: 'Jenkinsfile'
    ],
    [
        name: 'mirizoom-batch-ci',
        description: 'MiriZoom Batch CI Pipeline',
        repositoryUrl: backendRepositoryUrl,
        scriptPath: 'Jenkinsfile.batch'
    ],
    [
        name: 'mirizoom-frontend-ci',
        description: 'MiriZoom Frontend CI/CD Pipeline',
        repositoryUrl: frontendRepositoryUrl,
        scriptPath: 'Jenkinsfile'
    ],
    [
        name: 'mirizoom-fastapi-ci',
        description: 'MiriZoom FastAPI CI/CD Pipeline',
        repositoryUrl: fastApiRepositoryUrl,
        scriptPath: 'Jenkinsfile'
    ],
    [
        name: 'mirizoom-deploy',
        description: 'MiriZoom Ansible deployment pipeline',
        repositoryUrl: deployRepositoryUrl,
        scriptPath: 'Jenkinsfile'
    ],
    [
        name: 'mirizoom-batch-scheduler',
        description: 'Weekly law batch: enable after initial deployment',
        repositoryUrl: deployRepositoryUrl,
        scriptPath: 'Jenkinsfile.batch-scheduler'
    ]
]

pipelines.each { pipeline ->
    pipelineJob(pipeline.name) {
        description(pipeline.description)
        if (pipeline.name == 'mirizoom-batch-scheduler') {
            disabled(System.getenv('ENABLE_BATCH_SCHEDULE') != 'true')
        }

        logRotator {
            numToKeep(20)
            artifactNumToKeep(10)
        }

        properties {
            disableConcurrentBuilds()
        }

        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(pipeline.repositoryUrl)
                            credentials('github-credentials')
                        }

                        branch('*/develop')
                    }
                }

                scriptPath(pipeline.scriptPath)
                lightweight(true)
            }
        }
    }
}
