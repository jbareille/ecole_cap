pipeline {
    agent any
    options {
        // This is required if you want to clean before build
        skipDefaultCheckout(true)
    }
    stages {
        stage('Build') { 
            steps { 
                cleanWs()
                checkout([$class: 'GitSCM', branches: [[name: '*/test']],
                userRemoteConfigs: [[url: 'https://github.com/jbareille/ecole_cap.git']]])
//                userRemoteConfigs: [[url: 'https://github.com/spring-guides/gs-spring-boot.git']]])
                sh 'ls -laR'
                sh 'du -h .'

            }
        }
        stage('Test'){
            steps {
                sh 'pwd'
                sh 'ls -laR'
                sh 'cd spgboot/complete && mvn -B -DskipTests clean package'
            }
        }
        stage('Deploy') {
            steps {
                sh 'ansible localhost -m copy -a "src=/var/lib/jenkins/workspace/test/spgboot/complete/target/spring-boot-complete-0.0.1-SNAPSHOT.jar dest=/var/lib/jenkins/workspace//"'
                sh 'ansible localhost -m shell -a "java -jar /var/lib/jenkins/workspace/test/spring-boot-complete-0.0.1-SNAPSHOT.jar &"'
            }
        }
        stage('Services') {
            steps {
                sh 'sleep 10'
                sh 'curl localhost:8090'
            }
        }
    }
}
