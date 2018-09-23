#!/usr/bin/env groovy

pipeline {
    agent any
    /* agent {
        label "jenkins-maven"
    } */
    stages {
        stage('Run Tests') {
            steps {
                sh "./gradlew test"
            }
        }
        stage('Build Release') {
            when {
                branch 'master'
            }
            steps {
                sh "./gradlew build"
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
        buildDiscarder(logRotator(numToKeepStr:'10'))

        // ensure this build doesn't hang forever with 1 hr timeout
        timeout(time: 60, unit: 'MINUTES')
    }
}
