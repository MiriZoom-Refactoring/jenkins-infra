# MiriZoom Jenkins + JCasC

Amazon Linux 2023 서버 한 대에서 Jenkins Controller, 이미지 빌드, Ansible 실행을 수행합니다. 서버 생성부터 첫 배포까지는 `../deploy/README.md`를 따릅니다.

- `user-data.sh`: Jenkins EC2 생성 시 User data에 입력합니다. 호스트에 Git, Python 3.11, 초기 설정용 Ansible을 설치합니다.
- 최초 Bootstrap은 Jenkins 서버의 호스트에서 실행합니다. Jenkins는 아직 없어도 됩니다.
- `Dockerfile`: Jenkins 컨테이너에도 Ansible을 포함합니다. 이후 CI/CD에서는 이 Ansible로 App/Web에 배포합니다.
- 따라서 Ansible 실행 위치는 처음부터 Jenkins 서버이며, 사용자 PC에는 Ansible을 설치할 필요가 없습니다.
- `casc/jenkins.yaml`: 관리자, GitHub·Docker Hub·Kakao·EC2 Credentials를 생성합니다.
- `jobs/pipelines.groovy`: CI 4개, Deploy, Batch Scheduler를 생성합니다.
- `compose.yml`: 상태를 named volume에 보관하며 호스트 Docker group 권한을 사용합니다.
- SSH 계정은 `ec2-user`입니다. 동일한 암호 없는 EC2 키를 세 서버에 사용합니다.
- Jenkins JVM은 최대 heap 512 MiB, 실행 슬롯은 1개로 제한합니다. CI가 Deploy를 비동기로 요청해 슬롯 대기를 피합니다.
- 서버 OS는 Amazon Linux지만 Jenkins 컨테이너 이미지는 공식 Jenkins 이미지의 Linux 환경을 사용합니다.

`jenkins.env`의 실제 값은 `deploy/.secrets/`에 입력합니다. Bootstrap이 App/Web private IP, Docker GID, SSH 계정을 채웁니다. UI는 서버의 127.0.0.1:8080에만 열리므로 PC에서 SSH 터널로 접속합니다.

Jenkins/App은 t3.small, Web은 t3.micro가 시작 사양입니다. 이는 2025-07-15 이후 신규 계정의 프리티어 대상 유형이며, 실제 사용은 계정의 무료 플랜 및 남은 크레딧에 따라 결정됩니다. 세 서버와 RDS를 계속 무료로 사용할 수 있다는 보장은 아닙니다. AWS Billing의 Free Tier·Credits를 먼저 확인합니다.

설정 재적용은 호스트의 Ansible로 수행합니다. Bootstrap은 Jenkins를 재시작하므로 실행 중인 Job이 없는 시점에 진행합니다. Jenkins volume과 App/Web의 기존 환경 파일·태그는 유지됩니다. Batch Scheduler는 수동 실행 검증 전까지 비활성입니다.
