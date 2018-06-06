pipeline {
  // Use Jenkins Maven slave
  // Jenkins will dynamically provision this as OpenShift Pod
  // All the stages and steps of this Pipeline will be executed on this Pod
  // After Pipeline completes the Pod is killed so every run will have clean
  // workspace
  agent {
    label 'maven'
  }

  // Pipeline Stages start here
  // Requeres at least one stage
  stages {
    //def nexusurl = "http://nexus-cicd.192.168.99.100.nip.io/repository/lm-approved/"

    // Checkout source code
    // This is required as Pipeline code is originally checkedout to
    // Jenkins Master but this will also pull this same code to this slave
    stage('Git Checkout') {
      steps {
        // Turn off Git's SSL cert check, uncomment if needed
        // sh 'git config --global http.sslVerify false'
        git url: "${APPLICATION_SOURCE_REPO}"
        //print "GIT URL:${APPLICATION_SOURCE_REPO}" 
      }
    }

    // Run Maven build, skipping tests
  stage('Scan') {
     steps {

        //sh "mvn package"
        //hub_detect hubParams + '--detect.project.name="Redhat Mike Java 1" '
    
        print "USING DIR: ${CONTEXT_DIR}"
        sh "ls -lrt ${CONTEXT_DIR}"  
    
        sh "mkdir ./scanreports"
        dir("${CONTEXT_DIR}")
        {
          hub_detect '--blackduck.hub.url="https://redhathub.blackducksoftware.com" \
            --blackduck.hub.api.token="NDM2ODEwN2MtMWZkMC00MTAwLTgyNDItMzViMGY1ZDQ2YzdkOjM4OTVlMTA0LTk3ZjMtNDEzYS05ZjdiLWExYjhkNjgwYWY0Mg==" \
            --detect.project.name="${ARTIFACT_NAME}" \
            --detect.policy.check.fail.on.severities=BLOCKER,CRITICAL --detect.risk.report.pdf=true \
            --detect.risk.report.pdf.path="./scanreports/" \
            --blackduck.hub.trust.cert=true'

          sh 'pwd'
          sh 'ls -lrt'
          sh 'find . -name "*.pdf" > repfilepath'
          archiveArtifacts(artifacts: '../**/scanreports/**')
       }
     }
  
   }

   stage('Verify Report') 
   {
      steps {
        input( message: "Approve ${ARTIFACT_NAME}?")
      }
   }

   stage('Push to Nexus')
   {
      steps {
        script {
          dir ("${CONTEXT_DIR}")
          {
            def nexusurl = "http://nexus-cicd.192.168.99.100.nip.io/repository/lm-approved/"
            def todaysdate = new Date()
            uploadPath = todaysdate.format("YYYY/MM/dd/HH-mm-ss");
            print uploadPath
            reportPath = readFile('repfilepath').trim()
            print "rep:" + reportPath
            sh "curl -k -u admin:admin123 -X PUT " + nexusurl + uploadPath + "/report.pdf" + " -T " + reportPath
        
            sh "zip ${ARTIFACT_NAME}.zip -r ."
            sh "curl -k -u admin:admin123 -X PUT " + nexusurl + uploadPath + "/${ARTIFACT_NAME}.zip" + " -T ${ARTIFACT_NAME}.zip" 
          }
        }
      }
   }

 }
}
