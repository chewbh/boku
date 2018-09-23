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
}
