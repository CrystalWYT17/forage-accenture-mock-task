pipeline{
    agent any
    stages {
        stage('Build'){
            steps{
                echo 'Building...'
                script {
                    sh './gradlew assemble'
                }
            }
        }
        stage('Test'){
            steps{
                echo 'Testing...'
                script {
                    sh './gradlew test'
                }
            }
        }
    }
}