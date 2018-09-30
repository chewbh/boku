#!/usr/bin/env groovy

pipeline {
    agent none

    environment {
        DOCKER_HOST = ''	// remove specifying of docker host, switch to docker.sock
        // GIT_CREDS         = credentials('docker-build')
        JOB_NAME          = "$JOB_NAME"
        BUILD_NUMBER      = "$BUILD_NUMBER"
    }

    stages {

        stage('Run Tests') {
            agent any
            steps {
                sh 'echo $(pwd)'
                sh 'docker info'
                sh 'docker ps'
                sh 'printenv'
                sh "./gradlew test"
            }
        }

        stage('Build Release') {
            when {
                branch 'master'
            }
            agent {
                docker 'zenika/kotlin:1.3-jdk8-alpine'
            }
            steps {
                sh "./gradlew build"
            }
            post {
                always {
                    echo "Build stage complete"
                }
                success {
                    echo "Build succeeded"
                    archiveArtifacts '**/*.class'
                }
                failure {
                    echo "Build has issue. Fix needed"
                }
            }
        }
//        stage("SonarQube Quality Gate") {
//            timeout(time: 1, unit: 'HOURS') {
//                def qg = waitForQualityGate()
//                if (qg.status != 'OK') {
//                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
//                }
//            }
//        }
    }

    options {
        // keep at most 10 builds at a time
        buildDiscarder(logRotator(numToKeepStr: '1', daysToKeepStr: '7'))

        // ensure this build doesn't hang forever with 1 hr timeout
        timeout(time: 60, unit: 'MINUTES')
    }
}
