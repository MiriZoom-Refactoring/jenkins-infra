FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends git openssh-client docker-cli python3 python3-venv \
    && rm -rf /var/lib/apt/lists/*
COPY ansible-requirements.txt /tmp/ansible-requirements.txt
RUN python3 -m venv /opt/ansible \
    && /opt/ansible/bin/pip install --no-cache-dir -r /tmp/ansible-requirements.txt
ENV PATH="/opt/ansible/bin:${PATH}"
ENV ANSIBLE_COLLECTIONS_PATH=/opt/ansible/collections
COPY collections.yml /tmp/collections.yml
RUN ansible-galaxy collection install -r /tmp/collections.yml -p /opt/ansible/collections
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
RUN apt-get update \
    && apt-get install -y --no-install-recommends docker-buildx \
    && rm -rf /var/lib/apt/lists/*
USER jenkins
