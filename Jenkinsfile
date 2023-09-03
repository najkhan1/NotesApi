pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                sh "/opt/sbt/bin/sbt compile"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                sh "/opt/sbt/bin/sbt test"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging..."
                sh "/opt/sbt/bin/sbt package"
            }
        }

    }
    post {
        always {
            junit '**/target/test-reports/TEST-*.xml'
        }
        success {
            archiveArtifacts 'target/scala-*/*.jar'
        }
    }

}