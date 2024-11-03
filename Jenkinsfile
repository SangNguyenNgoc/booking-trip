pipeline {
    agent any

    environment {
        DEPLOY_SERVER = '167.71.192.132'
        DEPLOY_PATH = '/home/work'
        SSH_CREDENTIALS_ID = 'booking-trip-service-key'
        GITHUB_REPO = 'https://github.com/SangNguyenNgoc/booking-trip.git'
    }

    stages {
        stage('Deploy Application') {
            steps {
                sshagent([SSH_CREDENTIALS_ID]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no root@${DEPLOY_SERVER} '
                            # 1: Truy cập vào thư mục làm việc và xóa clone cũ nếu có
                            cd ${DEPLOY_PATH} &&
                            rm -rf cloned_repo &&
                            
                            # 2: Clone mã nguồn từ GitHub
                            git clone ${GITHUB_REPO} cloned_repo &&
                            
                            # 3: Copy file .env vào thư mục vừa clone
                            cd cloned_repo &&
                            cp ../.env . &&
                            
                            # 4: Chạy docker-compose để build và deploy
                            docker compose -f service.yml up -d --build &&
                            
                            # 5: Xóa thư mục clone sau khi deploy xong
                            cd ${DEPLOY_PATH} &&
                            rm -rf cloned_repo
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
