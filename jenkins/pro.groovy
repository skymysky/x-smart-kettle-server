    //使用这种方式构建,需要k8s环境,一般生产环境使用此
    def label = "mydev-${UUID.randomUUID().toString()}"
    def img_version = '${img_version}'
    def project_name = "springboot-sharding-pro"
    def img_name = project_name
    def package_name = "yaukie"
    def git_address = "github.com/"+package_name+"/"+project_name+".git"
    def setting_name = "settings"
    def repo_url = "registry.cn-hangzhou.aliyuncs.com"
    def user_name = "yaukie@163.com"
    def pass_word = "wst123456"

    podTemplate(cloud: 'kubernetes', namespace: 'yaukie_dev_ops', label: label, serviceAccount: 'bald-quetzal-ibm-jenkins',
            containers: [
                    containerTemplate(name: 'git', image: 'gitlab/gitlab-runner:latest', ttyEnabled: true, command: 'cat', args: ''),
                    containerTemplate(name: 'maven', image: 'maven:latest', ttyEnabled: true, command: 'cat', args: ''),
//                    containerTemplate(name: 'jnlp', image: "jnlpwithssh:latest", ttyEnabled: true, command: '', args: '${computer.jnlpmac} ${computer.name}'),
                    containerTemplate(name: 'docker', image: 'library/docker:latest', ttyEnabled: true, command: 'cat', args: '')
                        ],
            imagePullSecrets: ['artifactory-lambo'],
            volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]
        )
            {


                node {
                    stage('git-clone'){
                        echo '准备拉取源码..... '
                        container('git'){
                            sh 'git clone https://yaukie:yaukie@163.com@'+git_address +' && git -C ' +project_name + ' show HEAD '
                        }
                        echo '项目'+project_name+'拉取成功..... '
                    }
                    stage('mvn-build'){
                        echo '准备构建项目...... '
                        container('maven'){
                            sh 'mvn clean install -f ' + project_name + '/pom.xml -U -Dmaven.frame.skip=true -s settings/' + setting_name + '.xml'
                        }
                        echo '项目'+project_name+'构建成功..... '
                    }

                    stage('docker-build'){
                        echo '准备制作镜像,并推送至阿里云..... '
                        container('docker'){
                            sh 'sudo docker login -u '+user_name+ '-p '+pass_word+' registry.cn-hangzhou.aliyuncs.com'
                            sh 'cp ' + project_name + '/target/*.jar ' +project_name + '/docker'
                            def img_url = repo_url + '/'+ package_name+'/'+project_name+':'+img_version
                            sh 'cd ' + project_name + '/docker && docker build -t '+ img_url + ' .'
                            sh 'docker push '+ img_url
                        }
                        echo '成功将镜像'+img_name+'推送至阿里云..... '
                    }
                }

            }


