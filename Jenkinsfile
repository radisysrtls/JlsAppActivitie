pipeline {
     parameters {
      booleanParam(defaultValue: true, description: 'Execute pipeline?', name: 'shouldBuild')
   }
   
   
  agent {
    node {
      label 'cord_ci'
    }
    
  }
  stages {

 stage('Checkout') {
      steps {
		script{  
        try {
			echo 'checkout scm'
			checkout scm
            sh '''echo "Initial configuration before starting analysis.. "
					 '''
			}catch(err) {
        		this.notifyStash('FAILED')
                throw err
  			  		    } 
			
			  }
			}
			
		}



    stage('Java-PMD-Analysis') {
      steps {

                    script{
                        try {
                                        echo 'Running PMD analysis '
                                        sh 'bash tools/jenkins-rules/findbugs.sh ${WORKSPACE}'
                                        sh 'rm -rf  pom.xml app.xml'
					pmd unstableTotalAll:'0' , pattern: '*.xml'

                                           }catch(err) {
                        this.notifyStash('FAILED')
                throw err
                                           }
                                 }

      }
    }

	

	
	stage('Copyright check') {
      steps {
		script{  
        try {
			echo 'Checking Copyright for Apache,sterlite and Radisys'
			sh '''echo "Checking copyright..."
			rm -rf  ${WORKSPACE}/README.md
			bash tools/jenkins-rules/copyright.sh ${WORKSPACE} 
					
					 '''
			}catch(err) {
        		this.notifyStash('FAILED')
                throw err
  			  		    } 
			
			  }
			}
			
		}




	 stage('UNIT_TESTS') {
      steps {
                script{
        try {
                        echo ''
                        sh '''echo "Running Unit test suite ..."
		        echo "UT scripts to be added once ready."
			#nosetests
                                         '''
                        }catch(err) {
                        this.notifyStash('FAILED')
                throw err
                                            }

                          }
                        }

                }

	
	
  }
post {
 always {
		script{
		emailextrecipients([[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']])
	 	def emailBody = "This is the build ${env.JOB_NAME}  ${env.BUILD_NUMBER} ${env.BUILD_STATUS}  RESULT: ${currentBuild.result} ${env.BUILD_URL}  "  
     		def emailSubject = "${env.JOB_NAME} - Build# ${env.BUILD_NUMBER} - ${currentBuild.result}"
     		emailext(
         	mimeType: 'text/html',
         	replyTo: 'releasedept@radisys.com',
        	 subject: emailSubject, 
         	to: '$DEFAULT_RECIPIENTS',
         	attachmentsPattern: '*.xml', attachLog: true,
         	body: emailBody,
         	recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        
         )
		 } ////script
    } //always
} //post  
  
}


def notifyStash(String state) {

    if('SUCCESS' == state || 'FAILED' == state) {
        currentBuild.result = state         // Set result of currentBuild !Important! else build results will not get reflected in UI
    }

    step([$class: 'StashNotifier',
          //commitSha1: "$commit",            // jenkins parameter that resolves to commit's hash 
          credentialsId: 'a8846e97-ed9e-4cbd-bb96-359ea1a41d49',
          disableInprogressNotification: false,
          ignoreUnverifiedSSLPeer: true,
          includeBuildNumberInKey: false,
          prependParentProjectKey: false,
          projectKey: '',
          stashServerBaseUrl: 'https://alm.radisys.com/bitbucket'])
}

def findJiraIssue(){
	def changeLogSets = currentBuild.changeSets
	def jiraIssues = []
	for (int i = 0; i < changeLogSets.size(); i++) {
		def entries = changeLogSets[i].items
		for (int j = 0; j < entries.length; j++) {
			def entry = entries[j]
			//echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
			def files = new ArrayList(entry.affectedFiles)
				for (int k = 0; k < files.size(); k++) {
					def file = files[k]
					def list1 = entry.msg.split(' ') 
						for (int l = 0; l < list1.size(); l++) {
                            def b = (list1[l] ==~ /[A-Z]+-\d+/) 
							if(b){
								//println('Exact match!')
								//println list1[l]
								jiraIssues.add(list1[l])
							}
							else{
								println('Not exact match!')
							}                     
						}
				}
        
		}
	}
	return jiraIssues
}


