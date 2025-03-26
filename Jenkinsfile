pipeline {
    agent any

    environment {
        SONAR_SCANNER = "${tool 'SonarQube Scanner'}/bin/SonarQube Scanner"
    }

    tools {
        terraform 'Terraform'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mohay22/task8.git'
            }
        }

        stage('Setup JDK & Gradle') {
            steps {
                script {
                    def javaHome = tool name: 'JDK17', type: 'jdk'
                    env.JAVA_HOME = javaHome
                    if (isUnix()) {
                        env.PATH = "${javaHome}/bin:${env.PATH}"
                    } else {
                        env.PATH = "${javaHome}\\bin;${env.PATH}"
                    }
                }
            }
        }

        stage('Terraform Init') {
            steps {
                dir('terraform') {
                    bat 'terraform init'
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                dir('terraform') {
                    bat 'terraform plan'
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                dir('terraform') {
                    bat 'terraform apply -auto-approve'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew clean assembleDebug'
                    } else {
                        bat 'gradlew.bat clean assembleDebug'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew testDebugUnitTest'
                    } else {
                        bat 'gradlew.bat testDebugUnitTest'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sqp_c4da967e1f93a1ad93248e9f9e7408521da8b552', variable: 'SONAR_LOGIN')]) {
                        script {
                            echo "The SonarQube token is: ${SONAR_LOGIN}"
                            if (isUnix()) {
                                sh "./gradlew sonarqube -Dsonar.login=$SONAR_LOGIN"
                            } else {
                                bat "gradlew.bat sonarqube -Dsonar.login=%SONAR_LOGIN%"
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy step - You can add Firebase or other distribution steps here'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/testDebugUnitTest/*.xml'
            archiveArtifacts artifacts: 'app/build/outputs/**/*.apk', fingerprint: true
        }
        success {
            echo '✅ Build & Tests Passed Successfully!'
        }
        failure {
            echo '❌ Build or Tests Failed. Check logs for details.'
        }
    }
}